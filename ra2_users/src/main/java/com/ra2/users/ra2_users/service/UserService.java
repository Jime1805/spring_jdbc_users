package com.ra2.users.ra2_users.service;

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

    public List<Users> getAllUsers() {
        List<Users> usuarios = userRepository.findAll();
        return usuarios;
    }

    public List<Users> uploadImage(Long id, MultipartFile imageFile){
        List<Users> user = userRepository.findUserById(id);
        return user;
    }
}
