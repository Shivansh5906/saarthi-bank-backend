package com.saarthi.controller;

import com.saarthi.config.JwtUtil;
import com.saarthi.model.Transaction;
import com.saarthi.model.User;
import com.saarthi.repository.TransactionRepository;
import com.saarthi.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/transactions/pdf")
    public void downloadPdf(@RequestParam("token") String token, HttpServletResponse response) throws IOException, DocumentException {

       
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmailIgnoreCase(email);
        List<Transaction> transactions = transactionRepository.findByUserOrderByTimestampDesc(user);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=MiniStatement.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
        document.add(new Paragraph("BANK MINI STATEMENT\n\n", titleFont));

        // User details
        document.add(new Paragraph("Name: " + user.getName()));
        document.add(new Paragraph("Account Number: " + user.getAccount().getAccountNumber()));
        document.add(new Paragraph("Email: " + user.getEmail() + "\n\n"));

        // Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        table.addCell("Type");
        table.addCell("Amount (₹)");
        table.addCell("Details");
        table.addCell("Date & Time");

        for (Transaction tx : transactions) {
            table.addCell(tx.getType());
            table.addCell(String.valueOf(tx.getAmount()));
            table.addCell(tx.getDetails());
            table.addCell(tx.getTimestamp().toString());
        }

        document.add(table);
        document.close();
    }
}
