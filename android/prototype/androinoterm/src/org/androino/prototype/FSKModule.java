package org.androino.prototype;

import java.io.*;

public class FSKModule {
	
	private static int BUFFER_SIZE	= 30000;
	private static int SAMPLING_FREQUENCY = 44100; //Hz
	private static double SAMPLING_TIME = 1000.0/SAMPLING_FREQUENCY; //ms
	

	//high: 7 samples/peak
	//low : 14 samples/peak
	// 1492 samples/message low+8bits+stop+end 
	// 136 samples/bit  (=1492/11)
	private static int SAMPLES_PER_BIT = 136;

	// bit-high = 22 peaks
	// bit-low = 6 peaks
	private static int HIGH_BIT_N_PEAKS = 22;
	private static int LOW_BIT_N_PEAKS = 6;
	
	private static int SLOTS_PER_BIT = 4; // 4 parts: determines the size of the part analyzed to count peaks
	private static int N_POINTS = SAMPLES_PER_BIT/SLOTS_PER_BIT;  // 34=136/4
	
	private static double PEAK_AMPLITUDE_TRESHOLD = 5000; // significative sample (not noise)
	private static int NUMBER_SAMPLES_PEAK = 3;			// minimum number of significative samples to be considered a peak
	
	private FSKModule(){
		
	}
	
	private static void debugInfo(String message){
		System.out.println(">>" + message);
	}
	
	private double[] readInfoFromFile(String filePath){
		double[] data = new double[BUFFER_SIZE];
		int counter = 0;	                       
		try {
			File f = new File(filePath);
			FileReader fR = new FileReader(f);
			LineNumberReader lR = new LineNumberReader(fR);
			String line = lR.readLine(); //first line skipped
			do {
				line = lR.readLine();
				double d = Double.parseDouble(line);
				data[counter] = d;
				counter++;
				if (counter>=BUFFER_SIZE) break; 
			} while (line!=null );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private int[] parseBits(int[] peaks){
		// from the number of peaks array decode into bits
		// 
		int i =0;
		//int slots_per_bit = 4;
		int nBits = peaks.length /SLOTS_PER_BIT;
		int[] bits = new int[nBits];
		i = findNextZero(peaks,i);
		i = findNextNonZero(peaks,i);
		do {
			//int nPeaks = peaks[i]+peaks[i+1]+peaks[i+2]+peaks[i+3];
			int nPeaks = 0;
			for (int j = 0; j < SLOTS_PER_BIT; j++) {
				nPeaks+= peaks[i+j];
			}
			int position = i/SLOTS_PER_BIT;
			bits[position] = 0;
			
			if (nPeaks>LOW_BIT_N_PEAKS-2) bits[position] = 1;
			if (nPeaks>LOW_BIT_N_PEAKS+4) bits[position] = 2;
			//if (nPeaks>5) bits[position] = 1;
			//if (nPeaks>12) bits[position] = 2;
			i=i+SLOTS_PER_BIT;
		} while (SLOTS_PER_BIT+i<peaks.length);
		return bits;
	}
	private int findNextNonZero(int[] peaks, int startIndex){
		// returns the position of the next value != 0 starting form startIndex
		int index = startIndex;
		int value = 1;
		do {
			value = peaks[index];
			index++;
		} while (value==0);
		return index-1;
	}

	private int findNextZero(int[] peaks, int startIndex){
		// returns the position of the next value = 0 starting form startIndex
		int index = startIndex;
		int value = 1;
		do {
			value = peaks[index];
			index++;
		} while (value!=0);
		return index-1;
	}
	
	private int[] processSound(double[] sound){
		// split the sound array into slots of N_POINTS and calculate the number of peaks
		
		int nPoints = N_POINTS;
		int nParts = sound.length / nPoints;
		int[] nPeaks = new int[nParts]; 
		int startIndex = 0;
		int i = 0;
		do {
			int endIndex = startIndex + nPoints;
			int n = this.countPeaks(sound, startIndex, endIndex);
			nPeaks[i] = n;
			i++;
			startIndex = endIndex;
		} while (i<nParts);
		//} while (startIndex+nPoints<sound.length);
		return nPeaks;
	}
	private int countPeaks(double[] sound, int startIndex, int endIndex){
		// count the number of peaks in the selected interval
		// peak identification criteria: sign changed and several significative samples (>PEAK_AMPLITUDE_TRESHOLD) 
		
		int index = startIndex;
		int signChangeCounter = 0;
		int numberSamplesGreaterThresdhold = 0;
		int sign = 0; // initialized at the first significant value
		do {
			double value = sound[index];
			if (Math.abs(value)>PEAK_AMPLITUDE_TRESHOLD) 
				numberSamplesGreaterThresdhold++; //significative value
			// sign initialization: take the sign of the first significant value
			if (sign==0 & numberSamplesGreaterThresdhold>0) sign = (int) (value / Math.abs(value));
			boolean signChanged = false;
			if (sign <0 & value >0)	signChanged = true;
			if (sign >0 & value <0)	signChanged = true;
			
			if (signChanged & numberSamplesGreaterThresdhold>NUMBER_SAMPLES_PEAK){
				signChangeCounter++; // count peak
				sign=-1*sign; //change sign
			}
			index++;
			//debugInfo(">>>>>>>index=" + index + " sign=" + sign + " signChangeCounter=" + signChangeCounter + " value=" + value + " numberSamplesGreaterThresdhold=" + numberSamplesGreaterThresdhold);
		} while (index<endIndex);
		return signChangeCounter;
	}
	
	
	public static void main(String[] args){
		FSKModule m = new FSKModule();
		//debugInfo("slot time(ms)=" + TIME_PER_SLOT);
		double[] sound;
		try {
			// reading info form file
			File f = new File("./");
			String path = f.getCanonicalPath();
			debugInfo("working dir=" + path);
			sound = m.readInfoFromFile("../testdata/sound.dat");
			for (int i = 0; i < 10; i++) {
				debugInfo("data:" + i + ":" + sound[i]);
			}
			// processing sound
			int[] nPeaks = m.processSound(sound);
			for (int i = 0; i < nPeaks.length; i++) {
				debugInfo("nPeaks:" + i*N_POINTS + ":" + nPeaks[i]);
			}
			// 
			int[] bits = m.parseBits(nPeaks);
			for (int i = 0; i < bits.length; i++) {
				debugInfo("bits:" + i*N_POINTS*SLOTS_PER_BIT + ":" + bits[i]);
			}
			for (int i = 0; i < bits.length; i++) {
				debugInfo("bits:" + i + ":" + bits[i]);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
