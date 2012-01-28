/*		
* Copyright (C) 2011 Androino authors		
*		
* Licensed under the Apache License, Version 2.0 (the "License");		
* you may not use this file except in compliance with the License.		
* You may obtain a copy of the License at		
*		
*      http://www.apache.org/licenses/LICENSE-2.0		
*		
* Unless required by applicable law or agreed to in writing, software		
* distributed under the License is distributed on an "AS IS" BASIS,		
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.		
* See the License for the specific language governing permissions and		
* limitations under the License.		
*/

package org.androino.term;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		final Button button = (Button) findViewById(R.id.Button);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startStop();
			}
		});
		final Button sendB = (Button) findViewById(R.id.SendButton);
		sendB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					TextView txt = (TextView) findViewById(R.id.NumberText);
					int number = Integer.parseInt(""+txt.getText());
					showDebugMessage("Send " + number, true);
				} catch (Exception e) {
					showDebugMessage("ERROR happened, check number format",true);
				}
			}
		});
    }

    private void startStop(){
		RadioButton radio = (RadioButton) findViewById(R.id.RadioButton01);
		if (radio.isChecked()){
			// stop
			showDebugMessage("Service stoped", true);
		} else { 
			// start
			showDebugMessage("Service started", true);
		}
		// update UI
		radio.setChecked(! radio.isChecked());
    }
    
    
	void showDebugMessage(String message, boolean showToast){
		try {
			if (showToast){
				Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			} else {
				TextView txt = (TextView) findViewById(R.id.DebugText);
				String info = txt.getText().toString();
				if (info.length()> 300)
					info = info.substring(0, 30);
				info = message + "\n" + info;
				txt.setText(info);
			}
		} catch (Exception e) {
			Log.e(TAG, "ERROR showDebugMessage()=" + message, e);
		}
	}


}