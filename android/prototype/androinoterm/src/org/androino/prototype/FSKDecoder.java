package org.androino.prototype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import android.os.Handler;
import android.util.Log;

public class FSKDecoder extends Thread {

	private boolean forceStop;
	private Handler mClientHandler;
	private Vector<byte[]> mSound; 
	private static String TAG = "FSKDecoder";
		
	public FSKDecoder(Handler handler){
		this.mClientHandler = handler;
		this.mSound = new Vector<byte[]>();
		this.forceStop = false;
	}

	public void run() {
		
		while (!this.forceStop) {
			
			try {
				if (soundAvailable()){
					decodeSound();
				} else
				Thread.sleep(1000*1);
			} catch (InterruptedException e) {
				Log.e("FSKDecoder:run", "error", e);
				e.printStackTrace();
			}
		}

	}
	public synchronized void stopAndClean(){
		Log.i(TAG, "stopAndClean()");
		this.forceStop = true;
	}
	
	private synchronized boolean soundAvailable(){
		if (this.mSound.size()>0) return true;
		else return false;
	}
	
	public synchronized void addSound(byte[] sound, int nBytes){
		Log.i(TAG, "addSound nBytes="+ nBytes);
		byte[] data = new byte[nBytes];
		for (int i = 0; i < nBytes; i++) {
			data[i] = sound[i];
		}
		this.mSound.add(data);
	}
	
	private synchronized byte[] consumeSound(){
		int counter = 0;
		for (int i = 0; i < this.mSound.size(); i++) {
			counter += this.mSound.elementAt(i).length;
		}
		byte[] sound = new byte[counter];
		counter = 0;
		for (int i = 0; i < this.mSound.size(); i++) {
			byte[] s = this.mSound.elementAt(i);
			for (int j = 0; j < s.length; j++) {
				sound[counter+j] = s[j];
			}
			counter = s.length;
		}
		this.mSound.clear();
		return sound;
	}

	private double[] byte2double(byte[] data){
		double d[] = new double[data.length];
		ByteBuffer buf = ByteBuffer.wrap(data, 0, data.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int counter = 0;
		while (buf.remaining() >= 2) {
			short s = buf.getShort();
			double value = (double) s;
			d[counter] = value;
			counter++;
		}
		return d;
		
	}
	
	private void decodeSound(){
		byte[] sound = consumeSound();
		Log.i(TAG, "analizeSound: length=" + sound.length);
		//this.decodeAmplitude(sound, sound.length);
		this.decodeFSK(sound);
	}
	
	private void decodeFSK(byte[] audioData) {
		try {
			double[] sound = byte2double(audioData);
			int message = FSKModule.decodeSound(sound);
			Log.d(TAG, "decodeFSK():message=" + message);
			if (message >0)
			this.mClientHandler.obtainMessage(ArduinoService.HANDLER_MSG_RECEIVED, message, 0).sendToTarget();
		} 
		catch (AndroinoException ae){
			Log.e(TAG, "decodeFSK:Androino ERROR="+ ae.getMessage());
			this.mClientHandler.obtainMessage(ArduinoService.HANDLER_MSG_RECEIVED, -1, 0).sendToTarget();
		}
		catch (Exception e) {
			this.mClientHandler.obtainMessage(ArduinoService.HANDLER_MSG_RECEIVED, -2, 0).sendToTarget();
			Log.e(TAG, "decodeFSK:ERROR="+ e.getMessage(), e);
		}
	}

	
	
	private double decodeAmplitude(byte[] audioData, int nBytes) {
		double volume = 0;
		ByteBuffer buf = ByteBuffer.wrap(audioData, 0, nBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		double NORM_FACTOR = 32768.0;

		int counter = 0;
		double amplitude = 0;
		while (buf.remaining() >= 2) {
			counter++;
			short s = buf.getShort();
			double value = (double) s;
			value = value / NORM_FACTOR;
			if (value < 0)
				value = value * (-1.0);
			amplitude += value;
			// if (counter > 10) break;
		}
		volume = 100 * amplitude / counter;
		Log.v(TAG, "decodeAmplitude():volume=" + volume);
		this.mClientHandler.obtainMessage(1, (int)volume, (int)(volume*100)).sendToTarget();
		return volume;
	}
	
}
