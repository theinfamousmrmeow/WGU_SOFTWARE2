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
    //@FXML
    //DatePicker dpEnd;
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
        //populateTimes(cbTimeEnd);
        populateCustomers(cbCustomerID);

        if (screenState==screenStates.add){
            btnSave.setText("Add");
            lblHeading.setText("Add Appointment");
            dpStart.setValue(LocalDate.now());

        }
        else {
            lblHeading.setText("Update Appointment");
            btnSave.setText("Update");

            tfType.setText(currentAppointment.getType());
            int customerId = currentAppointment.getCustomerId();
            //A lambda to do a foreach
            cbCustomerID.getItems().forEach(i ->{
                String index = i.split("-")[0].trim();
                if (customerId==Integer.parseInt(index)){
                    cbCustomerID.getSelectionModel().select(i);
                }
            });

            //Update date picker
            //Timestamp start = currentAppointment.getStart();
            LocalDateTime startDateTime = connectionManager.UTCTimestampToLocalDateTime(currentAppointment.getStart());
            LocalDate startDate = startDateTime.toLocalDate();

            dpStart.setValue(startDate);
            Timestamp end = currentAppointment.getEnd();
            LocalDate endDate = end.toLocalDateTime().toLocalDate();
            //dpEnd.setValue(endDate);
            //Update start time
            LocalTime startTime = startDateTime.toLocalTime();
            System.err.println(startDateTime);
            System.err.println("Local start time:"+startTime);

            cbTimeStart.getItems().forEach(t ->
            {
                if (getTimeOfString(t).equals(startTime)){
                    cbTimeStart.getSelectionModel().select(t);
                }
            });
            //Update end time

            populateEndTimes();

            LocalTime endTime = end.toLocalDateTime().toLocalTime();
            cbTimeEnd.getItems().forEach(t ->
            {
                if (getTimeOfString(t).equals(endTime)){
                    cbTimeEnd.getSelectionModel().select(t);
                }
            });

        }
    }

    public static void populateTimes(ComboBox<String> cb){
        for (int i=0;i<24;i++) {
            LocalTime lt = LocalTime.of(i,0);
            if ((lt.equals(businessHoursStart) || lt.isAfter(businessHoursStart))
            && (lt.isBefore(businessHoursEnd))){
                cb.getItems().add(lt.format(timeFormatter));
            }
        }
    }

    public void populateEndTimes(){
        LocalTime timeStart = getTimeOfComboBox(cbTimeStart);
        cbTimeEnd.getItems().clear();
        cbTimeStart.getItems().forEach(t -> {
            LocalTime lt = getTimeOfString(t);
            if (lt.isAfter(timeStart)) {
                cbTimeEnd.getItems().add(t);
            }
        });
        cbTimeEnd.getItems().add(businessHoursEnd.format(timeFormatter));
        cbTimeEnd.getSelectionModel().select(0);

    }

    public static LocalTime getTimeOfComboBox(ComboBox<String> cb){
        return LocalTime.parse(cb.getValue(),timeFormatter);
    }

    public static LocalTime getTimeOfString(String cb){
        return LocalTime.parse(cb,timeFormatter);
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
            Timestamp startTimeDate = Timestamp.valueOf(LocalDateTime.of(dpStart.getValue(), startTime).atZone(ZoneId.of("UTC")).toLocalDateTime());
            startTimeDate = connectionManager.localDateTimeToUTCTimestamp(LocalDateTime.of(dpStart.getValue(),startTime));
            System.out.println("START TIME(Should be eastern)"+startTime);//Is in Eastern yes, now how the fuck to convert
            //startTimeDate = Timestamp.from((LocalDateTime.of(dpStart.getValue(),startTime)).toInstant(ZoneOffset.UTC));
            //System.err.println("startTimeDate(Should be UTC):"+startTime);


            ZonedDateTime startTimeZDT = LocalDateTime.of(dpStart.getValue(),startTime).atZone(ZoneId.systemDefault());
            //System.err.println("startTimeDate (LOCAL?) = "+startTimeDate);//local time.
            System.err.println("startTimeZDT (SHOULD BE UTC) = "+startTimeZDT);//Offset wrong fucking way
            System.err.println("Time in UTC land "+LocalDateTime.now(ZoneOffset.UTC));//+4
            System.err.println("Appointment in UTC land "+startTimeZDT.toInstant().atZone(ZoneId.of("UTC")));//WORKS
            System.err.println("Appointment in UTC land but now localDate object "+Timestamp.valueOf(startTimeZDT.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime()));//Still in local time.
            System.err.println("Appointment in UTC land with function: "+connectionManager.localDateTimeToUTCTimestamp(LocalDateTime.of(dpStart.getValue(),startTime)));
            System.err.println("Timestamp should reflect local time: "+connectionManager.UTCTimestampToLocalDateTime(startTimeDate));//should say 0900(GMT), says 1300(UTC).

            //Timestamp startTimeDate = Timestamp.valueOf(startTimeZDT.toLocalDateTime());
            Timestamp endTimeDate = Timestamp.valueOf(LocalDateTime.of(dpStart.getValue(), getTimeOfComboBox(cbTimeEnd)));
            endTimeDate = connectionManager.localDateTimeToUTCTimestamp(LocalDateTime.of(dpStart.getValue(),endTime));
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
