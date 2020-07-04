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
     * Checks word in wordlist before doing error detection
     * returns true if correct
     */
    public boolean check(String word) {
        String str = word;

        //Removes non-Alphabetic last char
        if (!Character.isLetter(str.charAt(word.length()-1))) {
            str = word.substring(0,word.length()-1 );
        }

        if(str.length() < 3){
            return true;
        }
        else if (search(str)) {
            //finds word in wordlist or dictionary
            return true;
        } else {
            //uses trigrams for a new word
            return errorDetection(str);
        }
    }
    
    /*
     * Searches for a word from wordlist
     * returns true if word is in the wordlist
     */
    private boolean search(String word) {

        return wordlist.contains(word) || dictionary.contains(word);
    }

    /*
     * Detects the errors of a word
     * returns: true if error was detected
     */
    private boolean errorDetection(String word) {
        //Get trigrams of the word
        ArrayList<String> trigrams = wordTrigram(word);
        boolean error = false;
        double frequency, threshold = 700;//Frequency for isiXhosa

        //Set frequency for isiZulu
        if(language.equalsIgnoreCase("isizulu")){
            threshold = 45;
        }

        //calculate the probability of each trigram and check for correctness
        for (String trigram : trigrams) {
            frequency = getFrequency(trigram);
            if (frequency < threshold) {
                error = false; //the trigam is incorrect
                break; //No neeed to continue iterations
            } else {
                error = true;
            }
        }
        return error;
    }

    /*
     * Reads a word word
     * returns: a list of trigrams
     */
    private ArrayList<String> wordTrigram(String word) {

        ArrayList<String> strTri = new ArrayList<>();
        int pos = 0;
        while (pos < word.length()) {
            if ((pos + 3) >= word.length()) {
                strTri.add(word.substring(pos));
                pos = word.length();
            } else {
                strTri.add(word.substring(pos, pos + 3));
                pos = pos + 1;
            }
        }
        return strTri;
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
                wdlist = SpellcheckerApplication.class.getResourceAsStream("resources/xhosaWordlist.txt");
                tri = SpellcheckerApplication.class.getResourceAsStream("resources/xhosaTrigrams.txt");
                dict = new File("user_xhosa_dictionary");

            }else{

                wdlist = SpellcheckerApplication.class.getResourceAsStream("resources/zuluWordlist.txt");
                tri = SpellcheckerApplication.class.getResourceAsStream("resources/zuluTrigrams.txt");
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
     * Gets the frequency of a trigram from a storage
     * return: frequency of trigram
     */
    private int getFrequency(String trigram) {
        return trigramMap.getOrDefault(trigram, 0);
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
