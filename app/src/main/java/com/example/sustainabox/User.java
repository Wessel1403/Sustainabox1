package com.example.sustainabox;

public class User {

    private String firstName, lastName, email, userType;
    private int credits;

    public User(String firstName, String lastName, String email, String userType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.credits = 5;
    }

    public int getCredits() {
        return credits;
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
}
