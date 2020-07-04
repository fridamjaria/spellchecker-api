package com.spellchecker.api;

import java.util.ArrayList;

public class Trigram {
    private String tri = "";
    private ArrayList<String> suggestions;
    private boolean alternatives;

    public Trigram(String s) {
        this.tri = s;
        suggestions = new ArrayList<String>();
        alternatives = false;
    }

    public Trigram(String s, ArrayList<String> a) {
        this.tri = s;
        this.suggestions = a;
        alternatives = false;
    }

    public void setSugg(ArrayList<String> arr) {
        for (String s : arr) {
            this.suggestions.add(s);
        }
    }

    public void setAlt() {
        alternatives = true;
    }

    public String getTri() {
        return this.tri;
    }

    public ArrayList<String> getSugg() {
        return suggestions;
    }

    public boolean getAlt() {
        return alternatives;
    }
}
