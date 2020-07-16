package com.spellchecker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

public class ErrorDetector {
    private HashSet<String> wordlist = new HashSet<>();
    private HashMap<String, Integer> probabilityMap;
    private String language;

    public ErrorDetector(String language, HashMap<String, Integer> probabilityMap, HashSet<String> wordlist){
        this.probabilityMap = probabilityMap;
        this.language = language;
        this.wordlist = wordlist;
    }

    /**
     *
     * @param words
     * Does the error detection
     * @return hashset of misspelled words
     */
    public HashSet<String> detectErrors(String[] words){
        HashSet<String> misspelledWords = new HashSet<>();

        for(String word : words) {
            word = stripPunctuation(word);

            if(!(word.length() < 3 || isInWordlist(word)) && isMispelled(word)){
                misspelledWords.add(word);
            }
        };

        return misspelledWords;
    }

    /**
     *
     * @param word
     * Gets the frequency of a trigram from probabilityMap (default == 0 for trigrams not found)
     * @return frequency of trigram
     */
    private int getFrequency(String trigram) {
        return probabilityMap.getOrDefault(trigram, 0);
    }

    /**
     *
     * @param word
     * Reads a word
     * @returns word with any trailing punctuation stripped
     */
    private String stripPunctuation(String word){
        return word.replaceAll("[^a-zA-Z0-9']", "");
    }

    /**
     *
     * @param word
     * Searches for a word from wordlist
     * @return true if word is in the wordlist || false otherwise
     *
     */
    private boolean isInWordlist(String word) {
        return wordlist.contains(word);
    }

    /**
     *
     * @param word
     * Detects the errors of a word
     * @return true if error was detected || false otherwise
     *
     */
    private boolean isMispelled(String word) {
        ArrayList<String> trigrams = wordTrigram(word);
        double frequency, threshold = 700;//Frequency for isiXhosa

        // sets frequency for isiZulu
        if(language.equalsIgnoreCase("isizulu")){
            threshold = 45;
        }

        // calculate the probability of each trigram and check for correctness
        for (String trigram : trigrams) {
            frequency = getFrequency(trigram);
            if (frequency < threshold) {
                return true; // trigam is incorrect, therefore word is mispelled
            }
        }

        return false;
    }

    /**
     *
     * @param word
     * Breaks up a word into its trigram constituents
     * @return a list of trigrams
     *
     */
    private ArrayList<String> wordTrigram(String word) {

        ArrayList<String> strTri = new ArrayList<>();
        int pos = 0;
        while (pos < word.length()) {
            if ((pos + 3) >= word.length()) {
                strTri.add(word.substring(pos));
                pos = word.length();
            } else {
                strTri.add(word.substring(pos, pos + 3));
                pos = pos + 1;
            }
        }
        return strTri;
    }

}
