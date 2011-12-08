package org.androino.prototype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import android.os.Handler;
import android.util.Log;

public class FSKDecoder extends Thread {

	private static int MINIMUM_BUFFER = 4;
	
	private boolean forceStop;
	private Handler mClientHandler;
	private Vector<byte[]> mSound; 
	private static String TAG = "FSKDecoder";
	private boolean debugAddSoundDisabled = false;
		
	public FSKDecoder(Handler handler){
		this.mClientHandler = handler;
		this.mSound = new Vector<byte[]>();
		this.forceStop = false;
	}

	public void run() {
		
		while (!this.forceStop) {
			
			try {
				//if (signalAvailable()){
				if (soundAvailable()){
					decodeSound();
				} else
					Thread.sleep(200*1);
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
		boolean available = false;
		if (this.mSound.size()>=MINIMUM_BUFFER) available = true;
		Log.v(TAG, "soundAvailable()=" + available);
		return available;
	}
	private boolean signalAvailable(){
		if (soundAvailable()){
			// there is sound in the buffer
			byte[] sound = this.mSound.elementAt(0);
			double data[] = this.byte2double(sound);
			boolean available = FSKModule.signalAvailable(data);
			if (available) return true; // signal recognized
			// signal not recognized, removing sound from the buffer
			clearFirstSound();
			return false;
		} else return false;
	}
	
	public synchronized void addSound(byte[] sound, int nBytes){
		if (debugAddSoundDisabled) return;
		byte[] data = new byte[nBytes];
		for (int i = 0; i < nBytes; i++) {
			data[i] = sound[i];
		}
		this.mSound.add(data);
		Log.i(TAG, "addSound nBytes="+ nBytes + " accumulated=" + this.mSound.size());
	}
	
	private synchronized void clearFirstSound(){
		this.mSound.remove(0);
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
		Log.d(TAG, "consumeSound() nBytes=" + sound.length);
		return sound;
	}
	private void saveAudioToFile(String header, byte[] audioData) {
		ByteBuffer buf = ByteBuffer.wrap(audioData, 0, audioData.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		StringBuffer strB = new StringBuffer(header);
		int counter = 0;
		while (buf.remaining() >= 2) {
			counter++;
			//short s = buf.getShort();
			double mono =buf.getShort();
			strB.append(mono);
			strB.append("\n");
		}
		Utility.writeToFile(strB.toString());
	}

	private double[] byte2double(byte[] data){
		double d[] = new double[data.length];
		String header="DATA:";
		//saveAudioToFile(header, data);
		
		ByteBuffer buf = ByteBuffer.wrap(data, 0, data.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		int counter = 0;
		while (buf.remaining() >= 2) {
			
			double s = buf.getShort();
			
			d[counter] = s;
			counter++;
		}
				
		return d;
		
	}
	
	
	private void decodeSound(){
		byte[] sound = consumeSound();
		Log.i(TAG, "decodeSound: length=" + sound.length);
		//this.decodeAmplitude(sound, sound.length);
		this.decodeFSK(sound);
	}
	private void debugAndroinoException(AndroinoException ae){
		if (ae.getType() == AndroinoException.TYPE_FSK_DEBUG){
			this.debugAddSoundDisabled = true;
			Vector info = (Vector) ae.getDebugInfo();
			double[] sound = (double[]) info.get(0);
			int[] nPeaks = (int[]) info.get(1);
			int[] bits = (int[]) info.get(2);
			String message = (String) info.get(3);
			Log.w(TAG, "debugAndroinoException() sound lenght=" + sound.length );
			Log.w(TAG, "debugAndroinoException() npeaks len=" + nPeaks.length );
			Log.w(TAG, "debugAndroinoException() bits len=" + bits.length );
			Log.w(TAG, "debugAndroinoException() message=" + message);
			
			try {
				String header ="Adroino Exception\n";
				StringBuffer strB = new StringBuffer(header);
				for (int i = 0; i < sound.length; i++) {
					strB.append(sound[i]+ "\n" );
				}
				Utility.writeToFile(strB.toString());
			} catch (Exception e) {
				Log.e(TAG, "Error saving to file", e);
				e.printStackTrace();
			}
			
		}
	}
	
	private void decodeFSK(byte[] audioData) {
		double[] sound = byte2double(audioData);
		
		Log.d(TAG, "decodeFSK: bytes length=" + audioData.length);
		Log.i(TAG, "decodeFSK: doubles length=" + sound.length);
		try {
			int message = FSKModule.decodeSound(sound);
			Log.w(TAG, "decodeFSK():message=" + message);
			if (message >0)
			this.mClientHandler.obtainMessage(ArduinoService.HANDLER_MSG_RECEIVED, message, 0).sendToTarget();
		} 
		catch (AndroinoException ae){
			//this.debugAndroinoException(ae);
			Log.e(TAG, "decodeFSK:Androino ERROR="+ ae.getMessage());
			this.mClientHandler.obtainMessage(ArduinoService.HANDLER_MSG_RECEIVED, ae.getType(), 0).sendToTarget();
		}
		catch (Exception e) {
			Log.e(TAG, "decodeFSK:ERROR="+ e.getMessage(), e);
			this.mClientHandler.obtainMessage(ArduinoService.HANDLER_MSG_RECEIVED, -2, 0).sendToTarget();
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
