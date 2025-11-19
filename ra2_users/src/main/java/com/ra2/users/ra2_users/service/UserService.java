package com.ra2.users.ra2_users.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    public int saving(Users user){
        int usuario = userRepository.save(user);
        return usuario;
    }

    public List<Users> getingAllUsers() {
        List<Users> usuarios = userRepository.findAll();
        return usuarios;
    }

    public List<Users> getingUsersById(Long userId){
        List<Users> usuario = userRepository.findUserById(userId);
        return usuario;
    }

    public int updating(Long userId, Users modificacio){
        int updated = userRepository.updateUser(userId, modificacio);
        return updated;
    }

    public int updatingName(Long userId, String nom){
        int updated = userRepository.updateUserName(userId, nom);
        return updated;
    }

    public int deletingUser(Long userId){
        int usuario = userRepository.deleteUser(userId);
        return usuario;
    }

    /*
    public int uploadFiles(MultipartFile file, Path fileDir) throws Exception{
        if(!Files.exists(fileDir)){
            Files.createDirectories(fileDir);
        }

        String originalFile = file.getOriginalFilename();
        if(originalFile == null){
            return 0;
        }

        return 1;
    }
    */

    public ResponseEntity<String> uploadingImage(Long id, MultipartFile imageFile) throws Exception{
        List<Users> users = userRepository.findUserById(id);
        if(users == null || users.isEmpty()){
            return null;
        }
        Users user = users.get(0);

        Path imagesDir = Paths.get("private/image");
        if(!Files.exists(imagesDir)){
            Files.createDirectories(imagesDir);
        }

        String originalFile = imageFile.getOriginalFilename();
        if(originalFile == null){
            return null;
        }

        String newFile = "user_" + id + originalFile.substring(originalFile.lastIndexOf("."));
        Path imagePath = imagesDir.resolve(newFile);      

        Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

        String pathFinal = "/images/" + newFile;

        int numReg = userRepository.updateUserImagePath(id, pathFinal);
        if (numReg == 0){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error. No s'ha pujat la imatge");
        }
        user.setImage_path(pathFinal);
        return ResponseEntity.status(HttpStatus.OK).body("Acceptat. S'ha pujat la imatge correctament");
    }

    public ResponseEntity<String> UploadingCsvUsers(MultipartFile csvFile){
        int totalAdded = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))){
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {

                if(linea.trim().isEmpty()){
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error. El csv està buit");
                }

                if(primeraLinea){
                    primeraLinea = false;
                    if(linea.toLowerCase().contains("nom") && linea.toLowerCase().contains("email")){
                        continue;
                    }
                }

                String [] camps = linea.split(",");

                if (camps.length < 4){
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error. Al csv li falta algun camp en alguna linea. S'han afegit " + totalAdded +" usuaris");
                }

                Users user = new Users();

                user.setNom(camps[0].trim());
                user.setDescripcion(camps[1].trim());
                user.setEmail(camps[2].trim());
                user.setContrasenya(camps[3].trim());
                
                int inserted = userRepository.save(user);

                if (inserted > 0){
                    totalAdded ++;
                }
            }

        } catch (Exception e) {
            System.err.println("Error en la linea " + totalAdded);
        }
        
        return ResponseEntity.status(HttpStatus.OK).body("Acceptat. S'han afegit " + totalAdded + " usuaris");
    }

    public int uploadingJson(MultipartFile file) {
        int nombre_users = 0;
        try {
            JsonNode arrel = mapper.readTree(file.getInputStream());

            JsonNode data = arrel.path("data");
            String control = data.path("controll").asText();
            if (!control.equals("OK")){
                return 0;
            }
            int count = data.path("count").asInt();

            JsonNode users = data.path("users");
            for (JsonNode user: users){
                Users usuario = new Users();

                String name = user.path("name").asText();
                String description = user.path("description").asText();
                String email = user.path("email").asText();
                String password = user.path("password").asText();

                usuario.setNom(name);
                usuario.setDescripcion(description);
                usuario.setEmail(email);
                usuario.setContrasenya(password);

                userRepository.save(usuario);
                nombre_users ++;
            }
            
            if (nombre_users != count){
                return 0;
            }
            
            return 1;
        } catch (IOException e) {
            System.err.println("El Json té algun error");
        }
    }
}
