package com.example.tutorialandroidsocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.tutorialandroidsocket.Adapters.FileAdapter;
import com.example.tutorialandroidsocket.Socket.MessageReceivedListener;
import com.example.tutorialandroidsocket.Socket.TCPClient;
import com.example.tutorialandroidsocket.Socket.TCPCommands;
import com.example.tutorialandroidsocket.Socket.Classes.BaseSocketObject;
import com.example.tutorialandroidsocket.Socket.Classes.FileData;
import com.example.tutorialandroidsocket.Socket.Classes.FolderData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements MessageReceivedListener, OnClickListener, OnItemClickListener {

	public static String TAG = "MainActivity";
	FileOutputStream fileOutputStream;
	ArrayList<FileData> filesOnServer;
	ArrayList<String> folderStruc = new ArrayList<String>(Arrays.asList("root")) ;
	FileData fileDownload;
	
	TCPClient client;
	FileAdapter fileAdapt;
	ListView lvFiles;
	Button btnConnect;
	ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnConnect = (Button) findViewById(R.id.btnConnect);
		lvFiles = (ListView) findViewById(R.id.lvFiles);
		
		lvFiles.setOnItemClickListener(this);
		btnConnect.setOnClickListener(this);
		
		client = new TCPClient(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (client != null && client.isConnected())
			client.Disconnect();
	}
	
	/**
	 * Get files from server
	 */
	private void GetFilesFromServer() {
		//Show loading window
		showLoadingDialog();
		//Create the request and send it to the server
		BaseSocketObject bso = new BaseSocketObject();
		bso.request = TCPCommands.CMD_REQUEST_FILES;
		bso.data = new FolderData(Utils.createFolderPathFromArray(folderStruc));
		client.WriteCommand(bso.toJSON());
	}
	
	/**
	 * Show dialog for when loading data from server
	 */
	private void showLoadingDialog() {
		MainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Resources re = getResources();
				progressDialog = ProgressDialog.show(MainActivity.this, 
						re.getText(R.string.dialog_loading_title),
						re.getText(R.string.dialog_loading_msg), true);
			} 
		});
	}
	
	/**
	 * Show dialog for file transfer progress
	 * @param value
	 * @param max
	 * @param fileName
	 */
	private void showTransferringDialog(final int max, final String fileName) {
		MainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Resources re = getResources();
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setTitle(R.string.dialog_transfer_file_title);
				progressDialog.setMessage(re.getString(R.string.dialog_transfer_file_msg, fileName));
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setIndeterminate(false);
				progressDialog.setProgress(0);
				progressDialog.setMax(max);
				//Hide the numbers.
				progressDialog.setProgressNumberFormat(null);
				progressDialog.show();
			} 
		});
	}
	
	/**
	 * Dismiss the progress dialog if possible
	 * We use this as we access the same ProgressDialog variable for 2 purposes
	 * and this function reduces code
	 */
	private void dismissProgressDialog() {
		MainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing())
					progressDialog.dismiss();
			} 
		});
	}
	
	/**
	 * Show dialog to confirm file transfer
	 */
	private void showTransferConfirmationDialog() {
		Resources re = getResources();
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		alertDialog.setTitle(R.string.dialog_file_confirm_title);
		alertDialog.setMessage(re.getString(R.string.dialog_file_confirm, fileDownload.fileName));
		
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, re.getString(R.string.dialog_option_no),
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	//Nope, transfer not confirmed. Just cloes this dialog
		            dialog.dismiss();
		        }
		    });
		
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, re.getString(R.string.dialog_option_yes),
			new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		//Yep, transfer confirmed. Request the server to send the file.
	    			BaseSocketObject bso = new BaseSocketObject();
	    			bso.request = TCPCommands.CMD_REQUEST_FILE_DOWNLOAD;
	    			bso.data = new FileData(fileDownload.Path);
	    			client.WriteCommand(bso.toJSON());
	    			//Close the dialog
	    			dialog.dismiss();
	        	}
	    	});
		
		alertDialog.show();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == btnConnect.getId()) {
			//Connect to the server
			client.Connect("192.168.0.4", 7462);
			btnConnect.setEnabled(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		FileData fd = (FileData) fileAdapt.getItem(position);
		
		if (fd.Type == 1) {
			//Download file
			fileDownload = fd;
			//AlertDialog. are you sure you want to download this file..
			showTransferConfirmationDialog();
		} else if (fd.Type == 2) {
			//Add folder to structure
			folderStruc.add(fd.fileName);
			//Get file list from new path
			GetFilesFromServer();
		} else if (fd.Type == -1) {
			//Remove last folder
			if (folderStruc.size() < 2)
				return;
			folderStruc.remove(folderStruc.size() -1);
			//Get file list from new path
			GetFilesFromServer();
		}
	}

	@Override
	public void OnMessageReceived(String msg) {
		try {
			JSONObject base =  new JSONObject(msg);
			String request = base.getString("Request");
			if (request.equals(TCPCommands.CMD_REQUEST_FILES_RESPONSE)) {
				//Here we parse the list of files from the server
				JSONObject jData = base.getJSONObject("Data"); 
				JSONArray jFiles = jData.getJSONArray("allFiles");
				if (filesOnServer != null)
					filesOnServer.clear();
				
				filesOnServer = new ArrayList<FileData>();
				
				if (folderStruc.size() > 1) //If this is not the root folder then
					filesOnServer.add(new FileData("Go back", "", -1, -1)); //Go back
				
				//Add all files to a list
				for (int i = 0; i < jFiles.length(); i++) {
					JSONObject obj = (JSONObject) jFiles.get(i);
					filesOnServer.add(new FileData(
							obj.getString("FileName"),
							obj.getString("Path"),
							obj.getLong("Size"),
							obj.getInt("Type")));
				}
				
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//Create a new adapter with the files
						fileAdapt = new FileAdapter(MainActivity.this, 0, filesOnServer);
						//Set the adapter to the listview
						lvFiles.setAdapter(fileAdapt);
						//Dismiss loading dialog
						dismissProgressDialog();
					} });
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnFileIncoming(int length) {
		try {
			Log.e(TAG, "File incoming");
			//Show progress dialog
			showTransferringDialog(length, fileDownload.fileName);
			//Create and open a filestream
			fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), fileDownload.fileName), false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnFileDataReceived(byte[] data, int read, int length,final int downloaded) {
		try {
			//We write the new data to the filestream
			fileOutputStream.write(data, 0, read);
			if (progressDialog != null && progressDialog.isShowing()) {
				//Then we update the progress
				progressDialog.setProgress(downloaded);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnFileComplete() {
		try {
			//We close the filestream
			fileOutputStream.flush();
			fileOutputStream.close();
			dismissProgressDialog();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void OnConnectionError() {
		Log.e(TAG, "Something went wrong");
		btnConnect.setEnabled(true);
	}
	
	@Override
	public void OnConnectSuccess() {
		//Get files from server
		GetFilesFromServer();
	}

}
