package com.ks.efir.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.efir.auth.GoogleAuthUtil;

@RestController
@RequestMapping(path = "/api/gdoc")
public class GoogleDocController {

    private final GoogleAuthUtil googleAuthUtil;

    @Autowired
    public GoogleDocController(GoogleAuthUtil googleAuthUtil) {
        this.googleAuthUtil = googleAuthUtil;
    }

    @GetMapping
    public List<String> getText() throws IOException, GeneralSecurityException {
     return  googleAuthUtil.getText();
    }
}
