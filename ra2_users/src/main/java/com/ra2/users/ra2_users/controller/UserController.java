package com.ra2.users.ra2_users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<String> postUser(@RequestBody Users user) {
        userRepository.save(user);
        return ResponseEntity.ok("Usuari creat amb èxit: " + user.getNom());
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> usuarios = userRepository.findAll();
        if (usuarios == null || usuarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<Users> findUser(@PathVariable Long userId) {
        List<Users> usuario = userRepository.findUserById(userId);
        if (usuario == null || usuario.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } 
        return ResponseEntity.status(HttpStatus.OK).body(usuario.get(0));
    }

    @PutMapping("/users/{userId}")
    public String putMethodName(@PathVariable Long userId, @RequestBody Users modificacio) {
        return "a";
    }
    
}
