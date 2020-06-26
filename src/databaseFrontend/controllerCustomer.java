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
    TextField tfAddress;
    @FXML
    TextField tfAddress2;
    @FXML
    TextField tfCity;
    @FXML
    TextField tfCountry;
    @FXML
    TextField tfPostalCode;
    @FXML
    TextField City;


    @FXML
    public void initialize() {
        if (screenState==screenStates.modify){
            lblTitle.setText("Edit Customer");
            //btnSave.setText("Update");
            addressId=currentCustomer.getAddressId();
            customerId=currentCustomer.getCustomerId();
            tfName.setText(currentCustomer.getCustomerName());
            Address currentAddress = connectionManager.getAddress(addressId);
            if (currentAddress!=null){
                tfAddress.setText(currentAddress.getAddress());
                tfAddress2.setText(currentAddress.getAddress2());
                int cityId = currentAddress.getCityId();
                int countryId = connectionManager.getCountryId(cityId);
                tfCity.setText(connectionManager.getCity(cityId));
                tfCountry.setText(connectionManager.getCountry(countryId));
                tfPostalCode.setText(currentAddress.getPostalCode());
                tfPhone.setText(currentAddress.getPhone());
            }
        }
        //Create new, it should auto-increment if these are not supplied.
        else {
            lblTitle.setText("New Customer");
            addressId=-1;
            //customerId=10;
        }
    }

    public void addCustomer(){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        try{
            String name = tfName.getText();
            String address = tfAddress.getText();
            String address2 = tfAddress2.getText();

            String postalCode = tfPostalCode.getText();
            String phone = tfPhone.getText();
            String city = tfCity.getText();
            String country = tfCountry.getText();

            //Make a new country if necessary
            int countryId = connectionManager.getCountryId(country);
            if (countryId<0){
                connectionManager.addCountry(country);
                countryId=connectionManager.getCountryId(country);
            }
            //Make new city if necessary.
            int cityId = connectionManager.getCityId(city);
            if (cityId<0){
                connectionManager.addCity(city,countryId);
                cityId=connectionManager.getCityId(city);
            }

            Address a = new Address(-1,address,address2,cityId,postalCode,phone,now,currentLogin.getUserName(),now,currentLogin.getUserName());
            //connectionManager.addAddress(address,address2,cityId,postalCode,phone);
            //connectionManager.getAddressId(address,address2,cityId,postalCode,phone);
            a.create();
            addressId=a.getAddressId();
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
