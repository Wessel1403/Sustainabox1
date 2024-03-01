package com.example.sustainabox;

public class User {

    private String firstName, lastName, email, phone, userType;
    private int credits;

    public User(String firstName, String lastName, String email, String userType, String phone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
