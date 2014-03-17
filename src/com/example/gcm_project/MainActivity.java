package com.example.gcm_project;

import static com.example.gcm_project.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.gcm_project.CommonUtilities.EXTRA_MESSAGE;
import static com.example.gcm_project.CommonUtilities.SENDER_ID;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {
	GoogleCloudMessaging gcm;
	private Context context;
	private TextView tvRegisterMsg;
	private String strRegId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvRegisterMsg = (TextView)findViewById(R.id.action_settings);
		context = getApplicationContext();
		gcm = GoogleCloudMessaging.getInstance(this);
		setGCM_RegID();
	}

	// get GCM Reg ID and Save to our server (use ServerUtilities.java)
	public void setGCM_RegID() {

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		// register with Google.
		new AsyncTask<Void, String, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					strRegId = gcm.register(SENDER_ID);
					msg = "Device registered, registration id=" + strRegId;

					// send id to our server
					boolean registered = ServerUtilities.register(context,
							strRegId);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// tvRegisterMsg.append(msg + "\n");
			}

		}.execute(null, null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mHandleMessageReceiver); // 在Activity
		// 消滅時才unregister
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		private final static String MY_MESSAGE = "com.stu.phonebook.DISPLAY_MESSAGE";

		@Override
		public void onReceive(Context context, Intent intent) {

			if (MY_MESSAGE.equals(intent.getAction())) {
				final String newMessage = intent.getExtras().getString(
						EXTRA_MESSAGE);
				 tvRegisterMsg.setText(newMessage);
				
			}
		}
	};

}
