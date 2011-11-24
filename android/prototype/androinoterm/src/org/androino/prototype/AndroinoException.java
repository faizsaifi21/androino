package org.androino.prototype;

public class AndroinoException extends RuntimeException {

	public static final int TYPE_FSK_DECODING_ERROR = 0;
	
	private int type;
	
	public int getType(){
		return this.type;
	}
	
	public AndroinoException(String detailMessage, Throwable throwable, int type) {
		super(detailMessage, throwable);
		this.type = type;
	}

	public AndroinoException(String detailMessage, int type) {
		super(detailMessage);
		this.type = type;
	}

	
}
