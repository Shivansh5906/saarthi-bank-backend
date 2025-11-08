package com.saarthi.dto;

public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String accountNumber;
    private double balance;

    public UserResponse(Long id, String name, String email, String accountNumber, double balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Getters (no setters required unless needed)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
}
