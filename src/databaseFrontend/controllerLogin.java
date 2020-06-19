package databaseFrontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.mysql.cj.jdbc.Driver;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.*;


import java.sql.DriverManager;
import java.time.Instant;

public class controllerLogin {

    @FXML
    private Label lblUsername;
    @FXML
    private Label lblPassword;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfPassword;
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnExit;

    private static String language="en";

    @FXML
    public void initialize() {
        language = System.getProperty("user.language");
        setLabels(language);
        //currentLogin.setUserId(1);
        //currentLogin.setUserName("Test");
        //connectionManager.addAppointment(1,"Title","Desc","A location","contact info","type","url",Timestamp.from(Instant.now()),Timestamp.from(Instant.now()));
        //connectionManager.updateAppointment(1,1,"UPDATE","Desc","A location","contact info","type","url",Timestamp.from(Instant.now()),Timestamp.from(Instant.now()));
    }


    public boolean checkCredentials(String givenUsername, String givenPassword){
        try {
            Connection conn = connectionManager.openConnection();
            ResultSet rs = connectionManager.sendStatement(conn,"SELECT userId, userName, active, password FROM user");
            //Check the stack until we find one
            while(rs.next()){
                String currentUsername = rs.getString("userName");
                int currentUserId = rs.getInt("userId");
                Boolean isActive = rs.getBoolean("active");
                String currentPassword = rs.getString("password");

                if (givenUsername.equals(currentUsername) && givenPassword.equals(currentPassword)){
                    currentLogin.setUserName(currentUsername);
                    currentLogin.setUserId(currentUserId);
                    return true;
                }
                else {
                    //Keep looking, do nothing
                }
            }
            rs.close();
            connectionManager.clear();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void setLabels(String language){
        switch (language){
            case "de":
                lblUsername.setText("DAS USER");
                lblPassword.setText("DAS PASSVERD");
                btnSubmit.setText("SSUBMITTEN");
                break;
            default:
                lblUsername.setText("Username");
                lblPassword.setText("Password");
                btnSubmit.setText("Submit");
                break;
        }
    }

    @FXML
    public void login(){
        String user=tfUsername.getText();
        String password=tfPassword.getText();

        //Send them over;
        if (checkCredentials(user,password)){
            logger.appendToLog(user+" logged in successfully.");

            //Launch the next screen.
            //TODO:  Record successful login with timestamp in a rolling log file.
            //TODO:  Check to see if there's an appointment for this user in the next 15 minutes.

            showSubSceneCommon("formAppointment.fxml",controller.ScreenStates.add);

        }
        else {
            badUsernamePassword();
        }
    }


    @FXML
    public void badUsernamePassword(){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        switch (language) {
            case "de":
                errorAlert.setHeaderText("DAS FAILURE");
                errorAlert.setContentText("DAS PASSWERD IST SCHEISSE");
                break;
            default:
            errorAlert.setHeaderText("Failed to Log In");
            errorAlert.setContentText("The Username and Password combination did not match any records in the database.");
            break;
        }
        errorAlert.showAndWait();
    }
    public void showSubSceneCommon(String newScreenFXML, controller.ScreenStates state){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newScreenFXML));
        try {
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            //stage.setTitle("ABC");
            stage.setScene(new Scene(root1));
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        btnSubmit.getScene().getWindow().hide();
    }

    public void exitProgram(){
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

}

