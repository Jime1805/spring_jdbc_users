package com.ra2.users.ra2_users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<String> postUser(@RequestBody Users user) {
        int usuario = userRepository.save(user);
        if(usuario == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Usuari no creat");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Usuari creat amb èxit: " + user.getNom());
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> usuarios = userRepository.findAll();
        if (usuarios == null || usuarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<Users> findUser(@PathVariable Long userId) {
        List<Users> usuario = userRepository.findUserById(userId);
        if (usuario == null || usuario.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } 
        return ResponseEntity.status(HttpStatus.OK).body(usuario.get(0));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<String> postUser(@PathVariable Long userId, @RequestBody Users modificacio) {
        int updated = userRepository.updateUser(userId, modificacio);

        if(updated == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No s'ha trobat cap usuari amb id: " + userId);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Usuari amb id: " + userId + " actualitzat correctament.");
    }

    @PatchMapping("/users/{userId}/nom")
    public ResponseEntity<Users> updateUserName(@PathVariable Long userId, @RequestParam String nom){
        int updated = userRepository.updateUserName(userId, nom);

        if(updated == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        Users updatUsers = userRepository.findUserById(userId).get(0);
        return ResponseEntity.status(HttpStatus.OK).body(updatUsers);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUsers(@PathVariable Long userId){
        int usuario = userRepository.deleteUser(userId);

        if (usuario == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario amb id " + userId + " no trobat.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Usuari amb id " + userId + " creat correctament.");
    }
    
}
