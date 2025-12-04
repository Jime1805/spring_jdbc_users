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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ra2.users.ra2_users.logs.UserLogs;
import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserLogs userLogs;
    
    public int saving(Users user){
        int usuario = userRepository.save(user);
        if (usuario == 0) {
            userLogs.error("No s'ha pogut crear l'usuari", "UserService", "saving");
        }
        else{
            userLogs.info("Creant l'usuari " + user.getNom(), "UserService", "saving");
        }
        return usuario;
    }

    public List<Users> getingAllUsers() {
        List<Users> usuarios = userRepository.findAll();
        if (usuarios == null || usuarios.isEmpty()) {
            userLogs.error("No hi ha usuaris enregistrats", "UserService", "gettingAllUsers");
        }
        else {
            userLogs.info("Cercant tots els usuaris...", "UserService", "gettingAllUsers");
        }
        return usuarios;
    }

    public List<Users> getingUsersById(Long userId){
        List<Users> usuario = userRepository.findUserById(userId);
        if (usuario == null || usuario.isEmpty()) {
            userLogs.error("No s'ha trobat cap usuari amb id: " + userId, "UserSerice", "gettingAllUsers");
        } else {
            userLogs.info("Cercant l'usuari amb id "+ userId, "UserSerice", "gettingAllUsers");
        }
        return usuario;
    }

    public int updating(Long userId, Users modificacio){
        List<Users> usuario = userRepository.findUserById(userId);
        if (usuario == null || usuario.isEmpty()) {
            userLogs.error("No s'ha trobat cap usuari amb id: " + userId, "UserSerice", "updating");
            return -1;
        }
        int updated = userRepository.updateUser(userId, modificacio);
        if(updated == 0){
            userLogs.error("No s'ha pogut modificar l'usuari amb id " + userId, "UserService", "updating");
        }
        else{
            userLogs.info("Actualitzant l'usuari amb id " + userId, "UserService", "updating");
        }
        return updated;
    }

    public int updatingName(Long userId, String nom){
        List<Users> usuario = userRepository.findUserById(userId);
        if (usuario == null || usuario.isEmpty()) {
            userLogs.error("No s'ha trobat cap usuari amb id: " + userId, "UserSerice", "updatingName");
            return -1;
        }
        int updated = userRepository.updateUserName(userId, nom);
        if (updated == 0){
            userLogs.error("No s'ha pogut modificar l'usuari amb id: " + userId, "UserSerice", "updatingName");
        }
        else{
            userLogs.info("Actualitzant l'usuari amb id: " + userId, "UserSerice", "updatingName");
        }
        return updated;
    }

    public int deletingUser(Long userId){
        List<Users> usuario = userRepository.findUserById(userId);
        if (usuario == null || usuario.isEmpty()) {
            userLogs.error("No s'ha trobat cap usuari amb id: " + userId, "UserSerice", "deletingUser");
            return -1;
        }
        int user = userRepository.deleteUser(userId);
        if(user == 0){
            userLogs.error("No s'ha pogut esborrar l'usuari amb id: " + userId, "UserSerice", "deletingUser");
        }
        else{
            userLogs.info("Esborrant l'usuari amb id: " + userId, "UserSerice", "deletingUser");
        }
        return user;
    }

    public String uploadingImage(Long id, MultipartFile imageFile) throws Exception{
        List<Users> users = userRepository.findUserById(id);
        int numReg = 0;
        if(users == null || users.isEmpty()){
            userLogs.error("No s'ha trobat l'usuari amb id " + id, "UserService", "uploadingImage");
            return null;
        }
        Users user = users.get(0);

        String pathFinal = savingFiles(imageFile, id);

        if (pathFinal != null){
            numReg = userRepository.updateUserImagePath(id, pathFinal);
        }
        
        if (numReg == 0) {
            userLogs.error("No s'ha afegit la imatge amb url " + pathFinal, "UserService", "uploadingImage");
            return null;
        }
        else{
            userLogs.info("Afegint la imatge a la url " + pathFinal, "UserService", "uploadingImage");
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
                    userLogs.error("Error al carregar el fitxer csv, la linea " + (totalAdded + 1) + " és buida", "UserService", "UploadingCsvUsers");
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
                    userLogs.error("Error al carregar el fitxer csv, la linea " + (totalAdded + 1), "UserService", "UploadingCsvUsers");
                    return totalAdded;
                }

                Users user = new Users();
                try {
                    user.setNom(camps[0].trim());
                    user.setDescripcion(camps[1].trim());
                    user.setEmail(camps[2].trim());
                    user.setContrasenya(camps[3].trim());
                
                    int inserted = userRepository.save(user);
                    if (inserted > 0){
                        totalAdded ++;
                    }
                } catch (Exception e) {
                    System.err.println("A la linea " + totalAdded + 1 + " no s'ha pogut afegir perqué les dades d'aquest usuari són incoherents");
                    userLogs.error("Error al carregar el fitxer csv, la linea " + (totalAdded + 1),"UserService", "UploadingCsvUsers");
                    return totalAdded;
                }
            }

        } catch (Exception e) {
            System.err.println("Error en la linea " + totalAdded);
            userLogs.error("Error al carregar el fitxer csv, la linea " + (totalAdded + 1) + ". " + e,"UserService", "UploadingCsvUsers");
            return totalAdded;
        }
        userLogs.info("Afegint " + totalAdded + " usuaris", "UserService", "UploadingCsvUsers");
        return totalAdded;
    }

    public int uploadingJson(MultipartFile file) {
        int nombre_users = 0;
        try {
            JsonNode arrel = mapper.readTree(file.getInputStream());

            JsonNode data = arrel.path("data");
            String control = data.path("control").asText();
            if (!control.equals("OK")){
                userLogs.error("Error al carregar json, el control no és OK", "UserService", "uploadingJson");
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
                userLogs.error("Error al carregar json, s'han registrat " + nombre_users + " quan s'havien de registrar " + count, "UserService", "uploadingJson");
                return 0;
            }

            userLogs.info("Afegint " + nombre_users + "usuaris", "UserService", "uploadingJson");

            return nombre_users;

        } catch (IOException e) {
            System.err.println("El Json té algun error");
            userLogs.error("Error al carregar el json, s'han enregistrat " + nombre_users + ". " + e, "UserService", "uploadingJson");
            return 0;
        }
    }

    public String savingFiles(MultipartFile file, Long id){
        try {
            if (file == null || file.isEmpty()) {
                userLogs.error("Error al carregar el fitxer, no s'ha afegit el fitxer", "UserService", "savingFiles");
                return null;
            }
            
            String originalFile = file.getOriginalFilename();
            
            if (originalFile == null) {
                userLogs.error("Error al carregar el fitxer, no s'ha afegit el fitxer", "UserService", "savingFiles");
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
            
            if (path.equals("private/altres")) {
                userLogs.error("Error al guardar el fitxer. Aquest fitxer s'ha guardat en la ruta " + path, "UserService", "savingFiles");
            }

            if(!Files.exists(fileDir)){
                Files.createDirectories(fileDir);
            }

            Path filePath = fileDir.resolve(newFile);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userLogs.info("Guardant fitxers, el fitxer s'ha emmagatzemat en " + path, "UserService", "savingFiles");
            return path;

        } catch (IOException e) {
            System.err.println("Error al guardar el fitxer: " + e);
            userLogs.error("Error al guardar el fitxer " + e, "UserService", "savingFiles");
            return null;
        }
    }
}
