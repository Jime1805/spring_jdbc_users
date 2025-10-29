package com.ra2.users.ra2_users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/api/users")
    public ResponseEntity<String> postUser(@RequestBody Users user) {
        userRepository.save(user);
        return ResponseEntity.ok("Usuari creat amb èxit: " + user.getNom());
    }

    @GetMapping("/api/users")
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
    
    
}
