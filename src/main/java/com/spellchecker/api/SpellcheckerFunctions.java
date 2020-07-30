package com.spellchecker.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
 * @author Nthabi Mashiane
 *
 */

public class SpellcheckerFunctions{
    private final String language;
    private final Initializer initializer;

    public SpellcheckerFunctions(String language) {
        this.language = language;
        this.initializer = new Initializer(this.language);
        initializer.initializeDataStructures();
    }

    /*
     * Performs error detection and error correction on text body
     * returns HashMap of incorrect words and their respective correction suggestions
     */
    public HashMap<String, List<String>> check(String[] text) {
        ErrorDetector detector = new ErrorDetector(language, initializer.probabilityMap, initializer.wordlist);
        HashSet<String> incorrect_words = detector.detectErrors(text);
        HashMap<String, List<String>> corrections = new HashMap<>();

        incorrect_words.forEach(word -> {
            HashSet<String> suggestions = createSuggestions(word);
            List<String> list = new ArrayList<String>(suggestions);
            int size = list.size();
            if(size > 1) {
                Collections.sort(list, new DLRanking(word));
                list = list.subList(0, Math.min(size-1, 5));
            }
            corrections.put(word, list);
        });

        return corrections;
    }

    /*
     * Reads in a word
     * returns: a set of suggestions for given word
     */
    private HashSet<String> createSuggestions(String word){
        ErrorCorrector corrector = new ErrorCorrector(initializer.wordlist, initializer.probabilityMap, initializer.nextTrigramPairs);
        return corrector.correct(word);
    }

    class DLRanking implements Comparator<String> {
        private String word;
        private DamerauLevenshtein DL;

        public DLRanking(String word) {
            this.DL = new DamerauLevenshtein(1, 1, 1, 2);
            this.word = word;
        }

        public int compare(String a, String b)
        {
            return DL.execute(word, a) - DL.execute(word, b);
        }
    }
}
