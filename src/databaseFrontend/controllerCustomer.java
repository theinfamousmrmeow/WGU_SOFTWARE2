package databaseFrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class controllerCustomer extends controller {
    public static screenStates screenState=screenStates.add;
    public static int customerId;
    public static int addressId;
    public static customer currentCustomer;

    @FXML
    Label lblTitle;
    @FXML
    Button btnSave;
    @FXML
    Button btnExit;
    @FXML
    TextField tfName;
    @FXML
    TextField tfPhone;
    @FXML
    TextField City;


    @FXML
    public void initialize() {
        if (screenState==screenStates.modify){
            addressId=currentCustomer.getAddressId();
            customerId=currentCustomer.getAddressId();
            tfName.setText(currentCustomer.getCustomerName());
        }
        //Create new, it should auto-increment if these are not supplied.
        else {
            addressId=1;
            customerId=10;
        }
    }

    public void addCustomer(){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        try{
            String name = tfName.getText();
            customer c = new customer(customerId,name,addressId,true,now,currentLogin.getUserName(),now,currentLogin.getUserName());
            c.create();
            backToMain();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void backToMain(){
        showSubSceneCommon("formCalendar.fxml", screenStates.add,btnExit);
    }


}
