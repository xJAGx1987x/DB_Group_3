package com.falconcars.www.falconcars;

public class DBUser {
    private int personID ;
    private String locationID;
    private boolean isManager ;

    DBUser(){
        this.personID = 0 ;
        this.locationID = "";
        this.isManager = false ;
    }

    DBUser(int personID, String locationID, boolean isManager){
        this.personID = personID ;
        this.locationID = locationID;
        this.isManager = isManager ;
    }

    public int getPersonID() {
        return this.personID ;
    }

    public boolean getIsManager() {
        return this.isManager ;
    }

    public String getLocation() {
        return this.locationID;
    }

    public void setPersonID(int personID){
        this.personID = personID ;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager ;
    }

    public void setLocation(String location) {
        this.locationID = location;
    }
}
