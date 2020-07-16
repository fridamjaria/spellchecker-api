package com.spellchecker.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Copyright 2020 fridamjaria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
