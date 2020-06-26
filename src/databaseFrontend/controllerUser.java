package databaseFrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class controllerUser extends controller{

    @FXML
    Button buttonSave;
    @FXML
    Button buttonCancel;
    @FXML
    TextField tfUsername;
    @FXML
    TextField tfPassword;

    public static screenStates screenState= screenStates.add;

    public static User currentUser;


    @FXML
    public void initialize(){
        if (screenState == screenStates.add){
            buttonSave.setText("Create");
        }
        else {
            tfUsername.setText(currentUser.getUserName());
            tfPassword.setText(currentUser.getPassword());
            buttonSave.setText("Update");
        }
    }

    public void addUser(){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        try{
            String name = tfUsername.getText();
            String password = tfPassword.getText();
            User c = new User(-1,name,password,true,now,currentLogin.getUserName(),now,currentLogin.getUserName());
            c.create();
            backToMain();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void backToMain(){
        showSubSceneCommon("formCalendar.fxml", screenStates.add,buttonCancel);
    }

}
