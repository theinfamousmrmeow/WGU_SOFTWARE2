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
import java.time.Instant;

public class controllerCalendar extends controller {

    @FXML
    private RadioButton rbWeekly;
    @FXML
    private RadioButton rbMonthly;
    @FXML
    static RadioButton rbAll;
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

        tcCustomerCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        tcCustomerCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        //tcCustomerCustomerPhone.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tcCustomerAddressId.setCellValueFactory(new PropertyValueFactory<>("addressId"));

            populateTable();
            populateCustomerTable();
            System.out.println("PLKJSDF");
    }

    public void deselectRadioButton(){
//        rbAll.setSelected(false);
        rbMonthly.setSelected(false);
        rbWeekly.setSelected(false);
    }

    public void populateTable(){
        //get all entries from appointment table, create object for each.
        tvCalendar.getItems().clear();
        try {
            Connection conn = connectionManager.openConnection();
            ResultSet rs = connectionManager.sendStatement( "SELECT * FROM appointment");
            while (rs.next()) {
                tvCalendar.getItems().add(
                        new Appointment(
                                rs.getInt("appointmentId"),
                                rs.getInt("customerId"),
                                rs.getInt("userId"),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("location"),
                                rs.getString("contact"),
                                rs.getString("type"),
                                rs.getString("url"),
                                rs.getTimestamp("start"),
                                rs.getTimestamp("end"),
                                rs.getTimestamp("createDate"),
                                rs.getString("createdBy"),
                                rs.getTimestamp("lastUpdate"),
                                rs.getString("lastUpdateBy")
                        )
                );
            }
            rs.close();
            connectionManager.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
