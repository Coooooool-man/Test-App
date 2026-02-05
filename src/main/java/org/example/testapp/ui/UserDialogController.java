package org.example.testapp.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.testapp.model.AccessLvl;
import org.example.testapp.model.User;
import org.example.testapp.service.UserService;

public class UserDialogController {

    @FXML private TextField idField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<AccessLvl> accessLvlCombo;
    @FXML private TextField modCol;
    @FXML private Button btnSave;

    private Stage dialogStage;
    private User user;
    private UserService userService;
    private boolean isEdit;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        accessLvlCombo.getItems().setAll(AccessLvl.values());
        // Запрещаем редактировать ID вручную, так как он берется из БД
        idField.setEditable(false);
        idField.setFocusTraversable(false); // Чтобы фокус не падал на него первым
    }

    public void setData(User user, UserService userService) {
        this.userService = userService;

        if (user != null && user.getId() != 0) {
            // При редактировании
            this.user = user;
            this.isEdit = true;
            dialogStage.setTitle("Редактирование пользователя");
            btnSave.setText("Изменить");
            fillFields();
        } else {
            // При создании
            this.user = new User();
            this.isEdit = false;
            dialogStage.setTitle("Создание пользователя");
            btnSave.setText("Создать");

            // Получаем следующий ID из базы данных через сервис
            try {
                long nextId = userService.getNextAvailableId();
                idField.setText(String.valueOf(nextId));
            } catch (Exception e) {
                idField.setText("?"); // В случае ошибки
            }

            loginField.setText("");
            passwordField.setText("");
            accessLvlCombo.setValue(null);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

//    public void setData(User user, UserService userService) {
//        this.user = user;
//        this.userService = userService;
//        this.isEdit = (user != null);
//        user.getId();
//
//        if (isEdit) {
//            dialogStage.setTitle("Редактирование пользователя");
//            btnSave.setText("Изменить");
//            fillFields();
//        } else {
//        dialogStage.setTitle("Создание пользователя");
//        btnSave.setText("Создать");
//
//        this.user = new User();
//        // idField.setText(""); // Оставляем пустым или пишем "Авто"
//        }
//    }

    private void fillFields() {
        idField.setText(String.valueOf(user.getId()));
        loginField.setText(user.getLogin());


        passwordField.setText("");
        accessLvlCombo.setValue(user.getAccess_lvl());
    }

    @FXML
    private void handleSave() {
        //Перенес валидацию в UserService

        //  Валидация полей (проверка формата данных)
//        String errorMessage = validateInput();
//
//        if (errorMessage != null) {
//            showErrorAlert(errorMessage);
//            return; // Прерываем сохранение, если данные неверны
//        }

        //  Сохранение через сервис
        try {
            user.setLogin(loginField.getText());
            user.setAccess_lvl(accessLvlCombo.getValue());

            // Пароль меняется, если поле не пустое или это создание нового пользователя
            String passInput = passwordField.getText();
            if (passInput != null && !passInput.isEmpty()) {
                user.setPassword(passInput);
            }

            userService.saveUser(user);

            saveClicked = true;
            dialogStage.close();

        } catch (Exception e) {
            // Ошибки от сервиса (например, "Пользователь уже существует")
            //Вывод в виде окна
            showErrorAlert(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    //Метод валидации, но больше не используется в контроллера, валидация проходит в UserService
    private String validateInput() {
        String login = loginField.getText();
        String pass = passwordField.getText();


        if (login == null || !login.matches("^[a-zA-Z0-9]{3,20}$")) {
            return "Логин должен быть от 3 до 20 символов и содержать только латиницу и цифры.";
        }


        if (!isEdit && (pass == null || pass.isEmpty())) {
            return "Пароль не может быть пустым.";
        }

        // Если пароль введен (при создании или редактировании), он должен быть >= 8 символов.
        if (pass != null && !pass.isEmpty() && pass.length() < 8) {
            return "Пароль должен быть не менее 8 символов.";
        }


        if (accessLvlCombo.getValue() == null) {
            return "Выберите уровень доступа.";
        }

        return null; // Ошибок нет
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle("Ошибка валидации");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }
}