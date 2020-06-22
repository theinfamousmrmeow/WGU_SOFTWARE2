package databaseFrontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class controllerAppointment extends controller {

    public static List<DayOfWeek> nonWorkDays = new ArrayList<DayOfWeek>();


    public static LocalTime businessHoursStart = LocalTime.of(9,0);
    public static LocalTime businessHoursEnd = LocalTime.of(17,0);

    public static screenStates screenState=screenStates.add;
    public static int appointmentId;
    public static Appointment currentAppointment;

    @FXML
    Button btnTitle;
    @FXML
    Label lblHeading;
    @FXML
    TextField tfTitle;
    @FXML
    TextField tfDescription;
    @FXML
    TextField tfLocation;
    @FXML
    TextField tfContact;
    @FXML
    TextField tfType;
    @FXML
    TextField tfUrl;
    @FXML
    DatePicker dpStart;
    @FXML
    DatePicker dpEnd;
    @FXML
    ComboBox<String> cbCustomerID;
    @FXML
    Button btnSave;
    @FXML
    Button btnCancel;
    @FXML
    ComboBox<String> cbTimeStart;
    @FXML
    ComboBox<String> cbTimeEnd;

    static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public void initialize() {
        nonWorkDays.add(DayOfWeek.SATURDAY);
        nonWorkDays.add(DayOfWeek.SUNDAY);
        populateTimes(cbTimeStart);
        populateTimes(cbTimeEnd);
        populateCustomers(cbCustomerID);

        if (screenState==screenStates.add){
            btnSave.setText("Add");
            lblHeading.setText("Add Appointment");
        }
        else {
            lblHeading.setText("Update Appointment");
            btnSave.setText("Update");
        }
    }

    public static void populateTimes(ComboBox<String> cb){
        for (int i=0;i<24;i++) {
            LocalTime lt = LocalTime.of(i,0);
            cb.getItems().add(lt.format(timeFormatter));
        }
    }

    public static LocalTime getTimeOfComboBox(ComboBox<String> cb){
        return LocalTime.parse(cb.getValue(),timeFormatter);
    }

    public static void converToDate(String str){
        LocalTime lt = LocalTime.parse(str);
    }

    public static void populateCustomers(ComboBox<String> cbCustomerID){
        //TODO:  Get all customers
        Connection conn = connectionManager.openConnection();
        ResultSet rs = connectionManager.sendStatement("SELECT * FROM customer");
        try {
            while (rs.next()) {
                String currentCustomerName = rs.getString("customerName");
                int currentId = rs.getInt("customerId");

                cbCustomerID.getItems().add(""+ currentId + "-" + currentCustomerName);

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addOrUpdateAppointment() {

        int customerId = Integer.parseInt(cbCustomerID.getValue().split("-")[0]);

        String description = "";
        String type = tfType.getText();
        String title = "Meeting.";
        LocalTime startTime = getTimeOfComboBox(cbTimeStart);
        LocalTime endTime = getTimeOfComboBox(cbTimeEnd);
        DayOfWeek day = dpStart.getValue().getDayOfWeek();

        //Make sure we are in business hours
        if (nonWorkDays.contains(day) ||
                startTime.isBefore(businessHoursStart) || startTime.isAfter(businessHoursEnd) ||
                endTime.isBefore(businessHoursStart) || endTime.isAfter(businessHoursEnd)) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Appointment Outside Business Hours");
            errorAlert.setContentText("Business hours are from "+businessHoursStart.toString() +" to " +businessHoursEnd+", Monday - Friday");
            errorAlert.showAndWait();
        } else {
            //Passed input validation.
            Timestamp startTimeDate = Timestamp.valueOf(LocalDateTime.of(dpStart.getValue(), startTime));
            Timestamp endTimeDate = Timestamp.valueOf(LocalDateTime.of(dpEnd.getValue(), getTimeOfComboBox(cbTimeEnd)));
            if (screenState == screenStates.add) {
                //Add new one
                connectionManager.addAppointment(customerId, title, description, "", "", type, "", startTimeDate, endTimeDate);
            } else {
                //Modify currently selected one.
                connectionManager.updateAppointment(appointmentId, customerId, title, description, "", "", type, "", startTimeDate, endTimeDate);
            }
            //Back out
            backToMain();
        }
    }

    public void backToMain(){
        showSubSceneCommon("formCalendar.fxml", screenStates.add,btnCancel);
    }

}
