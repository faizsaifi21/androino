package org.androino.prototype;

import android.media.AudioRecord;
import android.os.Handler;
import android.util.Log;

public class AudioArduinoService extends ArduinoService {
	
	public AudioArduinoService(Handler handler) {
		super(handler);
	}

	public void run() {
		// AUDIO RECORDING
		int j = AudioRecord.getMinBufferSize(44100, 2, 2);
	    byte[] audioData = new byte[j];
	    // "bufferSize " + j;
	    int m = 2;
	    AudioRecord audioR = new AudioRecord(1, 44100, 2, m, j);
	    audioR.startRecording();

	    this.forceStop = false;
	    // continuous loop
	    while (true) {
	    	// stop if requested
	    	if (this.forceStop) {
	    		audioR.stop();
	    		audioR.release();
	    		break;
	    	}
	    	// sound acquisition
	    	int nBytes = audioR.read(audioData, 0, j);
	    	analyzeSound(audioData,nBytes);
	    }
	}

	private void analyzeSound(byte[] audioData, int nBytes) {
		
		// decode sound
		
		sendMessage(audioData.length, nBytes);
	}
	
	public void write(String message) {
		Log.i("ArduinoService::MSG", message);

		//int i = AudioTrack.getMinBufferSize(44100, 2, 2);
	    //AudioTrack localAudioTrack = new AudioTrack(3, 44100, 2, 2, i, 1);
        //localAudioTrack.play();
		//int i23 = i10.length;
        //int i24 = localAudioTrack.write(i10, 0, i23);
        //localAudioTrack.write(new byte[1], 0, i10);
        //localAudioTrack.stop();
        //localAudioTrack.release();
	}
	
}
