package com.spellchecker.api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class Initializer {
    public final HashMap<String, ArrayList<String>> nextTrigramPairs;
    public final HashMap<String, Integer> probabilityMap;
    public final HashSet<String> wordlist;
    public final String language;

    public Initializer(String language) {
        this.language = language;
        this.wordlist = new HashSet<>();
        this.probabilityMap = new HashMap<>();
        this.nextTrigramPairs = new HashMap<>();
    }

    /**
     *
     * @param language
     * Initializes wordlist and trigram/Probability Map from text file into data structures
     * for quick access for the specified language.
     *
     */
    public final void initializeDataStructures() {
        InputStream wordsInStream;
        InputStream probsInStream;
        InputStream nextTriInStream;
        int threshold;

        try {
            if (language.equalsIgnoreCase("isixhosa")){
                wordsInStream = SpellcheckerApplication.class.getResourceAsStream("/xhosaWordlist.txt");
                probsInStream = SpellcheckerApplication.class.getResourceAsStream("/xhosaTrigrams.txt");
                nextTriInStream = SpellcheckerApplication.class.getResourceAsStream("/xhosaProbabilities.txt");
                threshold = 700;

            }else{
                wordsInStream = SpellcheckerApplication.class.getResourceAsStream("/zuluWordlist.txt");
                probsInStream = SpellcheckerApplication.class.getResourceAsStream("/zuluTrigrams.txt");
                nextTriInStream = SpellcheckerApplication.class.getResourceAsStream("/zuluProbabilities.txt");
                threshold = 100;
            }

            populateWordlist(wordsInStream);
            populateProbabilityMap(probsInStream, threshold);
            populateNextTrigramPairs(nextTriInStream);


        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            Logger.getLogger(SpellcheckerFunctions.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     *
     * @param stream
     * @throws IOException
     * Populates wordlist with words input stream data
     *
     */
    private void populateWordlist(InputStream stream) throws IOException {
        BufferedReader wdReader = new BufferedReader(new InputStreamReader(stream));

        try {
            String line = wdReader.readLine();
            while (line != null) {
                if(!line.isBlank()) wordlist.add(line.trim());
                line = wdReader.readLine();
            }
            wdReader.close();
        } catch(IOException e){
            throw e;
        }
    }


    /**
     *
     * @param stream
     * @throws IOException
     * Populates probabilitiesMap with trigrams that have a freq >= threshold
     *
     */
    private void populateProbabilityMap(InputStream stream, int threshold) throws IOException {
        BufferedReader probsReader = new BufferedReader(new InputStreamReader(stream));

        try {
            String[] entry;
            String trigram;
            int freq;

            String line = probsReader.readLine();
            while (line != null) {
                entry = line.split(" ");
                trigram = entry[0].trim();
                freq = Integer.parseInt(entry[1]);

                if(freq >= threshold && trigram.length() == 3) probabilityMap.put(trigram, freq);
                line = probsReader.readLine();
            }

            probsReader.close();
        } catch(IOException e){
            throw e;
        }
    }

    /**
     *
     * @param stream
     * @throws IOException
     * Populateds DS to be used in corrector as suggestions for correctly spelled trigrams
     *
     */
    private void populateNextTrigramPairs(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        try{
            String line = reader.readLine();
            Scanner scanner;

            while(line != null){
                ArrayList<String> list = new ArrayList<>();
                scanner = new Scanner(line);
                String key = scanner.next();

                while(scanner.hasNext()){
                    list.add(scanner.next());
                    scanner.nextInt();
                }

                scanner.close();
                nextTrigramPairs.put(key, list);

                line = reader.readLine();
            }

            reader.close();
        } catch(IOException e){
            throw e;
        }
    }
}
