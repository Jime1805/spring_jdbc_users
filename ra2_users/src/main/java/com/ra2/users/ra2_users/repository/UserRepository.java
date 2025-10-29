package com.ra2.users.ra2_users.repository;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ra2.users.ra2_users.model.Users;

public class UserRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public int save(Users users){
        String sql = "insert into users (name, description, email, password, ultimAcces, dataCreated, dataUpdated) values (?, ?, ?, ?, ?, ?, ?)";
        int numReg = jdbcTemplate.update(sql, users.getNom(), users.getDescripcion(), users.getEmail(), users.getContrasenya(), users.getUltimAcces(), LocalDate.now(), LocalDate.now());
        return numReg;
    }
}
