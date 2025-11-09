package com.saarthi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.saarthi.service.PdfService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PdfService pdfService;

    private String extractEmail(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "").trim();
        return jwtUtil.extractEmail(token).toLowerCase();
    }

    @GetMapping("/me")
    public UserResponse getUserDetails(@RequestHeader("Authorization") String tokenHeader) {
        String email = extractEmail(tokenHeader);
        User user = userRepository.findByEmailIgnoreCase(email);
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAccount().getAccountNumber(),
                user.getAccount().getBalance()
        );
    }

    @PostMapping("/deposit")
    public String deposit(@RequestHeader("Authorization") String tokenHeader,
                          @RequestBody DepositRequest request) {
        String email = extractEmail(tokenHeader);
        return userService.deposit(email, request.getAmount());
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestHeader("Authorization") String tokenHeader,
                           @RequestBody WithdrawRequest request) {
        String email = extractEmail(tokenHeader);
        return userService.withdraw(email, request.getAmount());
    }

    @PostMapping("/transfer")
    public String transferMoney(@RequestHeader("Authorization") String tokenHeader,
                                @RequestBody TransferRequest request) {
        String email = extractEmail(tokenHeader);
        return userService.transfer(email, request.getReceiverAccountNumber(), request.getAmount());
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestHeader("Authorization") String tokenHeader) {
        String email = extractEmail(tokenHeader);
        return userService.getTransactions(email);
    }

    // ✅ PDF Download Endpoint
    @GetMapping("/transactions/pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestHeader("Authorization") String tokenHeader) {
        String email = extractEmail(tokenHeader);
        byte[] pdfBytes = pdfService.generatePdf(email);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Transaction_History.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
