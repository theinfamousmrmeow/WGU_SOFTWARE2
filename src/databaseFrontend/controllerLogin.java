package databaseFrontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;

public class controllerLogin extends controller{

    public static final boolean BYPASS_LOGIN = false;

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

        /**
         * WGU server kept going down on me, use this macro to bypass
         */
        if (BYPASS_LOGIN){
            currentLogin.setUserName("test");
            currentLogin.setUserId(1);
            return true;
        }
        else {
            try {

                ResultSet rs = connectionManager.sendStatement("SELECT userId, userName, active, password FROM user");
                //Check the stack until we find one
                while (rs.next()) {
                    String currentUsername = rs.getString("userName");
                    int currentUserId = rs.getInt("userId");
                    Boolean isActive = rs.getBoolean("active");
                    String currentPassword = rs.getString("password");

                    if (givenUsername.equals(currentUsername) && givenPassword.equals(currentPassword)) {
                        currentLogin.setUserName(currentUsername);
                        currentLogin.setUserId(currentUserId);

                        return true;
                    } else {
                        //Keep looking, do nothing
                    }
                }
                rs.close();
                connectionManager.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public Appointment checkForAlerts(int userId){
        ObservableList<Appointment> apptLsit= connectionManager.getAllAppointmentsForUser(userId);
        ObservableList<Appointment> upcomingApptList= FXCollections.observableArrayList();
        if (apptLsit.size()>0){
            apptLsit.forEach(app -> {
                //If an appointment starts within the next 15 minutes!
                if (LocalDateTime.now().plusMinutes(15).plusSeconds(1).isAfter(connectionManager.UTCTimestampToLocalDateTime(app.getStart()))){
                    upcomingApptList.add(app);
                }
            });
        }
        //Could be more than one, but the brief only says we have to alert about "a" appointment....
        if (upcomingApptList.size()>0){
            return upcomingApptList.get(0);
        }
        return null;
    }

    public void setLabels(String language){
        System.out.println(language);
        switch (language){
            case "de":
                lblUsername.setText("Nutzername");
                lblPassword.setText("Passwort");
                btnSubmit.setText("einreichen");
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


            //Check for alerts
            Appointment upcomingAppt = checkForAlerts(currentLogin.getUserId());
            if (upcomingAppt!=null){
                Alert errorAlert = new Alert(Alert.AlertType.INFORMATION);
                errorAlert.setHeaderText("Upcoming Appointment");
                errorAlert.setContentText("You have an appointment at "+upcomingAppt.getStart()+"!");
                errorAlert.showAndWait();
            }

            logger.appendToLog(user+" logged in successfully.");

            //Launch the next screen.
            //TODO:  Record successful login with timestamp in a rolling log file.
            //TODO:  Check to see if there's an appointment for this user in the next 15 minutes.

            showSubSceneCommon("formCalendar.fxml", screenStates.add,btnSubmit);

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
                errorAlert.setHeaderText("Einloggen fehlgeschlagen");
                errorAlert.setContentText("Kombination aus Benutzername und Passwort stimmte nicht mit Datensätzen in der Datenbank überein.");
                break;
            default:
            errorAlert.setHeaderText("Failed to Log In");
            errorAlert.setContentText("The Username and Password combination did not match any records in the database.");
            break;
        }
        errorAlert.showAndWait();
    }



    public void showSubSceneCommon(String newScreenFXML, screenStates state){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newScreenFXML));
        try {
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
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

