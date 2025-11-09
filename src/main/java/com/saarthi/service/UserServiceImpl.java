package com.saarthi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.saarthi.config.JwtUtil;
import com.saarthi.dto.LoginRequest;
import com.saarthi.dto.SignupRequest;
import com.saarthi.dto.UserResponse;
import com.saarthi.model.Account;
import com.saarthi.model.Transaction;
import com.saarthi.model.User;
import com.saarthi.repository.UserRepository;
import com.saarthi.repository.AccountRepository;
import com.saarthi.repository.TransactionRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String signup(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            return "Email already registered!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userRepository.save(user);

        Account acc = new Account();
        acc.setUser(user);
        acc.setAccountNumber("AC" + System.currentTimeMillis());
        acc.setBalance(0.0);

        accountRepository.save(acc);

        return "Signup Successful! Account Created: " + acc.getAccountNumber();
    }

    @Override
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password!");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return token; // ✅ Only return token
    }


    // ✅ DEPOSIT
    @Transactional
    @Override
    public String deposit(String email, double amount) {
        User user = userRepository.findByEmail(email);
        if (user == null) return "User not found!";

        Account account = user.getAccount();
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        transactionRepository.save(new Transaction("DEPOSIT", amount, "Amount Deposited", user));

        return "₹" + amount + " deposited successfully! New Balance: ₹" + account.getBalance();
    }

    // ✅ WITHDRAW
    @Transactional
    @Override
    public String withdraw(String email, double amount) {
        User user = userRepository.findByEmail(email);
        if (user == null) return "User not found!";

        Account account = user.getAccount();
        if (account.getBalance() < amount) return "Insufficient Balance!";

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        transactionRepository.save(new Transaction("WITHDRAW", amount, "Amount Withdrawn", user));

        return "₹" + amount + " withdrawn successfully! Remaining Balance: ₹" + account.getBalance();
    }

    // ✅ FINAL FIXED TRANSFER
    @Transactional
    @Override
    public String transfer(String senderEmail, String receiverAccountNumber, double amount) {

        User sender = userRepository.findByEmail(senderEmail);
        if (sender == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found!");
        }

        User receiver = userRepository.findByAccount_AccountNumber(receiverAccountNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User not found with this Account Number!"
                ));

        if (sender.getAccount().getBalance() < amount) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient Balance!"
            );
        }

        // Update balances
        sender.getAccount().setBalance(sender.getAccount().getBalance() - amount);
        receiver.getAccount().setBalance(receiver.getAccount().getBalance() + amount);

        // Save accounts
        accountRepository.save(sender.getAccount());
        accountRepository.save(receiver.getAccount());

        // Record transactions
        transactionRepository.save(new Transaction("TRANSFER", amount, "Amount Transferred", sender));
        transactionRepository.save(new Transaction("RECEIVED", amount, "Amount Received", receiver));

        return "₹" + amount + " Transferred Successfully!";
    }

    // ✅ GET USER DETAILS
    @Override
    public UserResponse getUserDetails(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAccount().getAccountNumber(),
                user.getAccount().getBalance()
        );
    }

    // ✅ GET TRANSACTION HISTORY
    @Override
    public List<Transaction> getTransactions(String email) {

        User user = userRepository.findByEmail(email);
        if (user == null) return null;

        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }
}
