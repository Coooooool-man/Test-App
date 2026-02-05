package org.example.testapp.model;

import java.sql.Date;

//Главная модель всего приложения - класс Пользователь
public class User {
    private long id;
    private String login;
    private AccessLvl access_lvl;
    private String password;
    private Date date_creation;
    private Date date_modification;


    public User(long id, String login, AccessLvl access_lvl, String password,
                Date date_creation, Date date_modification) {
        this.id = id;
        this.login = login;
        this.access_lvl = access_lvl;
        this.password = password;
        this.date_creation = date_creation;
        this.date_modification = date_modification;
    }

    public User(){

    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AccessLvl getAccess_lvl() {
        return access_lvl;
    }

    public void setAccess_lvl(AccessLvl access_lvl) {

        this.access_lvl = access_lvl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate_creation() {return date_creation;}
    public void setDate_creation(Date date_creation) {
        this.date_creation = date_creation;
    }

    public Date getDate_modification() {
        return date_modification;
    }

    public void setDate_modification(Date date_modification) {
        this.date_modification = date_modification;
    }




}
