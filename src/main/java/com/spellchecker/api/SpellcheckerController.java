package com.spellchecker.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpellcheckerController {



    @GetMapping("/{userWord}")
    public boolean spellcheck(@PathVariable("userWord") String userWord){
        SpellcheckerFunctions functions = new SpellcheckerFunctions();
        return functions.errorDetection(userWord);
    }

    @PostMapping("/save-words")
    public boolean saveWords(){
        return true;
    }

}
