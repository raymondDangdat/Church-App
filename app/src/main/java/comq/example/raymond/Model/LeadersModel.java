package comq.example.raymond.Model;

import java.io.Serializable;

public class LeadersModel implements Serializable {
    private String id;
    private String position;
    private String profilePix;
    private String name;
    private String status;
    private String serviceYear;
    private String phone;

    public LeadersModel() {
    }

    public LeadersModel(String position, String profilePix, String name, String status, String serviceYear, String phone) {
        this.position = position;
        this.profilePix = profilePix;
        this.name = name;
        this.status = status;
        this.serviceYear = serviceYear;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProfilePix() {
        return profilePix;
    }

    public void setProfilePix(String profilePix) {
        this.profilePix = profilePix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceYear() {
        return serviceYear;
    }

    public void setServiceYear(String serviceYear) {
        this.serviceYear = serviceYear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
