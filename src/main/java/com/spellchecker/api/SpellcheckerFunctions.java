package com.spellchecker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fridamjaria
 * @author Nthabi Mashiane
 *
 */

public class SpellcheckerFunctions extends Initializer {
    private final String language;

    public SpellcheckerFunctions(String language) {
        this.language = language;
        initializeDataStructures(this.language);
    }

    /*
     * Performs error detection and error correction on text body
     * returns HashMap of incorrect words and their respective correction suggestions
     */
    public HashMap<String, HashSet<String>> check(String[] text) {
        ErrorDetector detector = new ErrorDetector(language, trigramMap, wordlist);
        HashSet<String> incorrect_words = detector.detectErrors(text);
        HashMap<String, HashSet<String>> corrections = new HashMap<>();

        incorrect_words.forEach(word -> {
            corrections.put(word, createSuggestions(word));
        });

        return corrections;
    }

    /*
     * Reads in a word
     * returns: a set of suggestions for given word
     */
    private HashSet<String> createSuggestions(String word){
        ErrorCorrector corrector = new ErrorCorrector();
        return correct(word);
    }
}
