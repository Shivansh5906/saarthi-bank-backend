package com.saarthi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.saarthi.config.JwtUtil;
import com.saarthi.dto.DepositRequest;
import com.saarthi.dto.TransferRequest;
import com.saarthi.dto.UserResponse;
import com.saarthi.dto.WithdrawRequest;
import com.saarthi.model.Transaction;
import com.saarthi.model.User;
import com.saarthi.repository.UserRepository;
import com.saarthi.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserResponse getUserDetails(@RequestHeader("Authorization") String token) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmailIgnoreCase(email);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAccount().getAccountNumber(),
                user.getAccount().getBalance()
        );
    }

    // ✅ DEPOSIT
    @PostMapping("/deposit")
    public String deposit(@RequestHeader("Authorization") String token,
                          @RequestBody DepositRequest request) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        return userService.deposit(email, request.getAmount());
    }

    // ✅ WITHDRAW
    @PostMapping("/withdraw")
    public String withdraw(@RequestHeader("Authorization") String token,
                           @RequestBody WithdrawRequest request) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        return userService.withdraw(email, request.getAmount());
    }

    // ✅ TRANSFER
    @PostMapping("/transfer")
    public String transferMoney(@RequestHeader("Authorization") String token,
                                @RequestBody TransferRequest request) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        return userService.transfer(email, request.getReceiverAccountNumber(), request.getAmount());
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // ✅ Same style as other methods
        String email = jwtUtil.extractEmail(token);
        return userService.getTransactions(email);
    }


}
