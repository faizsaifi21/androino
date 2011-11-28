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
	private static final int AUDIO_SAMPLE_FREQ = GT540_SAMPLE_FREQ;

	public static int BAUD_RATE_SENDING = 315;
	public static int BAUD_RATE_RECEIVING = 315;

	public static int SOFT_MODEM_HIGH_FREQ = 3150;
	public static int SOFT_MODEM_LOW_FREQ = 1575;

	private static final String TAG = "AudioArduinoService";

	private byte[] testAudioArray;
	private int testFrequency = 400;
	private int globalCounter = 0;
	private FSKDecoder mDecoder;
	
	public AudioArduinoService(Handler handler) {
		super(handler);
	}
	
	
	public void run() {
		this.forceStop = false;

		//testDecodeAmplitude();
		//testDecode();
		
		// do nothing loop to avoid thread finalization
		try {
			Thread.sleep(5000 * 1);
		} catch (InterruptedException e) {
			Log.e("FSKDecoder:run", "error", e);
			e.printStackTrace();
		}
		
		// continuous loop
		while (true) {
			// stop if requested
			if (this.forceStop) {
				// audioR.stop();
				// audioR.release();
				break;
			}
			// sound acquisition
			// int nBytes = audioR.read(audioData, 0, j);
			// analyzeSound(audioData, nBytes);
		}
	}

	private void analyzeSound(byte[] audioData, int nBytes) {
		// decode sound
		sendMessage(audioData.length, nBytes);
	}

	public void write(String message) {
		Log.i("ArduinoService::MSG", message);
		this.testEncode();
		//testRecordAudio();
		// testFrequency = testFrequency + 300;
		// if (testFrequency>3000) testFrequency = 400;
	}

	public void stopAndClean() {
		Log.i(TAG,"stopAndClean");
		this.mDecoder.stopAndClean();
		super.stopAndClean();
		
		// use this code to generate a pure tone
		//this.testAudioArray = this.generateTone(667);
		//testPlayAudio();
	}

	
	private byte[] generateTone(int frequency) {
		int duration = 1; // s
		int samplingRate = 8000; // Hz
		int numberOfSamples = duration * samplingRate;
		double samplingTime = 1.0 / samplingRate;
		Log.i(TAG, "generateTone:samplingTime=" + samplingTime);
		ByteBuffer buf = ByteBuffer.allocate(4 * numberOfSamples);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		double amplitude = 30000.0; // max amplitude 32768
		double y = 0;
		for (int i = 0; i < numberOfSamples; i++) {
			y = amplitude
					* Math.sin(2 * Math.PI * frequency * i * samplingTime);
			try {
				// buf.putDouble(y);
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

	private void testEncode() {
		int AUDIO_BUFFER_SIZE = 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize)
			AUDIO_BUFFER_SIZE = minBufferSize;
		AudioTrack aT = new AudioTrack(AudioManager.STREAM_MUSIC,
				AUDIO_SAMPLE_FREQ, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, AUDIO_BUFFER_SIZE,
				AudioTrack.MODE_STREAM);
		aT.play();
		//byte[] tone = generateTone(1000);
		int[] bits = {2,1,1,1,2,2,1,1};
		double[] sound = FSKModule.encode(bits);
		Log.i(TAG, "testEncode() sound lenght=" + sound.length);
		ByteBuffer buf = ByteBuffer.allocate(4 * sound.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < sound.length; i++) {
			int yInt = (int) sound[i];
			buf.putInt(yInt);
		}
		byte[] tone = buf.array();
		
		int nBytes = aT.write(tone, 0, tone.length);
		aT.stop();
		aT.release();
	}

	
	private void testPlayAudio() {
		int AUDIO_BUFFER_SIZE = 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize)
			AUDIO_BUFFER_SIZE = minBufferSize;
		AudioTrack aT = new AudioTrack(AudioManager.STREAM_MUSIC,
				AUDIO_SAMPLE_FREQ, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, AUDIO_BUFFER_SIZE,
				AudioTrack.MODE_STREAM);
		aT.play();
		int nBytes = aT.write(this.testAudioArray, 0,
				this.testAudioArray.length);
		// byte[] tone = generateTone(this.testFrequency);
		// int nBytes = aT.write(tone, 0, tone.length);
		aT.stop();
		aT.release();
	}

	private byte[] removed_newAudioDataBuffer(){
		int AUDIO_BUFFER_SIZE = 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize)
			AUDIO_BUFFER_SIZE = minBufferSize;

		Log.i(TAG, "buffer size:" + AUDIO_BUFFER_SIZE);
		byte[] audioData = new byte[AUDIO_BUFFER_SIZE];
		return audioData;
	}
	
	private int removed_recordAudio(byte[] audioData) {
		int AUDIO_BUFFER_SIZE = audioData.length;
		// appendDebugInfo("BufferSize", "" + AUDIO_BUFFER_SIZE);
		AudioRecord aR = new AudioRecord(MediaRecorder.AudioSource.MIC,
				AUDIO_SAMPLE_FREQ, 2, AudioFormat.ENCODING_PCM_16BIT,
				AUDIO_BUFFER_SIZE);

		// audio recording
		aR.startRecording();
		int nBytes = aR.read(audioData, 0, AUDIO_BUFFER_SIZE);
		aR.stop();
		aR.release();
		Log.i(TAG, "audio recording stoped");
		return nBytes;
	}
	
	private void testRecordAudio() {
		int AUDIO_BUFFER_SIZE = 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize)
			AUDIO_BUFFER_SIZE = minBufferSize;

		Log.i(TAG, "buffer size:" + AUDIO_BUFFER_SIZE);
		byte[] audioData = new byte[AUDIO_BUFFER_SIZE];

		// appendDebugInfo("BufferSize", "" + AUDIO_BUFFER_SIZE);
		AudioRecord aR = new AudioRecord(MediaRecorder.AudioSource.MIC,
				AUDIO_SAMPLE_FREQ, 2, AudioFormat.ENCODING_PCM_16BIT,
				AUDIO_BUFFER_SIZE);

		// audio recording
		aR.startRecording();
		int nBytes = 0;
		int index = 0;
		int freeBuffer = AUDIO_BUFFER_SIZE;
		do {
			nBytes = aR.read(audioData, index, freeBuffer);
			if (nBytes < 0) {
				Log.e(TAG, "read error=" + nBytes);
				break; // error happened
			}
			freeBuffer = freeBuffer - nBytes;
			index = index + nBytes;
			Log.i(TAG, "read #bytes:" + nBytes);
		} while (freeBuffer > 0);

		testAudioArray = audioData;
		String message = "Sampling=" + AUDIO_SAMPLE_FREQ + ":" + "buffer size="
				+ AUDIO_BUFFER_SIZE + "\n";
		saveAudioToFile(message, audioData);
		// Utility.writeToFile(message);
		// showBytebuffer(audioData);
		// try {Thread.sleep(1000 * 2); } catch (InterruptedException e)
		// {e.printStackTrace();}
		// nBytes = aR.read(audioData, 0, AUDIO_BUFFER_SIZE);
		// Log.d(TAG, "read #bytes:" + nBytes);
		// showBytebuffer(audioData);
		// try {Thread.sleep(1000 * 2); } catch (InterruptedException e)
		// {e.printStackTrace();}
		aR.stop();
		aR.release();
		Log.i(TAG, "audio recording stoped");
	}

	private void saveAudioToFile(String header, byte[] audioData) {
		ByteBuffer buf = ByteBuffer.wrap(audioData, 0, audioData.length);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		StringBuffer strB = new StringBuffer(header);
		int counter = 0;
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

		int counter = 0;
		while (buf.remaining() >= 2) {
			counter++;
			short s = buf.getShort();
			double mono = (double) s;
			// double mono_norm = mono / 32768.0;
			Log.v(TAG, "" + mono);
			// msg += "Bytebuffer: " + mono + ":\n";
			// if (counter > 10) break;
		}
	}

	private void testDecodeAmplitude() {
		int AUDIO_BUFFER_SIZE = 4000;// 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize)
			AUDIO_BUFFER_SIZE = minBufferSize;

		Log.i(TAG, "buffer size:" + AUDIO_BUFFER_SIZE);
		byte[] audioData = new byte[AUDIO_BUFFER_SIZE];

		// appendDebugInfo("BufferSize", "" + AUDIO_BUFFER_SIZE);
		AudioRecord aR = new AudioRecord(MediaRecorder.AudioSource.MIC,
				AUDIO_SAMPLE_FREQ, 2, AudioFormat.ENCODING_PCM_16BIT,
				AUDIO_BUFFER_SIZE);

		// audio recording
		aR.startRecording();
		int nBytes = 0;
		int index = 0;
		int counter = 0;
		this.forceStop = false;
		// continuous loop
		String message = "";
		while (true) {
			counter++;
			nBytes = aR.read(audioData, index, AUDIO_BUFFER_SIZE);
			// Log.v(TAG, "nBytes=" + nBytes);
			if (nBytes < 0) {
				Log.e(TAG, "read error=" + nBytes);
				break; // error happened
			}
			int volume = (int)decodeAmplitude(audioData, nBytes);
			Log.i(TAG, "volume="+volume+"%");
			
			//double frequency = decodeFrequency(audioData, nBytes);
			//Log.i(TAG, "freq=" + frequency + "Hz");

			if (this.forceStop) {
				break;
			}
		}
		aR.stop();
		aR.release();
		Log.i(TAG, "audio recording stoped");
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
		sendMessage((int)volume, 0);
//		this.globalCounter++;
//		if (globalCounter>50){
//			sendMessage((int)volume, 0);
//			globalCounter = 0;
//		}
		
		return volume;
	}

	private double decodeFrequency(byte[] audioData, int nBytes) {
		double frequency = 0;
		ByteBuffer buf = ByteBuffer.wrap(audioData, 0, nBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		int counter = 0;
		int signChangeCounter = 0;
		int sign = 1;
		while (buf.remaining() >= 2) {
			counter++;
			short s = buf.getShort();
			double value = (double) s;
			if (value > 0) {
				if (sign < 0) {
					signChangeCounter++;
					sign = 1;
				}
			} else { // value <0
				if (sign > 0) {
					signChangeCounter++;
					sign = -1;
				}
			}
		}
		int nCycles = signChangeCounter / 2;
		double time = (nBytes / 2.0) / AUDIO_SAMPLE_FREQ;

		frequency = nCycles / time;
		Log.v(TAG, "decodeFrequency():freq=" + frequency);
		// sendMessage(volume, 0);
		return frequency;
	}

	private void testDecode() {
		this.mDecoder = new FSKDecoder(this.mClientHandler);
		this.mDecoder.start();
		
		int AUDIO_BUFFER_SIZE = 4000;// 16000;
		int minBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT);
		if (AUDIO_BUFFER_SIZE < minBufferSize)
			AUDIO_BUFFER_SIZE = minBufferSize;

		Log.i(TAG, "buffer size:" + AUDIO_BUFFER_SIZE);
		byte[] audioData = new byte[AUDIO_BUFFER_SIZE];

		// appendDebugInfo("BufferSize", "" + AUDIO_BUFFER_SIZE);
		AudioRecord aR = new AudioRecord(MediaRecorder.AudioSource.MIC,
				AUDIO_SAMPLE_FREQ, 2, AudioFormat.ENCODING_PCM_16BIT,
				AUDIO_BUFFER_SIZE);

		// audio recording
		aR.startRecording();
		int nBytes = 0;
		int index = 0;
		int counter = 0;
		this.forceStop = false;
		// continuous loop
		String message = "";
		while (true) {
			counter++;
			nBytes = aR.read(audioData, index, AUDIO_BUFFER_SIZE);
			Log.d(TAG, "testDecode():audio acq: length=" + nBytes);
			// Log.v(TAG, "nBytes=" + nBytes);
			if (nBytes < 0) {
				Log.e(TAG, "read error=" + nBytes);
				break; // error happened
			}
			this.mDecoder.addSound(audioData, nBytes);
			
			//double frequency = decodeFrequency(audioData, nBytes);
			//Log.i(TAG, "freq=" + frequency + "Hz");

			if (this.forceStop) {
				break;
			}
		}
		aR.stop();
		aR.release();
		Log.i(TAG, "audio recording stoped");
	}
	
}
