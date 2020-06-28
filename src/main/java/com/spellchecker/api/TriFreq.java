package com.spellchecker.api;

public class TriFreq implements Comparable<TriFreq>{
	String tri = "";
	int freq;
	
	TriFreq(String t) {
		this.tri = t;
		this.freq = 0;
	}
	
	String getTri() {
		return tri;
	}
	
	int getFreq() {
		return freq;
	}
	
	//method to compare probabilities 
	public int compareTo(TriFreq tf) {
		int prob = tf.getFreq();
		return prob - this.getFreq();
	}
}