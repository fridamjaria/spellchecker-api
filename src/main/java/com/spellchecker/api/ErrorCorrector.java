package com.spellchecker.api;

import java.io.*;
import java.util.*;

/**
 *
 * @author fridamjaria
 *
 */

public class ErrorCorrector {

    static ArrayList<Trigram> arrTrig = null;
    static Trigram trig;
    static int true_negative = 0;
    static int false_positive = 0;
    static ArrayList<String> tempArr;
    static ArrayList<String> triArray;
    static HashMap<String, Integer> hashTri;
    static HashMap<String, TriNext> hashAlt;
    static Set<String> wordlist;
    static DamerauLevenshtein DL;
    static BinarySearch bs;
    static ArrayList<String> suggestions;
    static boolean alt;

    public void initCorrector(String language) {
        try {
            //create HashMap for Trigrams and arraylist for iterating through
            hashTri = new HashMap<>();
            Probabilities prob = new Probabilities(language);
            hashAlt = prob.getProbMap();
            triArray = new ArrayList<>();

            InputStream probs, words;

            //Set the language to be used by the corrector
            if (language.equalsIgnoreCase("isixhosa")) {
                probs = SpellcheckerApplication.class.getResourceAsStream("/xhosaTrigrams.txt");
                words = SpellcheckerApplication.class.getResourceAsStream("/xhosaWordlist.txt");
            } else {
                probs = SpellcheckerApplication.class.getResourceAsStream("/zuluTrigrams.txt");
                words = SpellcheckerApplication.class.getResourceAsStream("/zuluWordlist.txt");
            }

            BufferedReader probsReader = new BufferedReader(new InputStreamReader(probs));
            //Load the wordlist
            String inputline = probsReader.readLine();

            int threshold = 700; //IsiXhosa

            //Set frequency for isiZulu
            if (language.equalsIgnoreCase("isizulu")) {
                threshold = 45;
            }

            while (inputline != null) {
                String[] line = inputline.split(" ");
                String tri = line[0];
                int freq = Integer.parseInt(line[1]);
                if (freq >= threshold) {
                    hashTri.put(tri, freq);
                    triArray.add(tri);
                }
                inputline = probsReader.readLine();
            }

            //create hashset for wordlist
            wordlist = new HashSet<>();

            BufferedReader wordReader = new BufferedReader(new InputStreamReader(words));

            //Load the wordlist
            String wordsline = wordReader.readLine();

            while (wordsline != null) {
                wordlist.add(wordsline.trim());
                wordsline = wordReader.readLine();
            }

            DL = new DamerauLevenshtein(1, 1, 1, 2);
            bs = new BinarySearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*****
     *
     * @param sword
     * @return set of corrections for given word
     */
    public HashSet<String> correct(String sword) {
        HashSet<String> suggestions = new HashSet<>();

        try {
            String word = custom_lowercase(sword);

            //create trigram of word and store in an arrTrig
            int len = word.length();
            arrTrig = new ArrayList<>();
            if (len >= 4) arrTrig = triConstruct(word);

            else {
                if (len == 1) {
                    trig = new Trigram(word + "xx");
                    arrTrig.add(trig);
                } else if (len == 2) {
                    trig = new Trigram(word + "x");
                    arrTrig.add(trig);
                } else if (len == 3) {
                    trig = new Trigram(word);
                    arrTrig.add(trig);
                }
            }
            //check the trigrams are correct from list of trigrams
            for (int i = 0; i < arrTrig.size(); i++) {
                boolean correct = true;
                String source = arrTrig.get(i).getTri();

                if (!hashTri.containsKey(source)) {
                    correct = false;
                }
                if (correct) {
                    if (i != 0) {
                        arrTrig.get(i).setAlt();
                        String prevTri = arrTrig.get(i - 1).getTri();
                        //get TriNext object if the trigram occurs
                        if (hashAlt.containsKey(prevTri)) {
                            TriNext tn = hashAlt.get(prevTri);
                            ArrayList<String> nextTris = tn.getArray();
                            HashMap<String, Integer> probNext = tn.getMap();
                            if (probNext.containsKey(source)) {
                                arrTrig.get(i).setSugg(nextTris);
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

                        String newWord = transposition(i, temp, arrTrig);
                        if (!newWord.isEmpty() && wordlist.contains(newWord)) {
                            suggestions.add(newWord);
                            break;
                        }
                    } else if (count == 3) {
                        String temp = new StringBuilder().append(charArr[1]).append(charArr[0]).append(charArr[2]).toString();
                        String newWord = transposition(i, temp, arrTrig);
                        temp = new StringBuilder().append(charArr[0]).append(charArr[2]).append(charArr[1]).toString();
                        newWord = transposition(i, temp, arrTrig);
                        if (!newWord.isEmpty() && wordlist.contains(newWord)) {
                            suggestions.add(newWord);
                        }
                        if (!suggestions.isEmpty()) {
                            break;
                        }
                    }

                    ArrayList<String> targetArr = find(source);
                    arrTrig.get(i).setSugg(targetArr);
                }
            }

            if (suggestions.isEmpty() && !alt) {
                suggestions = createSugg(arrTrig);

            }

            if (!alt) {
                true_negative++;
            } else {
                false_positive++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suggestions;
    }

    public static String custom_lowercase(String word) {
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

    public static String transposition(int index, String temp, ArrayList<Trigram> arrTrig) {
        if (hashTri.containsKey(temp)) {
            String combo = "";
            int size = arrTrig.size();

            if (index == 0 && index < size - 1) { //if 1st element and array has more than 1 element
                combo = temp + arrTrig.get(index + 1).getTri().substring(2);
                if (index + 2 < size) {
                    combo += arrTrig.get(index + 2).getTri().substring(2);

                    for (int i = index + 3; i < size; i++) {
                        combo = combine(combo, arrTrig.get(i).getTri());
                        if (combo.isEmpty()) {
                            break;
                        }
                    }

                }
                return combo;
            } else if (index == size - 1 && size != 1) { //if last element and array has more than one element
                for (int i = 0; i < index - 1; i++) {
                    if (i == 0) {
                        combo = arrTrig.get(0).getTri();
                    } else {
                        combo = combine(combo, arrTrig.get(i).getTri());
                    }
                    if (combo.isEmpty()) {
                        return combo;
                    }
                }
                combo += arrTrig.get(index - 1).getTri().substring(0, 1) + temp;
                return combo;
            } else if (index > 0) { //if index is somewhere in the middle
                for (int i = 0; i < index - 1; i++) {
                    if (i == 0) {
                        combo = arrTrig.get(0).getTri();
                    } else {
                        combo = combine(combo, arrTrig.get(i).getTri());

                    }
                    if (combo.isEmpty()) {
                        return combo;
                    }

                }

                if (combo.isEmpty()) {
                    combo = arrTrig.get(index - 1).getTri().substring(0, 1) + temp + arrTrig.get(index + 1).getTri().substring(2);
                } else {
                    combo = combine(combo, arrTrig.get(index - 1).getTri().substring(0, 1) + temp + arrTrig.get(index + 1).getTri().substring(2));
                }

                if (combo.isEmpty()) {
                    return combo;
                }

                for (int i = index + 2; i < size; i++) {
                    if (i == index + 2) {
                        combo += arrTrig.get(i).getTri().substring(2);
                    } else {
                        combo = combine(combo, arrTrig.get(i).getTri());
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
    public static ArrayList<String> find(String source) {
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
     * @param arrTrig
     * @return Set of candidate corrections for misspelled word
     */
    public static HashSet<String> createSugg(ArrayList<Trigram> arrTrig) {
        HashSet<String> wordSugg = new HashSet<>();
        ArrayList<String> suggCombo; //stores substrings from combining suggestions - done to find corrections for deletion errors
        tempArr = new ArrayList<String>();
        for (int i = 0; i < arrTrig.size(); i++) {
            Trigram trig = arrTrig.get(i);
            ArrayList<String> suggestedTrigs = trig.getSugg();

            if (i == 0) {
                if (suggestedTrigs.size() == 0) { //if trigram is correct
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
                        String str_combine = combine(str, tri);
                        if (!str_combine.isEmpty()) {
                            tempArr.add(str_combine);
                            //check if str_combine is in the wordlist, is it is store it as a suggestion
                            if (wordlist.contains(str_combine)) {
                                if (!wordSugg.contains(str_combine)) {
                                    wordSugg.add(str_combine);
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

                            int start = bs.findStart(suggCombo, str, 0, len);
                            if (!(start < 0)) {
                                int end = bs.findEnd(suggCombo, str, start, len);
                                if (end < 0) {
                                    System.out.println("Something wrong with suggCombo from createSugg method.");
                                    System.exit(-1);
                                }

                                for (int k = start; k <= end; k++) {
                                    String str_combine = combine(str, suggCombo.get(k));

                                    if (!str_combine.isEmpty()) {
                                        tempArr.add(str_combine);
                                        if (wordlist.contains(str_combine)) {
                                            if (!wordSugg.contains(str_combine)) {
                                                wordSugg.add(str_combine);
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
                        int start = bs.findStart(sugg, str, 0, size - 1);
                        if (!(start < 0)) {
                            int end = bs.findEnd(sugg, str, start, size - 1);
                            if (end < 0) {
                                System.out.println("Something wrong with suggCombo from createSugg method.");
                                System.exit(0);
                            }

                            for (int k = start; k <= end; k++) {
                                if (i == 1) {
                                }
                                String str_combine = combine(str, sugg.get(k));
                                if (!str_combine.isEmpty()) {
                                    tempArr.add(str_combine);
                                    if (wordlist.contains(str_combine)) {
                                        if (!wordSugg.contains(str_combine)) {
                                            wordSugg.add(str_combine);
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

    static ArrayList<String> combineSugg(ArrayList<String> sugg) {
        ArrayList<String> combo = new ArrayList<String>();
        int high = sugg.size() - 1;
        for (String s : sugg) {
            int start = bs.findStart(sugg, s, 0, high);
            if (!(start < 0)) {
                int end = bs.findEnd(sugg, s, start, high);
                if (end < 0) {
                    System.out.println("Something wrong with combineSugg");
                    System.exit(0);
                }
                for (int i = start; i <= end; i++) {
                    String str_combine = combine(s, sugg.get(i));
                    if (!str_combine.isEmpty()) {
                        combo.add(str_combine);
                    }
                }
            }
        }
        return combo;
    }

    static String combine(String s1, String s2) {
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

    static ArrayList<Trigram> triConstruct(String word) {
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
