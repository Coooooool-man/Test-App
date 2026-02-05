package org.example.testapp.dao.impl;

import com.mysql.cj.xdevapi.SqlResult;
import org.example.testapp.model.AccessLvl;
import org.example.testapp.model.User;
import org.example.testapp.util.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    //SQL запросы для CRUD операция, если поменять СУБД ппредется переписывать весь SQL
    //ORM в этом плане проще при смене СУБД
    private static final String CREATE_SQL = "INSERT INTO users(login, access_lvl, password, date_creation, date_modification) values(?,?,?,?,?)";
    private static final String DELETE_SQL = "DELETE FROM users WHERE id=?";
    private static final String UPDATE_SQL = "UPDATE users SET login=?, access_lvl=?,password=?, date_creation=?, date_modification=? where id=?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM users";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE id=?";
    private static final String EXISTS_BY_LOGIN_SQL = "SELECT COUNT(*) FROM users WHERE login=?";
    private static final String RESET_AUTO_INC_SQL = "ALTER TABLE users AUTO_INCREMENT = 1";

    private static final String GET_NEXT_ID_SQL = "SELECT COALESCE(MAX(id), 0) + 1 FROM users";

    // Реализуйте метод (не забудьте добавить его в интерфейс UserDAO)
    @Override
    public long getNextId() {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_NEXT_ID_SQL);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении следующего ID", e);
        }
        return 1; // Если таблица пуста
    }


    //Создание пользователя

    @Override
    public void create(User user) {
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getLogin());
            stmt.setLong(2, user.getAccess_lvl().getLevel());
            stmt.setString(3, user.getPassword());
            stmt.setDate(4, user.getDate_creation());
            stmt.setDate(5, user.getDate_modification());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    user.setId(id);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании пользователя", e);
        }
    }
//Найти пользователя по ID Этот метод может пригодиться в будущем
    @Override
    public User findById(long id) {
        User user = new User();

        try(Connection conn = DataBaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)){

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                user.setId(rs.getLong("id"));
                long level = rs.getLong("access_lvl");
                user.setAccess_lvl(AccessLvl.fromInt(level));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setDate_creation(rs.getDate("date_creation"));
                user.setDate_modification(rs.getDate("date_modification"));
            }

        }catch(SQLException e){
            throw new RuntimeException("Ошибка при поиске пользователя по ID", e);
        }

        return user;

    }
    //Получить всех пользователй
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));// метод парсит текущую строку результат
                                                    // и отдает объект пользователь

            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка пользователей", e);
        }
        return users;
    }
    //Обноваить пользователя
    @Override
    public void update(User user) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, user.getLogin());
            stmt.setLong(2, user.getAccess_lvl().getLevel());
            stmt.setString(3, user.getPassword());
            stmt.setDate(4, user.getDate_creation());
            stmt.setDate(5, user.getDate_modification());
            stmt.setLong(6,user.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя");
        }

    }
    //Удаление пользователя
    @Override
    public void delete(User user) {

        try (Connection conn = DataBaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(DELETE_SQL);
                 PreparedStatement resetStmt = conn.prepareStatement(RESET_AUTO_INC_SQL)) {


                deleteStmt.setLong(1, user.getId());
                deleteStmt.executeUpdate();

                // Сбрасывает счетчик AUTO_INCREMENT
                // В MySQL это установит следующий ID как MAX(id) + 1
                resetStmt.executeUpdate();


                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя и обновлении счетчика ID", e);
        }
    }
    //Проверка существующего логина
    @Override
    public boolean existsByLogin(String login) {
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_LOGIN_SQL)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();


            if(rs.next()){
                return rs.getInt(1) > 0;
            }

        }catch (SQLException e){
            throw new RuntimeException("Ошибка при поиске пользователя по логину");
        }

        return false;
    }
    // Результат
    private User mapResultSetToUser(ResultSet rs) {
        User user = new User();

        try {
            user.setId(rs.getLong("id"));
            long level = rs.getLong("access_lvl");
            user.setAccess_lvl(AccessLvl.fromInt(level));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            user.setDate_creation(rs.getDate("date_creation"));
            user.setDate_modification(rs.getDate("date_modification"));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при маппинге ResultSet в объект User", e);
        }

        return user;
    }

}
