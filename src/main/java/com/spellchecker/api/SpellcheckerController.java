package com.spellchecker.api;

import java.util.HashMap;
import java.util.HashSet;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Nthabi Mashiane
 * @author fridamjaria
 *
 */

@RestController
public class SpellcheckerController {

    @PostMapping("/spellcheck")
    public HashMap<String, HashSet<String>> spellcheck(@PathVariable("userWord") String userText) {
        SpellcheckerFunctions functions = new SpellcheckerFunctions("isizulu");
        String[] words;
        words = userText.split(" ");
        return functions.check(words);
    }

    @PostMapping("/save-words")
    public boolean saveWords(){
        return true;
    }

}
