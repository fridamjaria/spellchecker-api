package com.spellchecker.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author fridamjaria
 *
 */

public class Probabilities {
    private final String language;

    public Probabilities(String langauge) {
        this.language = langauge;
    }

    static boolean upperCase(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }

        return true;
    }

    // This method is broken...
    public HashMap<String, TriNext> getProbMap() {
        HashMap<String, TriNext> mapTri = new HashMap<>(); //hashmap to store TriNext object;
        try {
            //Set the langauge for which the probabilities is checked
            InputStream probs;
            if (language.equalsIgnoreCase("isixhosa")) {
                probs = SpellcheckerApplication.class.getResourceAsStream("/xhosaProbabilities.txt");
            } else {
                probs = SpellcheckerApplication.class.getResourceAsStream("/zuluProbabilities.txt");
            }

            BufferedReader probsReader = new BufferedReader(new InputStreamReader(probs));
            //Load the wordlist
            String line = probsReader.readLine();
            HashMap<String, Integer> map;  //hashmap to get frequency of a trigram
            Scanner scTri;

            while (line != null) {
                map = new HashMap<>();
                scTri = new Scanner(line);
                while (scTri.hasNext()) {
                    String tNext = scTri.next().trim();
                    int freq = scTri.nextInt();
                    map.put(tNext, freq);
                }
                scTri.close();
                line = probsReader.readLine();
            }
            probs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapTri;
    }
}
