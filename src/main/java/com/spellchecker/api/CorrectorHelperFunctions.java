package com.spellchecker.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

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

public class CorrectorHelperFunctions {
    /**
     *
     * @param word
     * It iterates through the chars of a word and checks case of each char.
     * If the whole word is found to be capitalized, it changes the word to lowercase
     * If only the first or a few of the letters in the word are capitalized,
     * it only changes the first letter of the word to lowercase.
     * @return word with custom lowercase rules applied
     */
    public String customLowercase(String word) {
        if (word.equals(word.toLowerCase())) return word;

        boolean upperCase = true;
        for (char c : word.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                upperCase = false;
            }
        }
        if (upperCase) {
            word = word.toLowerCase(Locale.ROOT);
        } else {
            word = word.substring(0, 1).toLowerCase(Locale.ROOT) + word.substring(1);
        }
        return word;
    }

     /**
     *
     * @param word
     * @return array with trigrams of word
     */

    ArrayList<Trigram> constructTrigrams(String word) {
        switch(word.length()) {
            case 1:
                return new ArrayList<Trigram>(Arrays.asList(new Trigram(word + "xx")));
            case 2:
                return new ArrayList<Trigram>(Arrays.asList(new Trigram(word + "x")));
            case 3:
                return new ArrayList<Trigram>(Arrays.asList(new Trigram(word)));
        }

        ArrayList<Trigram> array = new ArrayList<>();
        String tri = "";

        for (int i = 0; i < word.length(); i++) {
            if (word.substring(i).length() < 3) {
                break;
            } else {
                tri += word.charAt(i);
                tri += word.charAt(i + 1);
                tri += word.charAt(i + 2);
                array.add(new Trigram(tri));
                tri = "";
            }
        }

        return array;
    }

    String combine(String s1, String s2) {
        String word = "";
        if (s1.substring(s1.length() - 2, s1.length()).equals(s2.substring(0, 2))) {
            word = s1.substring(0, s1.length() - 2) + s2;
        }
        return word;
    }

    //method to find trigram suggestions for incorrect trigram
    public ArrayList<String> findAlternatives(Set<String> correctTrigrams, String trigram) {
        DamerauLevenshtein DL = new DamerauLevenshtein(1, 1, 1, 2);
        ArrayList<String> tempArr = new ArrayList<String>();
        for (String target : correctTrigrams) {
            int dist = DL.execute(trigram, target);
            if (dist <= 2) {
                tempArr.add(target);
            }
        }

        Collections.sort(tempArr);
        return tempArr;
    }
}
