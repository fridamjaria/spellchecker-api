package com.spellchecker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

public class ErrorCorrector extends CorrectorHelperFunctions {

  private HashSet<String> wordlist;
  private Set<String> correctTrigrams;
  private HashMap<String, ArrayList<String>> trigramPairs;
  private BinarySearch BS;

  public ErrorCorrector(HashSet<String> wordlist, HashMap<String, Integer> probabilityMap, HashMap<String, ArrayList<String>> trigramPairs) {
    this.wordlist = wordlist;
    this.trigramPairs = trigramPairs;
    this.correctTrigrams = probabilityMap.keySet();
    this.BS = new BinarySearch();
  }

  /**
   *
   * @param incorrectWord
   * @return set of corrections for misspelled word
   */
  public HashSet<String> correct(String incorrectWord) {
    HashSet<String> suggestions = new HashSet<>();
    ArrayList<Trigram> wordTrigrams = constructTrigrams(customLowercase(incorrectWord));
    ArrayList<String> alternatives;
    int size = wordTrigrams.size();

    for(int index=0; index < size; index++) {
      Trigram trigram = wordTrigrams.get(index);
      String triStr = trigram.getTri();
      if(!correctTrigrams.contains(triStr)){ // find correct trigrams for incorrectly spelled trigrams
        alternatives = findAlternatives(correctTrigrams, triStr);
        if(alternatives.isEmpty()) return suggestions;

        if(trigram.getSugg().isEmpty()) trigram.setSugg(alternatives);
      } else if(index < size-1){ // set suggestions to equal all probable next trigrams for correctly spelled trigrams
        alternatives = trigramPairs.get(triStr);
        if(alternatives != null) {
          Trigram nextTrigram = wordTrigrams.get(index+1);
          if(correctTrigrams.contains(nextTrigram.getTri())) nextTrigram.setSugg(alternatives);
        } else {
          alternatives = findAlternatives(correctTrigrams, triStr);
          if(alternatives.isEmpty()) return suggestions;

          trigram.setSugg(alternatives);
        }
      }

      if(trigram.getSugg().isEmpty()){
        ArrayList<String> array = new ArrayList<>();
        array.add(triStr);
        trigram.setSugg(array);
      }
    }

    suggestions = createSuggestions(wordTrigrams);

    return suggestions;

  }

  private HashSet<String> createSuggestions(ArrayList<Trigram> trigrams) {
    ArrayList<String> array = new ArrayList<>();
    ArrayList<String> combinedSugg = new ArrayList<>();
    HashSet<String> suggestions = new HashSet<>();
    ArrayList<String> suggestions2;

    for (int index = 0; index < trigrams.size(); index++) {
      if (index == 0) {
        combinedSugg = trigrams.get(index).getSugg();
        continue;
      } else {
        suggestions2 = trigrams.get(index).getSugg();
      }

      for(String str : combinedSugg) {
        if(suggestions2.size() < 10) {
          for(String sugg : suggestions2){
            String combinedStr = combine(str, sugg);
            if(!combinedStr.isEmpty()) array.add(combinedStr);
          }
        } else {
          int size = suggestions2.size();
          int start = BS.findStart(suggestions2, str, 0, size-1);

          if(start == -1){
            if(wordlist.contains(str) && str.length() > 3) suggestions.add(str);
            continue;
          }

          int end = BS.findEnd(suggestions2, str, start, size-1);

          for (int i = start; i <= end; i++){ //O(logn)
            String combinedStr = combine(str, suggestions2.get(i));
            if(!combinedStr.isEmpty()) array.add(combinedStr);
          }
        }
      }

      if(!array.isEmpty()){
        combinedSugg = array;
        array = new ArrayList<>();
      }

      for(String word : combinedSugg) {
        if(wordlist.contains(word)) suggestions.add(word);
      }
    }

    return suggestions;
  }
}