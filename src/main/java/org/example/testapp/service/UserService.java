package org.example.testapp.service;

import org.example.testapp.dao.impl.UserDAO;
import org.example.testapp.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Date;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void saveUser(User user) {
        //  Валидация логина
        if (!user.getLogin().matches("^[a-zA-Z0-9]{3,20}$")) {
            throw new RuntimeException("Логин должен быть 3-20 символов и только латиница/цифры");
        }

        // Валидация пароля
        if (user.getPassword() == null || !user.getPassword().matches("^[a-zA-Z0-9]{8,}$")) {
            throw new RuntimeException("Пароль должен быть минимум 8 символов и только латиница/цифры");
        }
        //Выбран ли уровень доступа
        if(user.getAccess_lvl() == null) {
                throw new RuntimeException("Выберите уровень доступа");
        }

        // Хеширование пароля (чтобы в БД не было открытого текста)
        String salt = BCrypt.gensalt();
        String hashed = BCrypt.hashpw(user.getPassword(), salt);
        user.setPassword(hashed);

        // Установка дат
        Date now = new Date(System.currentTimeMillis());
        if (user.getId() == 0) { // Если новый
            if (userDAO.existsByLogin(user.getLogin())) {
                throw new RuntimeException("Такой логин уже занят!");
            }
            user.setDate_creation(now);
            user.setDate_modification(now);
            userDAO.create(user);
        } else { // Если редактирование
            user.setDate_modification(now);
            userDAO.update(user);
        }
    }

    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    public void deleteUser(User user) {
        userDAO.delete(user);
    }

    public long getNextAvailableId() {
        return userDAO.getNextId();
    }

}
