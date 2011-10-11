package org.androino.prototype;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

public class AudioArduinoService extends ArduinoService {

	private static final int GT540_SAMPLE_FREQ = 44100;
	private static final int DEVEL_SAMPLE_FREQ = 8000;
	private static final int AUDIO_SAMPLE_FREQ = DEVEL_SAMPLE_FREQ;

	private static final String TAG = "AudioArduinoService";
	
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
		testRecordAudio();

		// int i = AudioTrack.getMinBufferSize(44100, 2, 2);
		// AudioTrack localAudioTrack = new AudioTrack(3, 44100, 2, 2, i, 1);
		// localAudioTrack.play();
		// int i23 = i10.length;
		// int i24 = localAudioTrack.write(i10, 0, i23);
		// localAudioTrack.write(new byte[1], 0, i10);
		// localAudioTrack.stop();
		// localAudioTrack.release();
	}

	private void testRecordAudio() {
		int AUDIO_BUFFER_SIZE = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_FREQ, 
				2, AudioFormat.ENCODING_PCM_16BIT);
		Log.d(TAG, "buffer size:" + AUDIO_BUFFER_SIZE);
		byte[] audioData = new byte[2 * AUDIO_BUFFER_SIZE];

		// appendDebugInfo("BufferSize", "" + AUDIO_BUFFER_SIZE);
		AudioRecord aR = new AudioRecord(
				MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_FREQ, 2,
				AudioFormat.ENCODING_PCM_16BIT, AUDIO_BUFFER_SIZE);

		// audio recording
		aR.startRecording();
		int nBytes = 0;
		nBytes = aR.read(audioData, 0, AUDIO_BUFFER_SIZE);
		Log.d(TAG, "read #bytes:" + nBytes);
		showBytebuffer(audioData);
		//try {Thread.sleep(1000 * 2); } catch (InterruptedException e) {e.printStackTrace();}
		nBytes = aR.read(audioData, 0, AUDIO_BUFFER_SIZE);
		Log.d(TAG, "read #bytes:" + nBytes);
		showBytebuffer(audioData);
		try {Thread.sleep(1000 * 2); } catch (InterruptedException e) {e.printStackTrace();}
		aR.stop();
		aR.release();
		
	

//		double slotTime = 1.0 * AUDIO_BUFFER_SIZE / AUDIO_SAMPLE_FREQ;
//		double time = 0;

		// for (int i = 0; i < 5; i++) {
		// nBytes = localAudioRecord.read(audioData, 0, AUDIO_BUFFER_SIZE);
		// slotTime = 1.0 * nBytes / AUDIO_SAMPLE_FREQ ;
		// time = time + slotTime;
		// appendDebugInfo("Time:", "" + time);
		// }

		/*
		 * for (int i = 0; i < audioData.length; i++) { int value =
		 * byteArrayToInt(audioData); showAudioValue(value, nBytes);
		 * 
		 * }
		 */
//		Date d = new Date();
//		long ts = d.getTime();
		//nBytes = aR.read(audioData, 0, AUDIO_BUFFER_SIZE);
		// debugMessage("nBytes" + nBytes);
		//nBytes = localAudioRecord.read(audioData, nBytes, AUDIO_BUFFER_SIZE);
		// appendDebugInfo("nBytes2", "" + nBytes);

		//time = 1.0 * nBytes / AUDIO_SAMPLE_FREQ;
		//d = new Date();
		//ts = d.getTime() - ts; // number of millis

		// appendDebugInfo("slotTiem", " " +slotTime);
		// appendDebugInfo("NBytes", "" + nBytes);
		// appendDebugInfo("Time", "" + time);
		// appendDebugInfo("Total time", "" + ts);
/*
		try {
			Thread.sleep(1000 * 5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		aR.stop();
		aR.release();

		showBytebuffer(audioData);
*/
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
			if (counter > 10) break;
		}
	}

}
