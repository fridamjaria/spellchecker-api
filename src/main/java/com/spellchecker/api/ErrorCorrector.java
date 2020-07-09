package com.spellchecker.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fridamjaria
 *
 */

public class ErrorCorrector extends CorrectorHelperFunctions{

    Trigram trig;
    ArrayList<String> tempArr;
    String[] triArray;
    HashMap<String, Integer> probabilityMap;
    /*
     * This is a hashmap containing all trigrams in probabilityMap and other
     * trigrams that they can be paired with (and the frequency
     * that they were paired together in the corpus)
     */
    HashMap<String, TriNext> trigramPairs;
    HashSet<String> wordlist;
    BinarySearch BS;
    ArrayList<String> suggestions;
    boolean alt;
    String language;

    public ErrorCorrector(String language, HashSet<String> wordlist, HashMap<String, Integer> probabilityMap){
        this.language = language;
        this.wordlist = wordlist;
        this.probabilityMap = probabilityMap;
        this.triArray = probabilityMap.keySet().toArray(new String[probabilityMap.size()]);

        initialize(language);
    }

    public void initialize(String language) {
        Probabilities prob = new Probabilities(language);
        trigramPairs = prob.getProbMap();
        BS = new BinarySearch();
    }

    /**
     *
     * @param sword
     * @return set of corrections for given word
     */
    public HashSet<String> correct(String sword) {
        HashSet<String> suggestions = new HashSet<>();
        ArrayList<Trigram> wordTrigrams = null;
            String word = custom_lowercase(sword);
        wordTrigrams = constructTrigrams(word); // creates trigram of word and stores it in the wordTrigrams

            // creates trigram of word and stores it in the wordTrigrams
            switch(word.length()) {
                case 1:
                wordTrigrams = new ArrayList<>(Arrays.asList(new Trigram(word + "xx")));
                break;
                case 2:
                wordTrigrams = new ArrayList<>(Arrays.asList(new Trigram(word + "x")));
                break;
                case 3:
                wordTrigrams = new ArrayList<>(Arrays.asList(new Trigram(word)));
                break;
                default:
                wordTrigrams = constructTrigrams(word);
            }

            // check the trigrams are correct from list of trigrams
            for (int i = 0; i < wordTrigrams.size(); i++) {
                Trigram trigObj = wordTrigrams.get(i);
                String trigram = trigObj.getTri();

                if (isValidTrigram(trigram)) {
                    int index = wordTrigrams.indexOf(trigObj);
                    if (index != 0) {
                        trigObj.setAlt(); // what does this do??????? --- trigObj.alternatives is set to true, why?

                        // Grab the previous trigObj to this current trigObj
                        String prevTri = wordTrigrams.get(index - 1).getTri();

                        if (trigramPairs.containsKey(prevTri)) { // if the previous trigObj is contained in pairs hashmap
                            TriNext tn = trigramPairs.get(prevTri); //grab the object containing all info for possible trigNextObj pairs
                            ArrayList<String> nextTris = tn.getArray(); // also grab the array storing just the next trigram str
                            HashMap<String, Integer> probNext = tn.getMap(); // and the map of next trigrams and their probabilities
                            if (probNext.containsKey(trigram)) {
                                trigObj.setSugg(nextTris);
                            }
                        }
                    }
                } else { //if any trigram is found to be incorrectly spelt
                    char[] charArr = trigram.toCharArray();
                    int count = 0;
                    int start_index = 0;
                    for (int a = 0; a < charArr.length; a++) {
                        char c = charArr[a];
                        if ("AEIOUaeiou".indexOf(c) == -1) {
                            if (count == 0) {
                                start_index = a;
                            count++;
                            } else if (count == 1) count = 0;
                        }


                    if (count == 2) {
                        String temp = "";
                        switch (start_index) {
                            case 0:
                                temp = Character.toString(charArr[1]) + Character.toString(charArr[0]) + Character.toString(charArr[2]);
                                break;
                            case 1:
                                temp = Character.toString(charArr[0]) + Character.toString(charArr[2]) + Character.toString(charArr[1]);
                                break;
                            default:
                                System.out.println("There's an error in the code! start_index is > 1");
                                break;
                        }

                        String newWord = transpose(i, temp, wordTrigrams, probabilityMap);
                        if (!newWord.isEmpty() && wordlist.contains(newWord)) {
                            suggestions.add(newWord);
                            break;
                        }
                    } else if (count == 3) {
                        String temp = new StringBuilder().append(charArr[1]).append(charArr[0]).append(charArr[2]).toString();
                        String newWord = transpose(i, temp, wordTrigrams, probabilityMap);
                        temp = new StringBuilder().append(charArr[0]).append(charArr[2]).append(charArr[1]).toString();
                        newWord = transpose(i, temp, wordTrigrams, probabilityMap);
                        if (!newWord.isEmpty() && wordlist.contains(newWord)) {
                            suggestions.add(newWord);
                        }
                        if (!suggestions.isEmpty()) {
                            break;
                        }
                    }

                    ArrayList<String> targetArr = find(triArray, trigram);
                    wordTrigrams.get(i).setSugg(targetArr);
                }
            }

            if (suggestions.isEmpty() && !alt) {
                suggestions = createSugg(wordTrigrams);

            }
        }} catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    /**
     *
     * @param wordTrigrams
     * @return Set of candidate corrections for misspelled word
     */
    public HashSet<String> createSugg(ArrayList<Trigram> wordTrigrams) {
        HashSet<String> wordSugg = new HashSet<>();
        ArrayList<String> suggCombo; //stores substrings from combining suggestions - done to find corrections for deletion errors
        tempArr = new ArrayList<String>();
        for (int i = 0; i < wordTrigrams.size(); i++) {
            Trigram trig = wordTrigrams.get(i);
            ArrayList<String> suggestedTrigs = trig.getSugg();
            int size = suggestedTrigs.size();

            if (i == 0) {
                if (size == 0) { //if trigram is correct
                    tempArr.add(trig.getTri());
                } else {
                    for (String tri : suggestedTrigs) {
                        tempArr.add(tri);
                    }

                    suggCombo = combineSugg(suggestedTrigs);
                    if (!suggCombo.isEmpty()) {
                        for (String s : suggCombo) {
                            tempArr.add(s);
                        }
                    }

                    for (String s : tempArr) {
                        if (wordlist.contains(s) && s.length() > 3) wordSugg.add(s);
                    }
                }

            } else {
                int tempSize = tempArr.size();
                String tri = trig.getTri();
                if (suggestedTrigs.size() == 0) { //if trigram is correct
                    for (int j = 0; j < tempSize; j++) {
                        String str = tempArr.get(j);
                        String combined_str = combine(str, tri);
                        if (!combined_str.isEmpty()) {
                            tempArr.add(combined_str);
                            //check if combined_str is in the wordlist, is it is store it as a suggestion
                            if (wordlist.contains(combined_str)) {
                                if (!wordSugg.contains(combined_str)) {
                                    wordSugg.add(combined_str);
                                }
                            }
                        }
                    }
                } else { //if trigram is incorrect
                    //create combinations from suggestions and then combine these with strings in tempArr
                    suggCombo = combineSugg(trig.getSugg());
                    if (!suggCombo.isEmpty()) {
                        int len = suggCombo.size() - 1;
                        for (int j = 0; j < tempSize; j++) {
                            String str = tempArr.get(j);

                            int start = BS.findStart(suggCombo, str, 0, len);
                            if (!(start < 0)) {
                                int end = BS.findEnd(suggCombo, str, start, len);
                                if (end < 0) {
                                    System.out.println("Something wrong with suggCombo from createSugg method.");
                                    System.exit(-1);
                                }

                                for (int k = start; k <= end; k++) {
                                    String combined_str = combine(str, suggCombo.get(k));

                                    if (!combined_str.isEmpty()) {
                                        tempArr.add(combined_str);
                                        if (wordlist.contains(combined_str)) {
                                            if (!wordSugg.contains(combined_str)) {
                                                wordSugg.add(combined_str);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //combine strings in tempArr with suggestions from trig.suggestions
                    ArrayList<String> sugg = trig.getSugg();
                    for (int j = 0; j < tempSize; j++) {
                        String str = tempArr.get(j);
                        int start = BS.findStart(sugg, str, 0, size - 1);
                        if (!(start < 0)) {
                            int end = BS.findEnd(sugg, str, start, size - 1);
                            if (end < 0) {
                                System.out.println("Something wrong with suggCombo from createSugg method.");
                                System.exit(0);
                            }

                            for (int k = start; k <= end; k++) {
                                if (i == 1) {
                                }
                                String combined_str = combine(str, sugg.get(k));
                                if (!combined_str.isEmpty()) {
                                    tempArr.add(combined_str);
                                    if (wordlist.contains(combined_str)) {
                                        if (!wordSugg.contains(combined_str)) {
                                            wordSugg.add(combined_str);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //remove strings that combinations have already been done on from tempArr
                for (int m = 0; m < tempSize; m++) {
                    tempArr.remove(0);
                }
            }
        }
        return wordSugg;
    }

    private Boolean isValidTrigram(String trigram) {
        return !probabilityMap.containsKey(trigram);
    }

}
