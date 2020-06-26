package databaseFrontend;

import java.sql.Timestamp;

public class City {

    int cityId;
    String city;
    int countryId;
    Timestamp createDate;
    String createdBy;
    Timestamp lastUpdate;
    String lasteUpdateBy;

    public City(int cityId, String city, int countryId, Timestamp createDate, String createdBy, Timestamp lastUpdate, String lasteUpdateBy) {
        this.cityId = cityId;
        this.city = city;
        this.countryId = countryId;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lasteUpdateBy = lasteUpdateBy;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
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

    public String getLasteUpdateBy() {
        return lasteUpdateBy;
    }

    public void setLasteUpdateBy(String lasteUpdateBy) {
        this.lasteUpdateBy = lasteUpdateBy;
    }
}
