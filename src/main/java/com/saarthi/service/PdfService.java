package com.saarthi.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.saarthi.model.Transaction;
import com.saarthi.model.User;
import com.saarthi.repository.TransactionRepository;
import com.saarthi.repository.UserRepository;

@Service
public class PdfService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public PdfService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public ByteArrayInputStream generatePdf(String email) {

        User user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) return null;

        List<Transaction> transactions = transactionRepository.findByUserOrderByTimestampDesc(user);

        StringBuilder sb = new StringBuilder();
        sb.append("SAARTHI BANK - Transaction History\n\n");
        sb.append("Type\tAmount\tFrom → To\tDate & Time\n");
        sb.append("--------------------------------------------------------------\n");

        for (Transaction tx : transactions) {
            sb.append(tx.getType()).append("\t")
              .append("₹").append(tx.getAmount()).append("\t")
              .append((tx.getFromAccount() != null ? tx.getFromAccount() : "-"))
              .append(" → ")
              .append((tx.getToAccount() != null ? tx.getToAccount() : "-"))
              .append("\t")
              .append(tx.getTimestamp())
              .append("\n");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
