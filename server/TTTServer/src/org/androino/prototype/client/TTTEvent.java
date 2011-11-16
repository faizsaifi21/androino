package org.androino.prototype.client;

public class TTTEvent {
	
	public static final int TYPE_SERVER_ERROR		= -1;
	public static final int TYPE_BUTTON_CLICK 		= 0;
	public static final int TYPE_STARTGAME_CLICK 	= 1;
	public static final int TYPE_CONNECT 			= 2;
	public static final int TYPE_DISCONNECT 		= 3;
	public static final int TYPE_ENDGAME 			= 4;
	
	private int type;
	private String message;

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
