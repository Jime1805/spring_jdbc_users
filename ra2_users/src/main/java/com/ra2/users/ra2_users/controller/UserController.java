package com.ra2.users.ra2_users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.service.UserService;

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
    UserService userService;

    @PostMapping("/users") // localhost:8082/api/users {Estructura JSON}
    public ResponseEntity<String> postUser(@RequestBody Users user) {
        int usuario = userService.saving(user);
        if(usuario == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Usuari no creat");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Usuari creat amb èxit: " + user.getNom());
    }

    @PostMapping("/users/{id}/imageFile") // localhost:8082/api/users/{id del user}/image -> form-data (Key: imageFile, Type: file, Value: la imatge (.jpg o .png)) 
    public ResponseEntity<String> postUsersImage(@PathVariable Long id, @RequestParam MultipartFile imageFile) throws Exception {
        Users user = userService.uploadingImage(id, imageFile);

        if(user == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al trobar l'usuari.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Imatge pujada amb èxit.");
    }

    @PostMapping("/users/upload-csv")
    public ResponseEntity<String> postUserCsv(@RequestParam MultipartFile csvFile) {
        int totalAdded = userService.UploadingCsvUsers(csvFile);
        if (totalAdded == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al llegir el CSV.");
        }
        return ResponseEntity.status(HttpStatus.OK).body("CSV pujat amb èxit.");
    }

    @GetMapping("/users") //localhost:8082/api/users
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> usuarios = userService.getingAllUsers();
        if (usuarios == null || usuarios.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    
    @GetMapping("/users/{userId}") // localhost:8082/api/users/1
    public ResponseEntity<Users> getUserById(@PathVariable Long userId) {
        List<Users> usuario = userService.getingUsersById(userId);
        if (usuario == null || usuario.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } 
        return ResponseEntity.status(HttpStatus.OK).body(usuario.get(0));
    }

    @PutMapping("/users/{userId}") // localhost:8082/api/users/1 {Estructura JSOn}
    public ResponseEntity<String> postUser(@PathVariable Long userId, @RequestBody Users modificacio) {
        int updated = userService.updating(userId, modificacio);

        if(updated == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No s'ha trobat cap usuari amb id: " + userId);
        }
        return ResponseEntity.status(HttpStatus.OK).body("Usuari amb id: " + userId + " actualitzat correctament.");
    }

    @PatchMapping("/users/{userId}/nom") // localhost:8082/api/users/1/nom?nom=nouNom
    public ResponseEntity<Users> updateUserName(@PathVariable Long userId, @RequestParam String nom){

        int updated = userService.updatingName(userId, nom);

        if(updated == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        Users updatUsers = userService.getingUsersById(userId).get(0);
        return ResponseEntity.status(HttpStatus.OK).body(updatUsers);
    }

    @DeleteMapping("/users/{userId}") // localhost:8082/users/1
    public ResponseEntity<String> deleteUsers(@PathVariable Long userId){
        int usuario = userService.deletingUser(userId);

        if (usuario == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario amb id " + userId + " no trobat.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Usuari amb id " + userId + " eliminat correctament.");
    }
    
}
