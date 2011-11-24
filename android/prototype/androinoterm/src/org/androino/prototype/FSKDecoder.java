package org.androino.prototype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import android.os.Handler;
import android.util.Log;

public class FSKDecoder extends Thread {

	private Handler mClientHandler;
	private Vector<byte[]> mSound; 
	private static String TAG = "FSKDecoder";
		
	public FSKDecoder(Handler handler){
		this.mClientHandler = handler;
		this.mSound = new Vector<byte[]>();
	}

	public void run() {
		while (true) {
			try {
				if (soundAvailable()){
					analizeSound();
				} else
				Thread.sleep(1000*1);
			} catch (InterruptedException e) {
				Log.e("FSKDecoder:run", "error", e);
				e.printStackTrace();
			}
		}

	}
	private synchronized boolean soundAvailable(){
		if (this.mSound.size()>0) return true;
		else return false;
	}
	
	public synchronized void addSound(byte[] sound, int nBytes){
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
	
	private void analizeSound(){
		byte[] sound = consumeSound();
		Log.i(TAG, "analizeSound: length=" + sound.length);
		this.decodeAmplitude(sound, sound.length);
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
