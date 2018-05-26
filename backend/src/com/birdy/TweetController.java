package com.birdy;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TweetController {

    @RequestMapping("/")
    public String index() {
        return "Hello!";
    }
}
