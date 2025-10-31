package com.ra2.users.ra2_users.repository;

import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        int numReg = jdbcTemplate.update(sql, users.getNom(), users.getDescripcion(), users.getEmail(), users.getContrasenya(), null, now, now);
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

    public int updateUser(Long id, Users user){
        String sql = "UPDATE users SET nom = ?, descripcion = ?, email = ?, contrasenya = ?, dataUpdated = ? WHERE id = ?";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        return jdbcTemplate.update(sql, user.getNom(), user.getDescripcion(), user.getEmail(), user.getContrasenya(), now, id );
    }

    public int updateUserName(Long id, String nom){
        String sql = "UPDATE users SET nom = ?, dataUpdated = ? WHERE id = ?";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        return jdbcTemplate.update(sql, nom, now, id);
    }

    public int deleteUser(Long id){
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
