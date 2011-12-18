package org.androino.ttt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	private static final String TAG = "MainActivity";
	private TicTacToe mTTT;
	
	public MainActivity() {
		this.mTTT = new TicTacToe(this);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final Button button = (Button) findViewById(R.id.Button);
		final RadioButton radio = (RadioButton) findViewById(R.id.RadioButton01);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (radio.isChecked()){
					mTTT.stop();
				} else 
					mTTT.start();
			}
		});
		
	}

	protected void onPause() {
		Log.i("MainActivity:lifecycle", "onPause");
		super.onPause();

		//this.mTTT.stop();
		// restore volume
	}

	protected void onResume() {
		Log.i("MainActivity:lifecycle", "onResume");
		super.onResume();

		//this.mTTT.start();
		// setting max volume

	}
	void showDebugMessage(String message, boolean showToast){
		try {
			if (showToast){
				Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			} else {
				TextView txt = (TextView) findViewById(R.id.Text);
				txt.setText(message);
			}
		} catch (Exception e) {
			Log.e(TAG, "ERROR showDebugMessage()=" + message, e);
		}
	}


}