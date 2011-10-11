package org.androino.prototype;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final Handler mHandler;

	private int mVolume = 0; //
	private ArduinoService mArduinoS = null;

	public MainActivity() {
		this.mHandler = new Handler() {
			public void handleMessage(Message msg) {
				messageReceived(msg);
				dummyMessageReceived(msg);
			}

		};
	}

	private void dummyMessageReceived(Message msg){
		switch (msg.what) {
		case ArduinoService.HANDLER_MSG_RECEIVED:
			String str1 = Integer.toHexString(msg.arg1 & 0xFF);
			Log.i("MainActivity:handleMessage", str1);
			Toast.makeText(getApplicationContext(), "handle message:" + msg.arg1, Toast.LENGTH_SHORT).show();
			break;
		case ArduinoService.HANDLER_MSG_STOPPED:
			Toast.makeText(getApplicationContext(), "STOP message:" + msg.arg1, Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	
	private void messageReceived(Message msg){
		switch (msg.what) {
		case ArduinoService.HANDLER_MSG_RECEIVED:
			String str1 = Integer.toHexString(msg.arg1 & 0xFF);
			Log.i("MainActivity:handleMessage", str1);
			Toast.makeText(getApplicationContext(), "handle message:" + msg.arg1, Toast.LENGTH_SHORT).show();
			break;
		case ArduinoService.HANDLER_MSG_STOPPED:
			Toast.makeText(getApplicationContext(), "STOP message:" + msg.arg1, Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		Log.i("MainActivity:lifecycle", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final Button button = (Button) findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	sendMessage("1");        
            }
        });
		final Button button2 = (Button) findViewById(R.id.stop_button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mArduinoS.stopAndClean();
            }
        });
        
		// INITIALIZE Arduino service
		//this.mArduinoS = new ArduinoService(this.mHandler);
		this.mArduinoS = new AudioArduinoService(this.mHandler);
	}

	protected void onPause() {
		Log.i("MainActivity:lifecycle", "onPause");
		super.onPause();

		// STOP the Arduino service
		//if (this.mArduinoS != null) this.mArduinoS.stop();

//		// restore volume
//		AudioManager localAudioManager = (AudioManager) getSystemService("audio");
//		localAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//				this.mVolume, 0);
	}

	protected void onResume() {
		Log.i("MainActivity:lifecycle", "onResume");
		super.onResume();

//		// setting max volume
//		AudioManager localAudioManager = (AudioManager) getSystemService("audio");
//		int maxVolume = localAudioManager
//				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//		int currentVolume = localAudioManager.getStreamVolume(3);
//		this.mVolume = currentVolume;
//		localAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
//				0);

		// START the arduino service
		new Thread( this.mArduinoS).start();
	}

	private void sendMessage(String message){
		this.mArduinoS.write(message);
		
	}
	
}