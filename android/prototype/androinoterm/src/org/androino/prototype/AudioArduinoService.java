package org.androino.prototype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class AudioArduinoService extends ArduinoService {

	private static final int GT540_SAMPLE_FREQ = 44100;
	private static final int DEVEL_SAMPLE_FREQ = 8000;
	private static final int AUDIO_SAMPLE_FREQ = DEVEL_SAMPLE_FREQ;
	
	public static int BAUD_RATE_SENDING=315;
	public static int BAUD_RATE_RECEIVING=315;

	private static final String TAG = "AudioArduinoService";
	
	private byte[] 	testAudioArray;
	private int 	testFrequency = 400;
	
	public AudioArduinoService(Handler handler) {
		super(handler);
	}

	public void run() {
/*
		// AUDIO RECORDING
		int j = AudioRecord.getMinBufferSize(44100, 2, 2);
		byte[] audioData = new byte[j];
		// "bufferSize " + j;
		int m = 2;
		AudioRecord audioR = new AudioRecord(1, 44100, 2, m, j);
		audioR.startRecording();
*/
		this.forceStop = false;
		// continuous loop
		while (true) {
			// stop if requested
			if (this.forceStop) {
//				audioR.stop();
//				audioR.release();
				break;
			}
			// sound acquisition
//			int nBytes = audioR.read(audioData, 0, j);
//			analyzeSound(audioData, nBytes);
		}
	}

	private void analyzeSound(byte[] audioData, int nBytes) {

		// decode sound

		sendMessage(audioData.length, nBytes);
	}

	public void write(String message) {
		Log.i("ArduinoService::MSG", message);
		//testRecordAudio();
		testFrequency = testFrequency + 300;
		if (testFrequency>3000) testFrequency = 400;

	}
	public void stopAndClean(){
		super.stopAndClean();
		testPlayAudio();
	}
	
	private byte[] generateTone(int frequency){
		int duration = 1; //s
		int samplingRate = 8000; //Hz
		int numberOfSamples = duration * samplingRate;
		double samplingTime = 1.0/samplingRate;
		Log.i(TAG, "generateTone:samplingTime="+samplingTime);
		ByteBuffer buf = ByteBuffer.allocate(4*numberOfSamples);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		double amplitude = 10000.0;
		double y = 0;
		for (int i = 0; i < numberOfSamples; i++) {
			y  = amplitude *  Math.sin( 2 * Math.PI * frequency * i * samplingTime);
			try {
				//buf.putDouble(y);
				int yInt = (int) y;
				buf.putInt(yInt);
			} catch (Exception e) {
				Log.e(TAG, "generateTone:error i=" + i);
				e.printStackTrace();
				break;
			}
		}
		return buf.array();
		
	}
	private void testPlayAudio(){
		int AUDIO_BUFFER_SIZE = 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 
				2, AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize) AUDIO_BUFFER_SIZE = minBufferSize;
		AudioTrack aT = new AudioTrack(AudioManager.STREAM_MUSIC, AUDIO_SAMPLE_FREQ, 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
				AUDIO_BUFFER_SIZE, AudioTrack.MODE_STREAM);
		aT.play();
		//int nBytes = aT.write(this.testAudioArray, 0, this.testAudioArray.length);
		byte[] tone = generateTone(this.testFrequency);
		int nBytes = aT.write(tone, 0, tone.length);
		aT.stop();
		aT.release();
	}

	private void testRecordAudio() {
		int AUDIO_BUFFER_SIZE = 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 
				2, AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize) AUDIO_BUFFER_SIZE = minBufferSize;
				
		Log.i(TAG, "buffer size:" + AUDIO_BUFFER_SIZE);
		byte[] audioData = new byte[AUDIO_BUFFER_SIZE];

		// appendDebugInfo("BufferSize", "" + AUDIO_BUFFER_SIZE);
		AudioRecord aR = new AudioRecord(
				MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT, AUDIO_BUFFER_SIZE);

		// audio recording
		aR.startRecording();
		int nBytes = 0;
		int index = 0;
		int freeBuffer = AUDIO_BUFFER_SIZE;
		do {
			nBytes = aR.read(audioData, index, freeBuffer);
			if (nBytes<0) {
				Log.e(TAG, "read error=" + nBytes);
				break; //error happened
			}
			freeBuffer = freeBuffer - nBytes;
			index = index + nBytes;
			Log.i(TAG, "read #bytes:" + nBytes);
		} while (freeBuffer >0);
		
		testAudioArray = audioData;
		String message = "Sampling=" + AUDIO_SAMPLE_FREQ + ":" + "buffer size="  +AUDIO_BUFFER_SIZE + "\n"; 
		saveAudioToFile(message, audioData);
		//Utility.writeToFile(message);
		//showBytebuffer(audioData);
		//try {Thread.sleep(1000 * 2); } catch (InterruptedException e) {e.printStackTrace();}
		//nBytes = aR.read(audioData, 0, AUDIO_BUFFER_SIZE);
		//Log.d(TAG, "read #bytes:" + nBytes);
		//showBytebuffer(audioData);
		//try {Thread.sleep(1000 * 2); } catch (InterruptedException e) {e.printStackTrace();}
		aR.stop();
		aR.release();
		Log.i(TAG, "audio recording stoped");
	}
	private void saveAudioToFile(String header, byte[] audioData) {
		ByteBuffer buf = ByteBuffer.wrap(audioData, 0, audioData.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		StringBuffer strB = new StringBuffer(header);
		int counter=0;
		while (buf.remaining() >= 2) {
			counter++;
			short s = buf.getShort();
			double mono = (double) s;
			strB.append(mono);
			strB.append("\n");
		}
		Utility.writeToFile(strB.toString());
	}
	
	private void showBytebuffer(byte[] audioData) {
		ByteBuffer buf = ByteBuffer.wrap(audioData, 0, audioData.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		int counter=0;
		while (buf.remaining() >= 2) {
			counter++;
			short s = buf.getShort();
			double mono = (double) s;
			// double mono_norm = mono / 32768.0;
			Log.v(TAG, ""+mono);
			//msg += "Bytebuffer: " + mono + ":\n";
			//if (counter > 10) break;
		}
	}

}
