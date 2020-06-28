package com.spellchecker.api;

import java.util.ArrayList;

/**
 *
 * @author fridamjaria
 *
 */

public class BinarySearch {
	int findStart(ArrayList<String> arr, String tri, int low, int high) {
		String sub = tri.substring(tri.length()-2);
		while(high >= low) {
			int mid = (low + high) / 2;
			String tri2 = arr.get(mid);
			String sub2 = tri2.substring(0, 2);
			int compare = sub.compareTo(sub2);
			if(compare < 0) {
				high = mid - 1;
			}
			else if(compare == 0){ //if the strings are equal;
				if(mid == 0)
					return mid;
				else {
					String prev = arr.get(mid-1);
					if(sub.equals(prev.substring(0, 2))) { //if the previous element in the arraylist == sub
						high = mid-1;
					}
					else {
						return mid;
					}
				}
			}
			else {
				low = mid + 1;
			}
		}
		return -1;			
	}
	
	int findEnd(ArrayList<String> arr, String tri, int low, int high) {
		String sub = tri.substring(tri.length()-2);
		while(high >= low) {
			
			int mid = (low + high) / 2;
			//System.out.println(arr.get(mid));
			String tri2 = arr.get(mid);
			String sub2 = tri2.substring(0, 2);
			int compare = sub.compareTo(sub2);
			if(compare < 0) {
				high = mid -1;
			}
			else if(compare == 0){ //if the strings are equal;
				
				if(mid == arr.size()-1)
					return mid;
				else {
					String prev = arr.get(mid+1);
					if(sub.equals(prev.substring(0, 2))) { //if the previous element in the arraylist == sub
						low = mid + 1;
					}
					else {
						return mid;
					}
				}
			}
			else {
				low = mid + 1;
			}
		}
		return -1;
			
	}
	int findStartAlt(ArrayList<String> arr, String tri, int low, int high) {
		String sub = tri.substring(0, 2);
		while(high >= low) {
			int mid = (low + high) / 2;
			String tri2 = arr.get(mid);
			String sub2 = tri2.substring(tri2.length() - 2);
			int compare = sub.compareTo(sub2);
			if(compare < 0) {
				high = mid - 1;
			}
			else if(compare == 0){ //if the strings are equal;
				if(mid == 0)
					return mid;
				else {
					String prev = arr.get(mid-1);
					if(sub.equals(prev.substring(prev.length()-2))) { //if the previous element in the arraylist == sub
						high = mid-1;
					}
					else {
						return mid;
					}
				}
			}
			else {
				low = mid + 1;
			}
		}
		return -1;			
	}
	
	int findEndAlt(ArrayList<String> arr, String tri, int low, int high) {
		String sub = tri.substring(0, 2);
		while(high >= low) {
			
			int mid = (low + high) / 2;
			//System.out.println(arr.get(mid));
			String tri2 = arr.get(mid);
			String sub2 = tri2.substring(tri2.length()-2);
			int compare = sub.compareTo(sub2);
			if(compare < 0) {
				high = mid -1;
			}
			else if(compare == 0){ //if the strings are equal;
				
				if(mid == arr.size()-1)
					return mid;
				else {
					String prev = arr.get(mid+1);
					if(sub.equals(prev.substring(prev.length()-2))) { //if the previous element in the arraylist == sub
						low = mid + 1;
					}
					else {
						return mid;
					}
				}
			}
			else {
				low = mid + 1;
			}
		}
		return -1;
			
	}
}
