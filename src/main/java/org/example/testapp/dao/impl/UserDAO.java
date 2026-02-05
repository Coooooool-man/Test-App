package org.example.testapp.dao.impl;

import org.example.testapp.model.User;

import java.util.List;

public interface UserDAO {

    public void create(User user);
    public User findById(long id);
    public List<User> getAll();
    public void update(User user);
    public void delete(User user);
    public boolean existsByLogin(String login);
    public long getNextId();
}
