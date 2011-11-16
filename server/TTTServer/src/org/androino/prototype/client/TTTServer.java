package org.androino.prototype.client;

public class TTTServer implements iTTTEventListener{
	
	private static TTTServer instance;
	private iTTTEventListener listener;
	private ServerConnection server;

	private TTTServer(){
		this.server = new ServerConnection(this);
	}
	public static TTTServer getInstance(){
		if (instance == null){
			instance = new TTTServer();
		}
		return instance;
	}
	public void start(){
		this.server.start();
	}
	public void stop(){
		this.server.stopAndClean();
	}
	
	protected void notifyEvent(String eventMessage){
		TTTEvent event = null;
		if (this.listener !=null)
			listener.eventReceived(event);
	}
	
	public void registerEventListener(iTTTEventListener listener){
		// current implementation only allows a unique listener registration
		this.listener = listener;
	}
	public void unRegisterEventListener(iTTTEventListener listener){
		this.listener = null;
	}

	public void buttonClick(String buttonId){
		TTTEvent event = new TTTEvent();
		event.setType(TTTEvent.TYPE_BUTTON_CLICK);
		this.server.sentEvent(event);
	}
	public void startGameClick(){
		TTTEvent event = new TTTEvent();
		event.setType(TTTEvent.TYPE_STARTGAME_CLICK);
		this.server.sentEvent(event);
	}
	public void endGame(String result){
		TTTEvent event = new TTTEvent();
		event.setType(TTTEvent.TYPE_ENDGAME);
		this.server.sentEvent(event);
	}
	
	// test implementation
	
	public static void main(String[] args){
		TTTServer server = TTTServer.getInstance();
		server.registerEventListener(server);
		server.start();
		server.buttonClick("A3");
		server.stop();
	}

	public void eventReceived(TTTEvent event) {
		debugMessage("Event received: " + event.toString() );
		switch (event.getType()) {
		case TTTEvent.TYPE_BUTTON_CLICK:
			this.buttonClick( event.getMessage() + "A");
			break;
		default:
			break;
		}
	}
	private void debugMessage(String msg){
		System.out.println(">>" + msg);
	}

}
