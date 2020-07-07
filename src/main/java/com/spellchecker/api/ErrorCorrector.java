package com.spellchecker.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/**
 *
 * @author fridamjaria
 *
 */

public class ErrorCorrector {

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
    DamerauLevenshtein DL;
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

        DL = new DamerauLevenshtein(1, 1, 1, 2);
        BS = new BinarySearch();
    }

    /**
     *
     * @param sword
     * @return set of corrections for given word
     */
    public HashSet<String> correct(String sword) {
        HashSet<String> suggestions = new HashSet<>();
        ArrayList<Trigram> trigramsArray = null;

        try {
            String word = custom_lowercase(sword);

            // creates trigram of word and stores it in the trigramsArray
            switch(word.length()) {
                case 1:
                trigramsArray = new ArrayList<>(Arrays.asList(new Trigram(word + "xx")));
                break;
                case 2:
                trigramsArray = new ArrayList<>(Arrays.asList(new Trigram(word + "x")));
                break;
                case 3:
                trigramsArray = new ArrayList<>(Arrays.asList(new Trigram(word)));
                break;
                default:
                trigramsArray = constructTrigrams(word);
            }

            // check the trigrams are correct from list of trigrams
            for (int i = 0; i < trigramsArray.size(); i++) {
                boolean correct = true;
                String source = trigramsArray.get(i).getTri();

                if (!probabilityMap.containsKey(source)) {
                    correct = false;
                }
                if (correct) {
                    if (i != 0) {
                        trigramsArray.get(i).setAlt();
                        String prevTri = trigramsArray.get(i - 1).getTri();
                        //get TriNext object if the trigram occurs
                        if (trigramPairs.containsKey(prevTri)) {
                            TriNext tn = trigramPairs.get(prevTri);
                            ArrayList<String> nextTris = tn.getArray();
                            HashMap<String, Integer> probNext = tn.getMap();
                            if (probNext.containsKey(source)) {
                                trigramsArray.get(i).setSugg(nextTris);
                            }
                        }
                    }
                } else { //if any trigram is found to be incorrectly spelt
                    char[] charArr = source.toCharArray();
                    int count = 0;
                    int start_index = 0;
                    for (int a = 0; a < charArr.length; a++) {
                        char c = charArr[a];
                        if ("AEIOUaeiou".indexOf(c) == -1) {
                            if (count == 0) {
                                start_index = a;
                            }
                            count++;
                        } else {
                            if (count == 1) {
                                count = 0;
                            }
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

                        String newWord = transposition(i, temp, trigramsArray);
                        if (!newWord.isEmpty() && wordlist.contains(newWord)) {
                            suggestions.add(newWord);
                            break;
                        }
                    } else if (count == 3) {
                        String temp = new StringBuilder().append(charArr[1]).append(charArr[0]).append(charArr[2]).toString();
                        String newWord = transposition(i, temp, trigramsArray);
                        temp = new StringBuilder().append(charArr[0]).append(charArr[2]).append(charArr[1]).toString();
                        newWord = transposition(i, temp, trigramsArray);
                        if (!newWord.isEmpty() && wordlist.contains(newWord)) {
                            suggestions.add(newWord);
                        }
                        if (!suggestions.isEmpty()) {
                            break;
                        }
                    }

                    ArrayList<String> targetArr = find(source);
                    trigramsArray.get(i).setSugg(targetArr);
                }
            }

            if (suggestions.isEmpty() && !alt) {
                suggestions = createSugg(trigramsArray);

            }
        }} catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    public String custom_lowercase(String word) {
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

    public String transposition(int index, String temp, ArrayList<Trigram> trigramsArray) {
        if (probabilityMap.containsKey(temp)) {
            String combo = "";
            int size = trigramsArray.size();

            if (index == 0 && index < size - 1) { //if 1st element and array has more than 1 element
                combo = temp + trigramsArray.get(index + 1).getTri().substring(2);
                if (index + 2 < size) {
                    combo += trigramsArray.get(index + 2).getTri().substring(2);

                    for (int i = index + 3; i < size; i++) {
                        combo = combine(combo, trigramsArray.get(i).getTri());
                        if (combo.isEmpty()) {
                            break;
                        }
                    }

                }
                return combo;
            } else if (index == size - 1 && size != 1) { //if last element and array has more than one element
                for (int i = 0; i < index - 1; i++) {
                    if (i == 0) {
                        combo = trigramsArray.get(0).getTri();
                    } else {
                        combo = combine(combo, trigramsArray.get(i).getTri());
                    }
                    if (combo.isEmpty()) {
                        return combo;
                    }
                }
                combo += trigramsArray.get(index - 1).getTri().substring(0, 1) + temp;
                return combo;
            } else if (index > 0) { //if index is somewhere in the middle
                for (int i = 0; i < index - 1; i++) {
                    if (i == 0) {
                        combo = trigramsArray.get(0).getTri();
                    } else {
                        combo = combine(combo, trigramsArray.get(i).getTri());

                    }
                    if (combo.isEmpty()) {
                        return combo;
                    }

                }

                if (combo.isEmpty()) {
                    combo = trigramsArray.get(index - 1).getTri().substring(0, 1) + temp + trigramsArray.get(index + 1).getTri().substring(2);
                } else {
                    combo = combine(combo, trigramsArray.get(index - 1).getTri().substring(0, 1) + temp + trigramsArray.get(index + 1).getTri().substring(2));
                }

                if (combo.isEmpty()) {
                    return combo;
                }

                for (int i = index + 2; i < size; i++) {
                    if (i == index + 2) {
                        combo += trigramsArray.get(i).getTri().substring(2);
                    } else {
                        combo = combine(combo, trigramsArray.get(i).getTri());
                    }
                    if (combo.isEmpty()) {
                        break;
                    }
                }
                return combo;
            }

        }
        return temp;
    }

    //method to find trigram suggestions for incorrect trigram
    public ArrayList<String> find(String source) {
        ArrayList<String> tempArr = new ArrayList<String>();
        for (String target : triArray) {
            int dist = DL.execute(source, target);
            if (dist <= 2) {
                tempArr.add(target);
            }
        }

        Collections.sort(tempArr);
        return tempArr;
    }

    /**
     *
     * @param trigramsArray
     * @return Set of candidate corrections for misspelled word
     */
    public HashSet<String> createSugg(ArrayList<Trigram> trigramsArray) {
        HashSet<String> wordSugg = new HashSet<>();
        ArrayList<String> suggCombo; //stores substrings from combining suggestions - done to find corrections for deletion errors
        tempArr = new ArrayList<String>();
        for (int i = 0; i < trigramsArray.size(); i++) {
            Trigram trig = trigramsArray.get(i);
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

    ArrayList<String> combineSugg(ArrayList<String> sugg) {
        ArrayList<String> combo = new ArrayList<String>();
        int high = sugg.size() - 1;
        for (String s : sugg) {
            int start = BS.findStart(sugg, s, 0, high);
            if (!(start < 0)) {
                int end = BS.findEnd(sugg, s, start, high);
                if (end < 0) {
                    System.out.println("Something wrong with combineSugg");
                    System.exit(0);
                }
                for (int i = start; i <= end; i++) {
                    String combined_str = combine(s, sugg.get(i));
                    if (!combined_str.isEmpty()) {
                        combo.add(combined_str);
                    }
                }
            }
        }
        return combo;
    }

    String combine(String s1, String s2) {
        String word = "";
        if (s1.substring(s1.length() - 2).equals(s2.substring(0, 2))) {
            word = s1.substring(0, s1.length() - 2) + s2;
        }
        return word;
    }

    /**
     *
     * @param word
     * @return array with trigrams of word
     */

    ArrayList<Trigram> constructTrigrams(String word) {
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

}
