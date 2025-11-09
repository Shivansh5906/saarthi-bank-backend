package com.saarthi.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // DEPOSIT, WITHDRAW, TRANSFER, RECEIVED
    private double amount;

    private String details; // optional description

    // ✅ NEW FIELDS
    private String fromAccount; 
    private String toAccount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction() {}

    public Transaction(String type, double amount, String details, String fromAccount, String toAccount, User user) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }

    // ✅ Getters
    public Long getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDetails() { return details; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getUser() { return user; }

    // ✅ Setters
    public void setId(Long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDetails(String details) { this.details = details; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setUser(User user) { this.user = user; }
}
