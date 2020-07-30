package com.spellchecker.api;

import java.util.ArrayList;

/**
 *
 * @author fridamjaria
 *
 */

public final class BinarySearch {
	int findStart(ArrayList<String> arr, String word, int low, int high) {
		String sub = word.substring(word.length()-2);
		int mid;
		String word2;
		String sub2;
		int compare;
		String prevStr;

		while(high >= low) {
			mid = (low + high) / 2;
			word2 = arr.get(mid);
			sub2 = word2.substring(0, 2);
			compare = sub.compareTo(sub2);

			if(compare == 0){ //if the strings are equal;
				if(mid == 0) return mid;

				prevStr = arr.get(mid-1);
				if(!sub.equals(prevStr.substring(0, 2))) { //if the previous element in the arraylist != sub
					return mid;
				}

				high = mid-1;
			} else if(compare < 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}

		return -1; // no start found
	}

	int findEnd(ArrayList<String> arr, String word, int low, int high) {
		String sub = word.substring(word.length()-2);
		int mid;
		String word2;
		String sub2;
		int compare;
		String nextStr;

		while(high >= low) {
			mid = (low + high) / 2;
			word2 = arr.get(mid);
			sub2 = word2.substring(0, 2);
			compare = sub.compareTo(sub2);

			if(compare == 0){ //if the strings are equal;
				if(mid == arr.size()-1) return mid;

				nextStr = arr.get(mid+1);
				if(!sub.equals(nextStr.substring(0, 2))) { //if the next element in the arraylist != sub
					return mid;
				}
				low = mid + 1;
			} else if(compare < 0) {
				high = mid -1;
			} else {
				low = mid + 1;
			}
		}

		return -1;
	}
}
