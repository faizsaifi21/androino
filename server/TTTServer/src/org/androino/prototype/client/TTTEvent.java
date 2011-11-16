package org.androino.prototype.client;

import java.util.StringTokenizer;

public class TTTEvent {
	
	public static final int TYPE_SERVER_ERROR		= -1;
	public static final int TYPE_BUTTON_CLICK 		= 0;
	public static final int TYPE_STARTGAME_CLICK 	= 1;
	public static final int TYPE_CONNECT 			= 2;
	public static final int TYPE_DISCONNECT 		= 3;
	public static final int TYPE_ENDGAME 			= 4;
	
	private int type;
	private String message;

	public TTTEvent(int type, String message){
		this.type = type;
		switch (type) {
		case TYPE_SERVER_ERROR: 
			this.setMessage("ERROR_" + message);	
			break;
		case TYPE_BUTTON_CLICK: 
			this.setMessage("B_" + message);	
			break;
		case TYPE_STARTGAME_CLICK: 
			this.setMessage("START");	
			break;
		case TYPE_CONNECT: 
			this.setMessage("CONNECT");	
			break;
		case TYPE_DISCONNECT: 
			this.setMessage("DISCONNECT");	
			break;
		case TYPE_ENDGAME: 
			this.setMessage("END_" + message);	
			break;
		}
	}
	public static TTTEvent parseEvent(String token){
		// expected TEXT:TS
		StringTokenizer sT = new StringTokenizer(token, ":");
		String message = sT.nextToken();
		int t = TYPE_SERVER_ERROR;
		if (message.startsWith("CONNECT")) t = TYPE_CONNECT;
		else if (message.startsWith("B_")) t = TYPE_BUTTON_CLICK;
		else if (message.startsWith("START")) t = TYPE_STARTGAME_CLICK;
		else if (message.startsWith("DISCONNECT")) t = TYPE_DISCONNECT;
		else if (message.startsWith("END_")) t = TYPE_ENDGAME;
		TTTEvent event = new TTTEvent(t,message);
		return event;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString(){
		return super.toString() + " type=" + this.type + " msg=" + this.message;  
	}
	
	
}
