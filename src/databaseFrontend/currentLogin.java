package databaseFrontend;

import java.time.ZoneId;
import java.util.TimeZone;

public class currentLogin {

    static TimeZone localTimeZone;
    static ZoneId localZoneId;

    static int userId;

    static String userName;

    //Accessors and Mutators

    public static void setUserName(String _userName) {
       userName = _userName;
    }

    public static String getUserName() {
        return userName;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int _userId) {
       userId = _userId;
    }
}
