package com.ra2.users.ra2_users.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ra2.users.ra2_users.model.Users;
import com.ra2.users.ra2_users.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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
    
    public Users uploadingImage(Long id, MultipartFile imageFile) throws Exception{
        List<Users> users = userRepository.findUserById(id);
        if(users == null || users.isEmpty()){
            return null;
        }
        Users user = users.get(0);

        Path imagesDir = Paths.get("src/main/resources/public/images");
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
            return null;
        }
        user.setImage_path(pathFinal);
        return user;
    }

    public int UploadingCsvUsers(MultipartFile csvFile){
        int totalAdded = 0;

        if(csvFile == null || csvFile.isEmpty()){
            return totalAdded;
        }
        
        return totalAdded;
    }
}
