package com.example.sustainabox;

public class User {

    private String firstName, lastName, email, userType;
    private int credits;
    private int containerID, numberContainers;

    public User(String firstName, String lastName, String email, String userType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.credits = 5;
        this.containerID = 0;
        this.numberContainers = 0;
    }

    public int getCredits() {
        return credits;
    }
    public int getContainerID() {
        return containerID;
    }
    public int getContainerCount() {
        return numberContainers;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() { return userType;}
    public void setCredits(int credits) {
        this.credits = credits;
    }
    public void setContainerID(int containerID) {
        this.containerID = containerID;
    }
    public void setContainerCount(int containerCount) {
        this.numberContainers = numberContainers;
    }
}
