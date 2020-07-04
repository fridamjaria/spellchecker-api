package com.spellchecker.api;

import java.util.HashMap;
import java.util.HashSet;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public HashMap<String, HashSet<String>> spellcheck(@RequestBody String userText) {
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
