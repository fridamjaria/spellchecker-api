package com.spellchecker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SpellcheckerFunctions {

    public HashMap<String, Integer> trigramMap = new HashMap<>();


    private ArrayList<String> wordTrigram(String userWord) {
        ArrayList<String> trigram = new ArrayList<>();
        int position = 0;
        String word = ignorePunctuation(userWord);
        while (position < word.length()) {
            if ((position + 3) >= word.length()) {
                trigram.add(word.substring(position, word.length()));
                position = word.length();
            } else {
                trigram.add(word.substring(position, position + 3));
                position++;
            }
        }
        return trigram;
    }

    private String ignorePunctuation(String word){
//        Pattern pattern = Pattern.compile("\\p{Punct}");
        String newWord = word.replaceAll(",.?-","");
        newWord = newWord.toLowerCase();
        return newWord;

    }

    /*
     * Detects the errors of a word
     * returns: true if error was detected
     */
    public boolean errorDetection(String userWord) {
        //Get trigrams of the word
        String word = ignorePunctuation(userWord);
        ArrayList<String> trigrams = wordTrigram(word);
        boolean error = false;
        int fre = 0;
        double probability, threshold = 0.11;
        int totalWords = trigramMap.size();

        //calculate the probability of each trigram and check for correctness
        for (String trigram : trigrams) {
            probability = (double) getFrequency(trigram) / (double) totalWords;
            if (probability < threshold) {
                error = false; //the trigram is incorrect
                break;
            } else {
                error = true;
            }
        }
        return error;
    }

    /*
     * Gets the frequency of a trigram from a storage
     * return: frequency of trigram
     */
    private int getFrequency(String trigram) {
        if (trigramMap.containsKey(trigram)) {
            return trigramMap.get(trigram);
        } else {
            return 0;
        }
    }


}
