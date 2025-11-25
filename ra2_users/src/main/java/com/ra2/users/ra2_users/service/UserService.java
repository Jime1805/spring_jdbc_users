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

    public String uploadingImage(Long id, MultipartFile imageFile) throws Exception{
        List<Users> users = userRepository.findUserById(id);
        int numReg = 0;
        if(users == null || users.isEmpty()){
            return null;
        }
        Users user = users.get(0);

        String pathFinal = savingFiles(imageFile, id);

        if (pathFinal != null){
            numReg = userRepository.updateUserImagePath(id, pathFinal);
        }
        
        if (numReg == 0) {
            return null;
        }
        user.setImage_path(pathFinal);
        return pathFinal;
    }

    public int UploadingCsvUsers(MultipartFile csvFile){
        int totalAdded = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))){
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {

                if(linea.trim().isEmpty()){
                    return 0;
                }

                if(primeraLinea){
                    primeraLinea = false;
                    if(linea.toLowerCase().contains("nom") && linea.toLowerCase().contains("email")){
                        continue;
                    }
                }

                String [] camps = linea.split(",");

                if (camps.length < 4){
                    return totalAdded;
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
        
        return totalAdded;
    }

    public int uploadingJson(MultipartFile file) {
        int nombre_users = 0;
        try {
            JsonNode arrel = mapper.readTree(file.getInputStream());

            JsonNode data = arrel.path("data");
            String control = data.path("control").asText();
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

            return nombre_users;

        } catch (IOException e) {
            System.err.println("El Json té algun error");
            return 0;
        }
    }

    public String savingFiles(MultipartFile file, Long id){
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }
            
            String originalFile = file.getOriginalFilename();
            
            if (originalFile == null) {
                return null;
            }
            String path = "private/altres"; 
            Path fileDir = Paths.get(path);
            String newFile = "";

            if (id == null && originalFile.toLowerCase().endsWith(".json")){
                path = "private/json_processed";
                fileDir = Paths.get(path);
                String extension = originalFile.substring(originalFile.lastIndexOf("."));
                String baseName = originalFile.substring(0, originalFile.lastIndexOf("."));
                newFile = "user_" + baseName + "_" + System.currentTimeMillis() + extension;
            }
            else if (id == null && originalFile.toLowerCase().endsWith(".csv")){
                path = "private/csv_processed";
                fileDir = Paths.get(path);
                String extension = originalFile.substring(originalFile.lastIndexOf("."));
                String baseName = originalFile.substring(0, originalFile.lastIndexOf("."));
                newFile = "user_" + baseName + "_" + System.currentTimeMillis() + extension;
            }
            else if (id != null && (originalFile.toLowerCase().endsWith(".jpg") || originalFile.toLowerCase().endsWith(".png"))){
                path = "private/image";
                fileDir = Paths.get(path);
                newFile = "user_" + id + originalFile.substring(originalFile.lastIndexOf("."));
            }
            
            if(!Files.exists(fileDir)){
                Files.createDirectories(fileDir);
            }

            Path filePath = fileDir.resolve(newFile);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return path;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
