package databaseFrontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;

public class controllerCalendar extends controller {

    @FXML
    private RadioButton rbWeekly;
    @FXML
    private RadioButton rbMonthly;
    @FXML
    private  RadioButton rbAll;
    @FXML
    private  Button btnExit;
    @FXML
    private Button btnCustomers;
    @FXML
    private Button btnApptNew;
    @FXML
    private Button btnApptUpdate;
    @FXML
    private Button btnApptDelete;
    @FXML
    private Button btnCustomerNew;
    @FXML
    private Button btnCustomerUpdate;
    @FXML
    private Button btnCustomerDelete;
    @FXML
    private TableColumn tcAppointmentId;
    @FXML
    private TableColumn tcStartDate;
    @FXML
    private  TableColumn tcEndDate;
    @FXML
    private TableColumn tcStartTime;
    @FXML
    private TableColumn tcEndTime;
    @FXML
    private TableColumn tcType;
    @FXML
    private TableView tvCalendar;
    @FXML
    private TableView tvCustomer;
    @FXML
    private TableColumn tcCalendarCustomerId;

    @FXML
    private TableView tvUsers;
    @FXML
    private TableColumn tcUserId;
    @FXML
    private TableColumn tcUserName;


    @FXML
    private TableColumn tcCustomerCustomerId;
    @FXML
    private TableColumn tcCustomerCustomerName;
    @FXML
    private TableColumn tcCustomerPhone;

    @FXML
    private TableColumn tcCustomerAddressId;

    @FXML
    public void initialize() {
        deselectRadioButton();
//        rbAll.setSelected(true);
/*        final ObservableList<Appointment> data = FXCollections.observableArrayList(
                new Appointment(1,1,1,"Title","desc","loc","con","type","url", Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), "creator", Timestamp.from(Instant.now()),"someguy")
        );*/
        tcAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        tcType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tcStartDate.setCellValueFactory(new PropertyValueFactory<>("start"));
        tcEndDate.setCellValueFactory(new PropertyValueFactory<>("end"));
        tcCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));


        tcCustomerCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tcCustomerCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        //tcCustomerCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        //tcCustomerAddressId.setCellValueFactory(new PropertyValueFactory<>("addressId"));
        tcCustomerAddressId.setCellValueFactory(new PropertyValueFactory<>("textAddress"));

        tcUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        tcUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));

            populateTable();
            populateCustomerTable();
            populateUserTable();
            System.out.println("PLKJSDF");
    }

    public void deselectRadioButton(){
        rbAll.setSelected(false);
        rbMonthly.setSelected(false);
        rbWeekly.setSelected(false);

        LocalDateTime now = LocalDateTime.now();
        System.out.println("LOCAL: "+now);
        System.out.println("GMT?: "+LocalDateTime.now(ZoneOffset.UTC));

    }

    public void filterWeekly(){
        deselectRadioButton();
        rbWeekly.setSelected(true);
        Timestamp now = Timestamp.from(Instant.now());
        //Instant.now().atZone(ZoneId.of("GMT"));
        Timestamp nextWeek = Timestamp.valueOf(LocalDateTime.now().plusWeeks(1));

        System.out.println(now);
        System.out.println(nextWeek);

        //now.toInstant().atZone(ZoneId.of("GMT")).toLocalDate();

        //Lambdas for days
        //Lambda to just iterate through a collection and add to calendar
        tvCalendar.getItems().clear();
        getApointmentsInRange(now,nextWeek).forEach(a -> tvCalendar.getItems().add(a));
    }

    public void filterMonthly(){
        deselectRadioButton();
        rbMonthly.setSelected(true);
        Timestamp now = Timestamp.from(Instant.now());
        //Instant.now().atZone(ZoneId.of("GMT"));
        Timestamp nextWeek = Timestamp.valueOf(LocalDateTime.now().plusMonths(1));

        System.out.println(now);
        System.out.println(nextWeek);

        //now.toInstant().atZone(ZoneId.of("GMT")).toLocalDate();

        //Lambda to just iterate through a collection and add to calendar
        tvCalendar.getItems().clear();
        getApointmentsInRange(now,nextWeek).forEach(a -> tvCalendar.getItems().add(a));
    }

    public void filterAll(){
        deselectRadioButton();
        rbAll.setSelected(true);
        populateTable();
    }

    /**
     * Remember to use the GMT time for start and end
     * @param start
     * @param end
     * @return
     */
   public static ObservableList<Appointment> getApointmentsInRange(Timestamp start, Timestamp end){
        ObservableList<Appointment> appointments = connectionManager.getallAppointments();
        //Even more lambdas to filter through collections.
        return (appointments.filtered(p->(p.getStart().after(start) || p.getStart().equals(start)) && p.getStart().before(end)));
       //return (appointments.filtered(p->p.getStart().after(start) ));
    }

    public void populateTable(){
        //get all entries from appointment table, create object for each.
        if (tvCalendar.getItems()!=null){
            tvCalendar.getItems().clear();
        }
        tvCalendar.setItems(connectionManager.getallAppointments());

    }

    public void populateCustomerTable(){
        //get all entries from appointment table, create object for each.
            tvCustomer.getItems().clear();
        try {
            ResultSet rs = connectionManager.sendStatement( "SELECT * FROM customer");
            while (rs.next()) {
                tvCustomer.getItems().add(
                        new customer(
                                rs.getInt("customerId"),
                                rs.getString("customerName"),
                                rs.getInt("addressId"),
                                rs.getBoolean("active"),
                                rs.getTimestamp("createDate"),
                                rs.getString("createdBy"),
                                rs.getTimestamp("lastUpdate"),
                                rs.getString("lastUpdateBy")
                        )
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateUserTable(){
        //get all entries from appointment table, create object for each.
        tvUsers.getItems().clear();
        try {
            ResultSet rs = connectionManager.sendStatement( "SELECT * FROM user");
            while (rs.next()) {
                tvUsers.getItems().add(
                        new User(
                                rs.getInt("userId"),
                                rs.getString("userName"),
                                rs.getString("password"),
                                rs.getBoolean("active"),
                                rs.getTimestamp("createDate"),
                                rs.getString("createdBy"),
                                rs.getTimestamp("lastUpdate"),
                                rs.getString("lastUpdateBy")
                        )
                );
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAppointment(){
        Object appt =  tvCalendar.getSelectionModel().getSelectedItem();

        System.out.println("Deleting...");

        if (appt!=null){
            try{
                connectionManager.deleteAppointment(((Appointment)appt).getAppointmentId());
                populateTable();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void createNewUser(){
        controllerUser.screenState=screenStates.add;
        showSubSceneCommon("formUser.fxml", screenStates.add,btnApptNew);
    }
    public void modifyUser(){
        Object appt =  tvUsers.getSelectionModel().getSelectedItem();

        if (appt!=null) {

            controllerUser.screenState=screenStates.modify;
            controllerUser.currentUser= ((User)appt);
            showSubSceneCommon("formUser.fxml", screenStates.modify, btnApptNew);
        }
    }
    public void deleteUser(){
        Object appt =  tvUsers.getSelectionModel().getSelectedItem();

        System.out.println("Deleting...");

        if (appt!=null){
            try{
                connectionManager.deleteUser(((User)appt).getUserId());
                populateUserTable();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void logout(){
        showSubSceneCommon("formLogin.fxml", screenStates.add,btnApptNew);
    }

    public void createNewAppointment(){
        controllerAppointment.screenState=screenStates.add;
        showSubSceneCommon("formAppointment.fxml", screenStates.add,btnApptNew);
    }
    public void modifyAppointment(){
        Object appt =  tvCalendar.getSelectionModel().getSelectedItem();

        if (appt!=null) {
            controllerAppointment.appointmentId = ((Appointment)appt).getAppointmentId();
            controllerAppointment.screenState=screenStates.modify;
            controllerAppointment.currentAppointment= ((Appointment)appt);
            showSubSceneCommon("formAppointment.fxml", screenStates.modify, btnApptNew);
        }
    }

    public void createNewCustomer(){
        controllerCustomer.screenState=screenStates.add;
        showSubSceneCommon("formCustomer.fxml", screenStates.add,btnApptNew);
    }
    public void modifyCustomer(){
        Object appt =  tvCustomer.getSelectionModel().getSelectedItem();

        if (appt!=null) {
            controllerCustomer.currentCustomer= ((customer)appt);
            controllerCustomer.screenState = screenStates.modify;
            showSubSceneCommon("formCustomer.fxml", screenStates.modify, btnApptNew);
        }
    }
    public void deleteCustomer() {
        Object appt = tvCustomer.getSelectionModel().getSelectedItem();
        //Assertions.
        if (appt != null) {
            try {
                System.out.println("Deleting customer ");
                connectionManager.deleteCustomer(((customer) appt).getCustomerId());
                populateCustomerTable();
                populateTable();//Purged his appointments too, so refresh calendar.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
