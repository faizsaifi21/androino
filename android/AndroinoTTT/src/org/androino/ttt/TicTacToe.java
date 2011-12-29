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

package org.androino.ttt;

import org.androino.tttserver.client.TTTEvent;
import org.androino.tttserver.client.TTTServer;
import org.androino.tttserver.client.iTTTEventListener;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RadioButton;

public class TicTacToe implements iTTTEventListener{

	private static final String TAG = "TicTacToe";
	// arduino messages
	// [0-9] error codes
	// [10-19] specific messages
	// [20-30] button events
	
	public static final int ARDUINO_MSG_START_GAME 		= 10;
	public static final int ARDUINO_MSG_END_GAME_WINNER = 11;
	public static final int ARDUINO_MSG_END_GAME_LOSER 	= 12;

	public static final int HANDLER_MESSAGE_FROM_SERVER = 2001;

	private Handler mHandler;
	private ArduinoService mArduinoS;
	private TTTServer mServer;
	private MainActivity mActivity;

	public TicTacToe(MainActivity activity){
		this.mActivity = activity;
		// 
		this.mHandler = new Handler() {
			public void handleMessage(Message msg) {
				messageReceived(msg);
			}

		};
	}
	public void stop(){
		Log.i(TAG, "stop()");
		((RadioButton) this.mActivity.findViewById(R.id.RadioButton01)).setChecked(false);
		
		// STOP the arduino service
		if (this.mArduinoS != null)
			this.mArduinoS.stopAndClean();
		// DISCONNECT from server
		if (this.mServer != null)
			this.mServer.stop();
	}

	public void start(){
		Log.i(TAG, "start()");
		((RadioButton) this.mActivity.findViewById(R.id.RadioButton01)).setChecked(true);

		// START the arduino service
		this.mArduinoS = new ArduinoService(this.mHandler);
		new Thread(this.mArduinoS).start();
		// CONNECT to the TTT Server
		// DEVELOPMENT:SERVERDISABLED	
		//this.mServer = TTTServer.getInstance();
		//this.mServer.registerEventListener(this);
		//this.mServer.start();
	}
	
	@Override
	public void eventReceived(TTTEvent event) {
		Log.w(TAG, "eventReceived(): type=" + event.getType()+ " message=" + event.getMessage());
		int value = Integer.parseInt(event.getMessage());
		this.mHandler.obtainMessage(HANDLER_MESSAGE_FROM_SERVER, value, event.getType()).sendToTarget();
	}
	
	private void messageReceived(Message msg) {
		int target = msg.what;
		int value = msg.arg1;
		int type = msg.arg2;
		Log.w(TAG, "messageReceived(): target=" + target + " value=" + value + " type=" + type);
		
		// DEVELOPMENT:SERVERDISABLED
		if (true) return;
		
		switch (target) {
		case HANDLER_MESSAGE_FROM_SERVER:
			int msgCode = value;
			switch (type) {
				case TTTEvent.TYPE_BUTTON_CLICK:
					msgCode = value;
					break;
				case TTTEvent.TYPE_STARTGAME_CLICK:
					msgCode = ARDUINO_MSG_START_GAME;
					break;
				case TTTEvent.TYPE_ENDGAME:
					if (value == 1) 
						msgCode = ARDUINO_MSG_END_GAME_WINNER;
					else 
						msgCode = ARDUINO_MSG_END_GAME_LOSER;
				default:
					break;
			}
			this.mActivity.showDebugMessage("Received from server()=" + msgCode, false);
			//this.mArduinoS.write(msgCode);
			break;
		case ArduinoService.HANDLER_MESSAGE_FROM_ARDUINO:
			switch (value) {
				case ARDUINO_MSG_START_GAME:
					this.mServer.startGameClick();
					break;
				case ARDUINO_MSG_END_GAME_WINNER:
					this.mServer.endGame("0");
					break;
				case ARDUINO_MSG_END_GAME_LOSER:
					this.mServer.endGame("1");
					break;
				default:
					this.mServer.buttonClick(""+value);
					break;
			}
			this.mActivity.showDebugMessage("Received from arduino: " + value, true);
			//Toast.makeText(this.mActivity.getApplicationContext(), "Received from arduino: " + value, Toast.LENGTH_SHORT);
			break;
		default:
			//FIXME error happened handling messages
			break;
		}
	}
	
	protected void developmentSendMessage(int number){
		this.mArduinoS.write(number);
	}
		
}
