package com.saarthi.service;

import java.util.List;  // ✅ IMPORT THIS

import com.saarthi.dto.LoginRequest;
import com.saarthi.dto.SignupRequest;
import com.saarthi.dto.UserResponse;
import com.saarthi.model.Transaction;  // ✅ IMPORT THIS

public interface UserService {

    String signup(SignupRequest request);

    String login(LoginRequest request);

    String deposit(String email, double amount);

    String withdraw(String email, double amount);   // ✅ ensure withdraw is here

    String transfer(String senderEmail, String receiverAccNo, double amount);

    UserResponse getUserDetails(String email);

    List<Transaction> getTransactions(String email);  // ✅ returns user's history
}
