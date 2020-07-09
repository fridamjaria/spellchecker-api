package com.spellchecker.api;

/**
 *
 * @author fridamjaria
 *
 */

public class TriFreq implements Comparable<TriFreq>{
	private String trigram;
	private int freq;

	TriFreq(String trigram, int freq) {
		this.trigram = trigram;
		this.freq = freq;
	}

	String getTri() {
		return trigram;
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
