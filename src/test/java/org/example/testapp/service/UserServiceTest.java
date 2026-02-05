package org.example.testapp.service;

import org.example.testapp.dao.impl.UserDAO;
import org.example.testapp.model.AccessLvl;
import org.example.testapp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;
    //Тест валидации не правильного пароля
    @Test
    void saveUser_invalidLogin() {
        User user = new User();
        user.setLogin("ab");
        user.setPassword("password123");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.saveUser(user));
        assertEquals("Логин должен быть 3-20 символов и только латиница/цифры", ex.getMessage());
        verifyNoInteractions(userDAO);
    }
    //
    @Test
    void saveUser_shortPassword() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword("short");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.saveUser(user));
        assertEquals("Пароль должен быть минимум 8 символов", ex.getMessage());
        verifyNoInteractions(userDAO);
    }

    @Test
    void saveNewUser() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword("Password1");
        user.setAccess_lvl(AccessLvl.GUEST);
        user.setId(0);

        when(userDAO.existsByLogin("validLogin")).thenReturn(false);

        userService.saveUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).create(captor.capture());
        User saved = captor.getValue();

        assertNotNull(saved.getDate_creation());
        assertNotNull(saved.getDate_modification());
        assertNotEquals("Password1", saved.getPassword());
        assertTrue(BCrypt.checkpw("Password1", saved.getPassword()));
    }

    @Test
    void saveUser_newUser_existingLogin() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword("Password1");
        user.setAccess_lvl(AccessLvl.ADMIN);
        user.setId(0);

        when(userDAO.existsByLogin("validLogin")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.saveUser(user));
        assertEquals("Такой логин уже занят!", ex.getMessage());
        verify(userDAO, never()).create(user);
    }

    @Test
    void saveUser_existingUser() {
        User user = new User();
        user.setLogin("validLogin");
        user.setPassword("Password1");
        user.setAccess_lvl(AccessLvl.GUEST);
        user.setId(5);
        user.setDate_creation(Date.valueOf("2024-01-01"));

        userService.saveUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDAO).update(captor.capture());
        User saved = captor.getValue();

        assertNotNull(saved.getDate_modification());
        assertEquals(Date.valueOf("2024-01-01"), saved.getDate_creation());
        verify(userDAO, never()).existsByLogin("validLogin");
    }

    @Test
    void getAllUsers() {
        when(userDAO.getAll()).thenReturn(List.of());

        List<User> result = userService.getAllUsers();

        assertEquals(0, result.size());
        verify(userDAO).getAll();
    }

    @Test
    void deleteUser() {
        User user = new User();

        userService.deleteUser(user);

        verify(userDAO).delete(user);
    }
}
