package org.example.testapp.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.testapp.dao.impl.UserDAOImpl;
import org.example.testapp.model.User;
import org.example.testapp.service.UserService;

import java.io.IOException;
import java.sql.Date; // ВАЖНО: используем java.sql.Date
import java.text.SimpleDateFormat;

public class MainController {

    @FXML private TableView<User> table;

    // Типы колонок изменены, чтобы соответствовать типам данных
    @FXML private TableColumn<User, Long> idCol;
    @FXML private TableColumn<User, String> loginCol;
    @FXML private TableColumn<User, String> passCol;
    @FXML private TableColumn<User, String> roleCol;

    // ВАЖНО: Здесь теперь java.sql.Date, а не String
    @FXML private TableColumn<User, Date> createCol;
    @FXML private TableColumn<User, Date> modCol;

    @FXML private Button btnDelete;
    @FXML private Button btnEdit;
    @FXML private Button btnCreate;

    private final UserService userService = new UserService(new UserDAOImpl());
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Простые поля
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        // Пароль
        passCol.setCellValueFactory(cellData -> new SimpleStringProperty("***********"));

        // Роль
        roleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAccess_lvl().getName()));



        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

        // Колонка создания
        createCol.setCellValueFactory(new PropertyValueFactory<>("date_creation"));
        createCol.setCellFactory(column -> new TableCell<User, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Преобразуем Date в String здесь
                    setText(formatter.format(item));
                }
            }
        });

        // Колонка редактирования
        modCol.setCellValueFactory(new PropertyValueFactory<>("date_modification"));
        modCol.setCellFactory(column -> new TableCell<User, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                } else {
                    // Получаем весь объект User для этой строки
                    User currentUser = getTableRow().getItem();

                    // Защита от NPE (иногда при перерисовке строка может быть еще не связана с данными)
                    if (currentUser == null) {
                        setText(null);
                        return;
                    }

                    // Получаем дату создания
                    Date createDate = currentUser.getDate_creation();

                    // Логика отображения:
                    if (item == null) {
                        // Если даты редактирования вообще нет -> тире
                        setText("-");
                    }
                    else if (createDate != null && item.toString().equals(createDate.toString())) {
                        // Если Дата Редактирования совпадает с Датой Создания -> тире
                        setText("-");
                    }
                    else {
                        // Иначе показываем отформатированную дату
                        setText(formatter.format(item));
                    }
                }
            }
        });

        // Listner выделения строки в таблице
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = (newSelection != null);
            btnDelete.setDisable(!isSelected);
            btnEdit.setDisable(!isSelected);
        });

        modCol.setMinWidth(180);


        refreshTable();// обновить таблицу
    }



    private void refreshTable() {
        userList.setAll(userService.getAllUsers());
        table.setItems(userList);
    }

    @FXML
    private void handleDelete() {
        User selectedUser = table.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Вы уверены?");
        alert.setContentText("Удалить пользователя: " + selectedUser.getLogin() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            userService.deleteUser(selectedUser);
            refreshTable();
        }
    }

    @FXML
    private void handleCreate() {
        showUserDialog(null);
    }

    @FXML
    private void handleEdit() {
        User selectedUser = table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            showUserDialog(selectedUser);
        }
    }

    private void showUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("user-dialog.fxml"));
            // Загружаем Pane, чтобы можно было применить стили, если нужно, или просто сцену
            Pane page = loader.load();
            Scene scene = new Scene(page);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);


            dialogStage.initOwner(table.getScene().getWindow());

            dialogStage.setScene(scene);
            //Загрузка контроллера
            UserDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(user, userService);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                refreshTable();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не удалось загрузить окно диалога");
            alert.showAndWait();
        }
    }
}