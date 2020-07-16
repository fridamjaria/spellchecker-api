package com.spellchecker.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
 *
 */

public class Probabilities {
    private final String language;

    public Probabilities(String langauge) {
        this.language = langauge;
    }

    // Check if method is fixed...
    public HashMap<String, ArrayList<TriFreq>> getProbMap(HashMap<String, Integer> probabilityMap) {
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
                    if(probabilityMap.containsKey(triPair)) triFreqArr.add(new TriFreq(triPair, triPairFreq));
                }

                if(probabilityMap.containsKey(tri)) triPairsMap.put(tri, triFreqArr);
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