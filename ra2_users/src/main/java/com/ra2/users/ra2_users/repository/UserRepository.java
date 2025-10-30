package com.ra2.users.ra2_users.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ra2.users.ra2_users.model.Users;

@Repository
public class UserRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public static final class UsersRowMapper implements RowMapper<Users>{
        @Override
        public Users mapRow(ResultSet rs, int rowNum) throws SQLException{
            Users user = new Users();
            user.setId(rs.getLong("id"));
            user.setNom(rs.getString("nom"));
            user.setDescripcion(rs.getString("descripcion"));
            user.setEmail(rs.getString("email"));
            user.setContrasenya(rs.getString("contrasenya"));
            user.setUltimAcces(rs.getTimestamp("ultimAcces"));
            user.setDataCreated(rs.getTimestamp("dataCreated"));
            user.setDataUpdated(rs.getTimestamp("dataUpdated"));
            return user;
        }    
    }
    
    public int save(Users users){
        String sql = "insert into users (nom, descripcion, email, contrasenya, ultimAcces, dataCreated, dataUpdated) values (?, ?, ?, ?, ?, ?, ?)";
        int numReg = jdbcTemplate.update(sql, users.getNom(), users.getDescripcion(), users.getEmail(), users.getContrasenya(), null, LocalDate.now(), LocalDate.now());
        return numReg;
    }

    public List<Users> findAll(){
        String sql = "Select * from users";
        return jdbcTemplate.query(sql, new UsersRowMapper());
    }

    public List<Users> findUserById(Long id){
        String sql = "Select * from users where id = ?";
        return jdbcTemplate.query(sql, new UsersRowMapper(), id);
    }

    public boolean updateUser(){
        String sql = "";
    }
}
