package databaseFrontend;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;

public class connectionManager {

    public static String USER = "U079QE";
    public static String PASS = "53688965778";
    public static String DB_URL = "jdbc:mysql://3.227.166.251:3306/U079QE";
    public static Statement lastStatement;
    public static Connection currentConnection=null;



    /**
     * Don't want to bother with managing connections so make this guy do it instead.
     * @return
     */
    public static Connection openConnection() {
        if (currentConnection !=null){
            try {
                if (currentConnection.isClosed()) {
                    currentConnection.close();
                    currentConnection = null;
                }
            }
            catch (Exception e){
                e.printStackTrace();
                currentConnection = null;
            }
        }
        if (currentConnection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Connecting to database...");
                currentConnection = DriverManager.getConnection(DB_URL, USER, PASS);
                return currentConnection;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        return currentConnection;
    }

    public static void closeConnection(){
        if (currentConnection!=null){
            try {
                currentConnection.close();
                currentConnection=null;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void populateTestData(){

        //INSERT INTO customer(customerId,customerName,addressId,active) VALUES (1,"Frank Frankson",1,true)
        //INSERT INTO country(countryId,country) VALUES (1,"US"),(2,"UK"),(3,"DE")
        //INSERT INTO city(city, countryId) VALUES ("Phoenix",1)
        //INSERT INTO address(address,address2,cityId,postalCode,phone) VALUES ("123 Apple Street","",1,78602,"123-456-7890")
        //INSERT INTO customer(
    }

    public static void addCustomer(int customerId,String name,int addressId, boolean active){
        Connection conn = openConnection();
        connectionManager.sendStatement("INSERT INTO customer(customerId,customerName,addressId,active) VALUES ("+customerId+","+name+","+addressId+","+active+")");
        connectionManager.clear();
    }

    public static void addCustomer(customer c){
        addCustomer(c.getCustomerId(),c.getCustomerName(),c.getAddressId(),c.isActive(),c.getCreateDate(),c.getCreatedBy(),c.getLastUpdate(),c.getLastUpdateBy());
    }

    public static void addCustomer(int customerId,String customerName, int addressId,boolean active, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy){
        String sep = "\",";
        int _active = active ? 1:0;
        connectionManager.sendUpdate("INSERT INTO customer(customerName,addressId,active,createDate,createdBy,lastUpdate,lastUpdateBy) " +
                "VALUES (\""+customerName+"\",\""+addressId+"\",\""+_active+"\",\""+createDate+"\",\""+ createdBy+"\",\"" +lastUpdate+"\",\""+lastUpdateBy+"\")");
        connectionManager.clear();
    }

    public static void updateCustomer(int customerId,String customerName, int addressId,boolean active, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy){
        int _active = active ? 1:0;
        connectionManager.sendUpdate("UPDATE customer SET customerName=" + customerName
                        +", addressId ="+addressId
                        +", active ="+_active
                        +", createDate ="+wrap(createDate)
                        +", createdBy ="+wrap(createdBy)
                        +", lastUpdate ="+wrap(lastUpdate)
                        +", lastUpdateBy ="+wrap(lastUpdateBy)
                        +" WHERE customerId ="+customerId);
    }

    public static void deleteCustomer(int customerId){
        connectionManager.sendUpdate("DELETE FROM appointment WHERE customerId="+customerId);
        connectionManager.sendUpdate("DELETE FROM customer WHERE customerId="+customerId);
    }

    public static void deleteFromWhere(String from, String where){
        connectionManager.sendUpdate("DELETE FROM "+from +" WHERE "+where);
    }

    public static void addAppointment(int customerId,String title, String description,String location, String contact, String type, String url, Timestamp start, Timestamp end){

        Timestamp now = Timestamp.from(Instant.now());
        Connection conn = openConnection();
        connectionManager.sendUpdate("INSERT INTO appointment(customerId,userId,title,description,location,contact,type,url,start,end,createDate,createdBy,lastUpdate,lastUpdateBy) " +
                "VALUES ("+customerId+",\""+currentLogin.getUserId()+"\",\""+title+"\",\""+description+"\",\""+location+"\",\""+ contact+"\",\"" +type+"\",\""+url+"\",\""+start+"\",\""+end+"\",\""+now+"\",\""+currentLogin.getUserName()+"\",\""+now+"\",\""+currentLogin.getUserName()+"\")");
        connectionManager.clear();
    }

    public static void deleteAppointment(int appointmentId){
        Connection conn = openConnection();
        connectionManager.sendUpdate("DELETE FROM appointment WHERE appointmentId="+appointmentId);
        connectionManager.clear();
    }

    public static void updateAppointment(int appointmentId, int customerId,String title, String description,String location, String contact, String type, String url, Timestamp start, Timestamp end){
        Connection conn = openConnection();
        connectionManager.sendUpdate("UPDATE appointment SET customerId=" + customerId
                +", title ="+wrap(title)
                +", description ="+wrap(description)
                +", location ="+wrap(location)
                +", contact ="+wrap(contact)
                +", type ="+wrap(type)
                +", url ="+wrap(url)
                +", start ="+wrap(start)
                +", end ="+wrap(end)
                +", lastUpdate="+wrap(Timestamp.valueOf(LocalDateTime.now()))
                +", lastUpdateBy="+wrap(currentLogin.getUserName())
                +" WHERE appointmentId ="+appointmentId);
        connectionManager.clear();
    }

    public static int getCountryId(String country){
        ResultSet rs = sendStatement("SELECT countryId FROM country WHERE country = "+country);
        try {
            if (rs.next()) {
                return rs.getInt("countryId");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static int getCityId(String city){
        ResultSet rs = sendStatement("SELECT cityId FROM city WHERE city = "+city);
        try {
            if (rs.next()) {
                return rs.getInt("cityId");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static void addAddress(String address, String address2, int cityId, String postalCode, String phone){

        Timestamp now = Timestamp.from(Instant.now());
        Connection conn = openConnection();
        connectionManager.sendUpdate("INSERT INTO address(address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                "VALUES ("+address+",\""
                +address2+"\",\""
                +cityId+"\",\""
                +postalCode+"\",\""
                +phone+"\",\""
                +now+"\",\""
                +currentLogin.getUserName()+"\",\""
                +now+"\",\""
                +currentLogin.getUserName()+"\")");
        connectionManager.clear();
    }

    public static ResultSet sendStatement(String sql){
        openConnection();
        System.out.println("Creating statement...");
        try {
            lastStatement = currentConnection.createStatement();
            ResultSet rs = lastStatement.executeQuery(sql);
            System.out.println(rs.toString());
            return rs;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static int sendUpdate(String sql){
        openConnection();
        System.out.println(sql);
        try {
            lastStatement = currentConnection.createStatement();
            int result = lastStatement.executeUpdate(sql);
            System.out.println(result);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static void clear(){
        if (lastStatement!=null){
            try {
                lastStatement.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            lastStatement=null;
        }
        closeConnection();
    }

    ///Convenience methods
    public static String wrap(String str){
        return "\""+str+"\"";
    }
    public static String wrap(Timestamp str){
        return "\""+str.toString()+"\"";
    }
}
