package com.spellchecker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fridamjaria
 *
 */

public class ErrorDetector {
    private HashSet<String> wordlist = new HashSet<>();
    private HashMap<String, Integer> trigramMap;
    private String language;

    public ErrorDetector(String language, HashMap<String, Integer> trigramMap, HashSet<String> wordlist){
        this.trigramMap = trigramMap;
        this.language = language;
        this.wordlist = wordlist;
    }

    public HashSet<String> detectErrors(String[] words){
        HashSet<String> misspelledWords = new HashSet<>();

        for(String word : words) {
            word = stripPunctuation(word);

            if(!(word.length() < 3 || search(word))){
                misspelledWords.add(word);

            }
        };

        return misspelledWords;
    }

    /*
     * Gets the frequency of a trigram from a storage
     * return: frequency of trigram
     */
    private int getFrequency(String trigram) {
        return trigramMap.getOrDefault(trigram, 0);
    }

    /*
     * Reads a word
     * returns: word with any trailing punctuation stripped
     */
    private String stripPunctuation(String word){
        if (!Character.isLetter(word.charAt(word.length()-1))) {
            return word.substring(0,word.length()-1 );
        }

        return word;
    }

    /*
     * Searches for a word from wordlist
     * returns true if word is in the wordlist
     */
    private boolean search(String word) {

        return wordlist.contains(word);
    }

    /*
     * Detects the errors of a word
     * returns: true if error was detected
     */
    private boolean isMispelled(String word) {
        //Get trigrams of the word
        ArrayList<String> trigrams = wordTrigram(word);
        boolean error = false;
        double frequency, threshold = 700;//Frequency for isiXhosa

        //Set frequency for isiZulu
        if(language.equalsIgnoreCase("isizulu")){
            threshold = 45;
        }

        //calculate the probability of each trigram and check for correctness
        for (String trigram : trigrams) {
            frequency = getFrequency(trigram);
            if (frequency < threshold) {
                error = false; //the trigam is incorrect
                break; //No neeed to continue iterations
            } else {
                error = true;
            }
        }

        return error;
    }

    /*
     * Reads a word word
     * returns: a list of trigrams
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
