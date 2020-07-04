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

public class Initializer {
    public final HashMap<String, Integer> trigramMap = new HashMap<>();
    public final HashSet<String> wordlist = new HashSet<>();

    /**
     *
     * @param language
     * Initializes wordlist and trigram/Probability Map from text file into data structures
     * for quick access for the specified language.
     *
     */
    public final void initializeDataStructures(String language) {
        try {
            InputStream wdlist;
            InputStream tri;

            if (language.equalsIgnoreCase("isixhosa")){
                wdlist = SpellcheckerApplication.class.getResourceAsStream("/xhosaWordlist.txt");
                tri = SpellcheckerApplication.class.getResourceAsStream("/xhosaTrigrams.txt");

            }else{

                wdlist = SpellcheckerApplication.class.getResourceAsStream("/zuluWordlist.txt");
                tri = SpellcheckerApplication.class.getResourceAsStream("/zuluTrigrams.txt");
            }


            BufferedReader wdReader = new BufferedReader(new InputStreamReader(wdlist));
            BufferedReader trigramReader = new BufferedReader(new InputStreamReader(tri));

            //Load the wordlist
            String line = wdReader.readLine();
            while (line != null) {
                wordlist.add(line.trim());
                line = wdReader.readLine();
            }
            wdlist.close();

            //Load the trigram
            line = trigramReader.readLine();
            String[] entry;
            while (line != null) {
                entry = line.split(" ");//trigram and frequency
                trigramMap.put(entry[0], Integer.parseInt(entry[1]));
                line = trigramReader.readLine();
            }
            tri.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException ex) {
            Logger.getLogger(SpellcheckerFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
