package com.spellchecker.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

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
    public String custom_lowercase(String word) {
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
        if (s1.substring(s1.length() - 2).equals(s2.substring(0, 2))) {
            word = s1.substring(0, s1.length() - 2) + s2;
        }
        return word;
    }

    ArrayList<String> combineSugg(ArrayList<String> sugg) {
        ArrayList<String> combo = new ArrayList<String>();
        BinarySearch BS = new BinarySearch();
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

    //method to find trigram suggestions for incorrect trigram
    public ArrayList<String> find(String[] triArray, String trigram) {
        DamerauLevenshtein DL = new DamerauLevenshtein(1, 1, 1, 2);
        ArrayList<String> tempArr = new ArrayList<String>();
        for (String target : triArray) {
            int dist = DL.execute(trigram, target);
            if (dist <= 2) {
                tempArr.add(target);
            }
        }

        Collections.sort(tempArr);
        return tempArr;
    }

    public String transpose(int index, String temp, ArrayList<Trigram> trigramsArray, HashMap<String, Integer> probabilityMap) {
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

}
