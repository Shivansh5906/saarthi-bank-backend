package com.saarthi.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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

    @PostMapping("/deposit")
    public String deposit(@RequestHeader("Authorization") String token,
                          @RequestBody DepositRequest request) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        return userService.deposit(email, request.getAmount());
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestHeader("Authorization") String token,
                           @RequestBody WithdrawRequest request) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        return userService.withdraw(email, request.getAmount());
    }

    @PostMapping("/transfer")
    public String transferMoney(@RequestHeader("Authorization") String token,
                                @RequestBody TransferRequest request) {

        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        return userService.transfer(email, request.getReceiverAccountNumber(), request.getAmount());
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userService.getTransactions(email);
    }

    // ✅ Download PDF Statement
    @GetMapping("/transactions/pdf")
    public void downloadPdf(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        try {
            token = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);

            List<Transaction> transactions = userService.getTransactions(email);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Transaction_History.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();
            document.add(new Paragraph("SAARTHI BANK - TRANSACTION STATEMENT\n\n"));

            for (Transaction tx : transactions) {
                String from = (tx.getFromAccount() != null) ? tx.getFromAccount() : "-";
                String to = (tx.getToAccount() != null) ? tx.getToAccount() : "-";

                String line = tx.getType() + " | ₹" + tx.getAmount() +
                        " | " + from + " → " + to +
                        " | " + tx.getTimestamp().toString();

                document.add(new Paragraph(line));
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }
}
