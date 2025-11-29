package com.saarthi.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.saarthi.repository.UserRepository;
import com.saarthi.repository.AccountRepository;
import com.saarthi.repository.TransactionRepository;
import java.util.stream.Collectors;
import java.util.stream.*; 

import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "https://saarthibank.netlify.app")
public class ChatController {

    @Autowired UserRepository userRepo;
    @Autowired AccountRepository accountRepo;
    @Autowired TransactionRepository transactionRepo;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {

        String msg = req.getMessage().toLowerCase().trim();

        // 1) BALANCE
        if(msg.equals("balance")){
            var acc = accountRepo.findById(1L).orElse(null);
            if(acc == null) return new ChatResponse("No account found ❗");
            return new ChatResponse("💰 Your balance is: ₹" + acc.getBalance());
        }

        // 2) LAST 5 TRANSACTIONS
        if(msg.equals("transactions")){
            var list = transactionRepo.findTop5ByOrderByIdDesc();
            if(list.isEmpty()) return new ChatResponse("No transactions found ❗");

            String data = list.stream()
                    .map(t -> "• "+t.getType()+" ₹"+t.getAmount())
                    .collect(Collectors.joining("\n"));

            return new ChatResponse("📄 Last 5 transactions:\n\n" + data);
        }

        // 3) ACCOUNT DETAILS
        if(msg.equals("account details")){
            var acc = accountRepo.findById(1L).orElse(null);
            if(acc == null) return new ChatResponse("Account not found ❗");

            var user = acc.getUser();

            return new ChatResponse(
                    "🏦 Account Details:\n"
                    + "Name: " + user.getName() + "\n"
                    + "Account No: " + acc.getAccountNumber() + "\n"
                    + "Email: " + user.getEmail() + "\n"
                    + "Balance: ₹" + acc.getBalance()
            );
        }

        // 4) CONTACT SUPPORT
        if(msg.equals("contact")){
            return new ChatResponse(
                "📞 Saarthi Bank Support\n"
                + "Email: shivanshchitranshi1009@gmail.com\n"
                + "Phone: +91 7388292550"
            );
        }

        return new ChatResponse("⚠ Please tap a button — Balance / Transactions / Account Details / Contact");
    }
}
