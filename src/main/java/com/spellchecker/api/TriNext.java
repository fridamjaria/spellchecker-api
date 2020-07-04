package com.spellchecker.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author fridamjaria
 *
 */

public class TriNext implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 7460116597809101933L;
	private ArrayList<String> array;
	private HashMap<String, Integer> map;

	TriNext(ArrayList<String> arr, HashMap<String, Integer> hash){
		this.array = arr;
		this.map = hash;
	}

	ArrayList<String> getArray(){
		return this.array;
	}

	HashMap<String, Integer> getMap(){
		return this.map;
	}
}
