package com.example.social_media.controller;


import com.example.social_media.entity.AuthRequest;
import com.example.social_media.entity.UserInfo;
import com.example.social_media.service.JwtService;
import com.example.social_media.service.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService service;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    // Removed the role checks here as they are already managed in SecurityConfig

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        System.out.println(authRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        System.out.println("JEL NE UDJES OVDE");
        if (authentication.isAuthenticated()) {
            // Dobavi User iz baze po username/email
            System.out.println("NASAO JE");
            UserInfo user = service.findByEmail(authRequest.getUsername());
            System.out.println("******************" + user);
            return jwtService.generateToken(user.getEmail());
        } else {
            System.out.println("NIJE NASAO");
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
}
