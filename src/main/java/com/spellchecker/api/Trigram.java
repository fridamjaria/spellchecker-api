package com.spellchecker.api;

import java.util.ArrayList;

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
