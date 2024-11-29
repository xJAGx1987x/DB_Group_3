package com.falconcars.www.falconcars;

public class DBUser {
    private int personID ;
    private boolean isManager ;

    DBUser(){
        this.personID = 0 ;
        this.isManager = false ;
    }

    DBUser(int personID, boolean isManager){
        this.personID = personID ;
        this.isManager = isManager ;
    }

    public int getPersonID() {
        return this.personID ;
    }

    public boolean getIsManager() {
        return this.isManager ;
    }

    public void setPersonID(int personID){
        this.personID = personID ;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager ;
    }

}
