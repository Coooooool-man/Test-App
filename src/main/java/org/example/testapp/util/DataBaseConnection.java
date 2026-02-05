package org.example.testapp.util;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
public class DataBaseConnection {
// Загрузка переменных среды  из файла .env для подключение к БД
    static public Connection getConnection() throws SQLException{
        Dotenv dotenv = Dotenv.load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
