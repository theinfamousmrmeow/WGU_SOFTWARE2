package databaseFrontend;

import java.sql.Timestamp;

public class customer {

    private int customerId;
    private String customerName;
    private int addressId;
    private boolean active;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;

    public customer(int customerId, String customerName, int addressId, boolean active, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        this.active = active;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public void delete(){
        //connectionManager.deleteAppointment(appointmentId);
        connectionManager.deleteFromWhere("customer","customerId="+customerId);
    }
    public void create(){
        connectionManager.addCustomer(customerId,customerName,addressId,active,createDate,createdBy,lastUpdate,lastUpdateBy);
    }

    public void update(){
        connectionManager.updateCustomer(customerId,customerName,addressId,active,createDate,createdBy,lastUpdate,lastUpdateBy);
    }

}
