package com.spellchecker.api.com.spellchecker.database;

import com.google.firebase.internal.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
public class DBController {

    @Autowired
    DBService service;

    @GetMapping("/view-corpus")
    public ArrayList<String> viewCorpus(){
        ArrayList<String> corpus = new ArrayList<String>();
        return corpus;
    }

    @PostMapping("/add-word")
    public void addNewWord(String word) throws  InterruptedException, ExecutionException {
        service.saveNewWord(word);
    }

}
