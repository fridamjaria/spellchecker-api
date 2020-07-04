package com.spellchecker.api;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fridamjaria
 * @author Nthabi Mashiane
 *
 */

public class SpellcheckerFunctions extends ErrorCorrector {

    public HashMap<String, Integer> trigramMap = new HashMap<>();
    private final HashSet<String> wordlist = new HashSet<>();
    public final HashSet<String> dictionary = new HashSet<>(); //Stores words added by the user
    private final StringBuilder dictDatabas = new StringBuilder(); //to make writing back to file easier
    private final String language;

    public SpellcheckerFunctions(String language) {
        this.language = language;
        initialize();
        initCorrector(language);
    }

    /*
     * Performs error detection and error correction on text body
     * returns HashMap of incorrect words and their respective correction suggestions
     */
    public HashMap<String, HashSet<String>> check(String[] text) {
        ErrorDetector detector = new ErrorDetector(language, trigramMap, wordlist)
        HashSet<String> incorrect_words = detector.detectErrors(text);
        HashMap<String, HashSet<String>> corrections = new HashMap<>();

        incorrect_words.forEach(word -> {
            corrections.put(word, createSuggestions(word));
        });

        return corrections;
    }

    /*
     * Reads in a word
     * returns: a set of suggestions for given word
     */
    private HashSet<String> createSuggestions(String word){
        return correct(word);
    }

    /*
     * Initializes wordlist, loads from text file into a hashset for quick access
     */
    private void initialize() {
        try {
            InputStream wdlist;
            InputStream tri;
            File dict;

            if (language.equalsIgnoreCase("isixhosa")){
                wdlist = SpellcheckerApplication.class.getResourceAsStream("/xhosaWordlist.txt");
                tri = SpellcheckerApplication.class.getResourceAsStream("/xhosaTrigrams.txt");
                dict = new File("user_xhosa_dictionary");

            }else{

                wdlist = SpellcheckerApplication.class.getResourceAsStream("/zuluWordlist.txt");
                tri = SpellcheckerApplication.class.getResourceAsStream("/zuluTrigrams.txt");
                dict = new File("user_zulu_dictionary");
            }


            BufferedReader wdReader = new BufferedReader(new InputStreamReader(wdlist));
            BufferedReader trigramReader = new BufferedReader(new InputStreamReader(tri));

            // Load user dictionary if it exists
            String line;
            if (dict.exists()) {
                BufferedReader dictReader = new BufferedReader(new FileReader(dict));

                line = dictReader.readLine();
                while (line != null) {
                    dictionary.add(line.trim());
                    dictDatabas.append(line.trim());
                    line = dictReader.readLine();
                }
                dictReader.close();
            }

            //Load the wordlist
            line = wdReader.readLine();
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

    /*
     * Adds a new User word to user's personal dictionary
     */
    public void addWord(String word) {
        BufferedWriter bw = null;
        String file_name = language.equalsIgnoreCase("isixhosa") ? "user_xhosa_dictionary" : "user_zulu_dictionary";
        File dict = new File(file_name);

        try {
            bw = new BufferedWriter(new FileWriter(dict));
            if (!dict.exists() && dict.createNewFile()) {
                dictionary.add(word);
                dictDatabas.append(word);
                bw.write(dictDatabas.toString());
            } else {
                dictionary.add(word);
                dictDatabas.append("\n" + word);
                bw.write(dictDatabas.toString());
            }
        } catch (IOException e) {
            Logger.getLogger(SpellcheckerFunctions.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(SpellcheckerFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
