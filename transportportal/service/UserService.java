package com.transportportal.service;

import com.transportportal.dao.UserDAO;
import com.transportportal.model.User;

import java.sql.SQLException;
import java.util.List;


public class UserService {

    private final UserDAO dao = new UserDAO();

    public int createUser(User u) throws SQLException {
        // Optionally validate unique username here
        User existing = dao.findByUsername(u.getUsername());
        if (existing != null) {
            return -1; // indicates username already exists
        }
        return dao.create(u);
    }

    public User getUserById(int id) throws SQLException {
        return dao.findById(id);
    }

    public User getUserByUsername(String username) throws SQLException {
        return dao.findByUsername(username);
    }

    public List<User> listUsers() throws SQLException {
        return dao.findAll();
    }

    public boolean updateUser(User u) throws SQLException {
        return dao.update(u);
    }

    public boolean deleteUser(int id) throws SQLException {
        return dao.delete(id);
    }
}
