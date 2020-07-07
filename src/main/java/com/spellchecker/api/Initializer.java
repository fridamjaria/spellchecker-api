package com.spellchecker.api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fridamjaria
 *
 */

public class Initializer {
    public final HashMap<String, Integer> probabilityMap;
    public final HashSet<String> wordlist;
    public final String language;

    public Initializer(String language) {
        this.language = language;
        this.wordlist = new HashSet<>();
        this.probabilityMap = new HashMap<>();
    }

    /**
     *
     * @param language
     * Initializes wordlist and trigram/Probability Map from text file into data structures
     * for quick access for the specified language.
     *
     */
    public final void initializeDataStructures(String language) {
        InputStream wordsInStream;
        InputStream probsInStream;
        int threshold;

        try {
            if (language.equalsIgnoreCase("isixhosa")){
                wordsInStream = SpellcheckerApplication.class.getResourceAsStream("/xhosaWordlist.txt");
                probsInStream = SpellcheckerApplication.class.getResourceAsStream("/xhosaTrigrams.txt");
                threshold = 700;

            }else{
                wordsInStream = SpellcheckerApplication.class.getResourceAsStream("/zuluWordlist.txt");
                probsInStream = SpellcheckerApplication.class.getResourceAsStream("/zuluTrigrams.txt");
                threshold = 45;
            }

            populateWordlist(wordsInStream);
            populateProbabilityMap(probsInStream, threshold);


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
                wordlist.add(line.trim());
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
                trigram = entry[0];
                freq = Integer.parseInt(entry[1]);

                if(freq >= threshold) probabilityMap.put(trigram, freq);
                line = probsReader.readLine();
            }

            probsReader.close();
        } catch(IOException e){
            throw e;
        }
    }
}
