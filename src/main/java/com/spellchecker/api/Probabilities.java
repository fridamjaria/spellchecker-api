package com.spellchecker.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    // Check if method is fixed...
    public HashMap<String, ArrayList<TriFreq>> getProbMap() {
        HashMap<String, ArrayList<TriFreq>> triPairsMap = new HashMap<>(); //hashmap to store trigram pairs and their frequencies
        try {
            //Set the langauge for which the probabilities is checked
            InputStream probs;
            if (language.equalsIgnoreCase("isixhosa")) {
                probs = SpellcheckerApplication.class.getResourceAsStream("/xhosaProbabilities.txt");
            } else {
                probs = SpellcheckerApplication.class.getResourceAsStream("/zuluProbabilities.txt");
            }

            BufferedReader probsReader = new BufferedReader(new InputStreamReader(probs));
            String line = probsReader.readLine();
            Scanner scanner;
            ArrayList<TriFreq> triFreqArr;
            while (line != null) {
                scanner = new Scanner(line);
                String tri = scanner.next();
                triFreqArr = new  ArrayList<TriFreq>();
                while (scanner.hasNext()) {
                    String triPair = scanner.next().trim();
                    int triPairFreq = scanner.nextInt();
                    triFreqArr.add(new TriFreq(triPair, triPairFreq));
                }
                triPairsMap.put(tri, triFreqArr);
                scanner.close();
                line = probsReader.readLine();
            }
            probs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return triPairsMap;
    }
}