package com.saarthi.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saarthi.model.Transaction;
import com.saarthi.model.User;
import com.saarthi.repository.UserRepository;
import com.saarthi.repository.TransactionRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class PdfService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public byte[] generatePdf(String email) {
        try {
            User user = userRepository.findByEmailIgnoreCase(email);
            List<Transaction> transactions = transactionRepository.findByUserOrderByTimestampDesc(user);

            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font tableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font tableContent = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("SAARTHI BANK - Transaction Statement", titleFont));
            document.add(new Paragraph("Account Holder: " + user.getName()));
            document.add(new Paragraph("Account Number: " + user.getAccount().getAccountNumber()));
            document.add(new Paragraph("Email: " + user.getEmail()));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{3, 3, 4, 4, 5});

            table.addCell(new PdfPCell(new Phrase("Type", tableHeader)));
            table.addCell(new PdfPCell(new Phrase("Amount (₹)", tableHeader)));
            table.addCell(new PdfPCell(new Phrase("From", tableHeader)));
            table.addCell(new PdfPCell(new Phrase("To", tableHeader)));
            table.addCell(new PdfPCell(new Phrase("Date & Time", tableHeader)));

            for (Transaction tx : transactions) {
                table.addCell(new PdfPCell(new Phrase(tx.getType(), tableContent)));
                table.addCell(new PdfPCell(new Phrase("₹ " + tx.getAmount(), tableContent)));
                table.addCell(new PdfPCell(new Phrase(tx.getFromAccount(), tableContent)));
                table.addCell(new PdfPCell(new Phrase(tx.getToAccount(), tableContent)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(tx.getTimestamp()), tableContent)));
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
