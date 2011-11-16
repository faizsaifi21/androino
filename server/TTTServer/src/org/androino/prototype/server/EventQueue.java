package org.androino.prototype.server;

import java.util.Date;

/**
 * Memory register of event information. One variable for each user.
 *
 */
public class EventQueue {
	private String idAPlayer = "A";
	private String idBPlayer = "B";
	private String lastEventAPlayer = "NO";
	private String lastEventBPlayer = "NO";
	private Date lastDateAPlayer = new Date();
	private Date lastDateBPlayer = new Date();
	private boolean lastEventAConsumed = false;
	private boolean lastEventBConsumed = false;

	public static String NO_EVENT_TOKEN = "OK";
	public static String CONNECT_EVENT_TOKEN = "CONNECT";

	private static EventQueue instance = null;

	public static EventQueue getInstance() {
		if (instance == null) {
			instance = new EventQueue();
		}
		return instance;
	}

	public String connectEvent() {
		// initial event: register the users, assigns and returns an user id
		String user = this.idAPlayer; // assigned A by default
		if (lastEventAPlayer.equals(CONNECT_EVENT_TOKEN)) {
			user = this.idBPlayer;
		}
		this.addEvent(user, CONNECT_EVENT_TOKEN);
		return user;
	}

	public void addEvent(String user, String message) {
		// register a new event in memory for the user (the previous event is overwritten) 
		if (user.equals(idAPlayer)) {
			this.lastEventAPlayer = message;
			this.lastDateAPlayer = new Date();
			this.lastEventAConsumed = false;
		} else {
			this.lastEventBPlayer = message;
			this.lastDateBPlayer = new Date();
			this.lastEventBConsumed = false;
		}
	}

	public String consumeEvents(String user) {
		// check if there a new event for the user. The response is:
		// OK:TS (if there is no new event)
		// EVENT:TS (with the last event info and the event is "consumed"
		String response = NO_EVENT_TOKEN + ":" + new Date().getTime();
		if (user.equals(idBPlayer)) {
			if (!this.lastEventAConsumed) {
				response = this.lastEventAPlayer + ":"
						+ this.lastDateAPlayer.getTime();
				this.lastEventAConsumed = true;
			}
		} else {
			if (!this.lastEventBConsumed) {
				response = this.lastEventBPlayer + ":"
						+ this.lastDateBPlayer.getTime();
				this.lastEventBConsumed = true;
			}
		}
		return response;
	}
	
	public String debugInfo(){
		String info = "DEBUG:" + new Date().getTime() + "<br>";  
		info+= idAPlayer + "=" + this.lastEventAPlayer + ":" + this.lastDateAPlayer.getTime() + "=>" + this.lastEventAConsumed + "<br/>";
		info+= idBPlayer + "=" + this.lastEventBPlayer + ":" + this.lastDateBPlayer.getTime() + "=>" + this.lastEventBConsumed + "<br/>";
		return info;
	}
}
