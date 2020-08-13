package com.spellchecker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
 * @author Nthabi Mashiane
 * @author fridamjaria
 *
 */

@RestController
public class SpellcheckerController {

    @Value("${TARGET:World}")
    String target;

    @GetMapping("/")
    String success() {
      return "Hello " + target + "!";
    }

    @CrossOrigin
    @PostMapping("/spellcheck")
    ResponseEntity<HashMap<String, List<String>>> spellcheck(@RequestBody String data) {
        SpellcheckerFunctions functions = new SpellcheckerFunctions("isizulu");
        JSONObject requestBody = new JSONObject(data);
        String[] words;
        words = requestBody.getString("userText").split("\\s+");
        HashMap<String, List<String>> suggestedCorrections = functions.check(words);
        ResponseEntity<HashMap<String, List<String>>> response =
        new ResponseEntity<HashMap<String, List<String>>>(suggestedCorrections, HttpStatus.OK);
        return response;
    }
}
