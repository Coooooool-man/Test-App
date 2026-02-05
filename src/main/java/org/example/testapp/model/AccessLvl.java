package org.example.testapp.model;
// Вместо join используется класс enum для задания
// Считывается id из таблицы Access_lvl и подбирается такое
// числовое значение в перечислении
public enum AccessLvl {
    GUEST(0, "Гость"),
    USER(1, "Пользователь"),
    DEVELOPER(2, "Разработчик"),
    ADMIN(3, "Администратор");

    private final long level;
    private final String name;

    AccessLvl(int level, String name) {
        this.level = level;
        this.name = name;
    }

    // Получить числовое значение доступа для текущего объекта User
    public long getLevel() {
        return level;
    }
    //Получить название доступа
    public String getName() {
        return name;
    }
    // Подобрать объет уровня доступа для конкретного User по
    // ID из таблицы Access_lvl
    public static AccessLvl fromInt(long level){
        for (AccessLvl al: AccessLvl.values()){
            if(level == al.getLevel()){
                return al;
            }
        }

        return GUEST;
    }

    @Override
    public String toString() {
        return name;
    }
}
