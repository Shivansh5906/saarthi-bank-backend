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
            return new ChatResponse("Your balance is ₹" + acc.getBalance());
        }

        // 2) LAST 5 TRANSACTIONS – simple clean format
        if(msg.equals("transactions")){
            var list = transactionRepo.findTop5ByOrderByIdDesc();
            if(list.isEmpty()) return new ChatResponse("No recent transactions ❗");

            String data = list.stream()
                    .map(t -> t.getType() + " ₹" + t.getAmount())
                    .collect(Collectors.joining("\n"));

            return new ChatResponse(data); // <-- clean output only
        }

        // 3) ACCOUNT DETAILS (single txt reply)
        if(msg.equals("account details")){
            var acc = accountRepo.findById(1L).orElse(null);
            if(acc == null) return new ChatResponse("Account not found ❗");

            var user = acc.getUser();

            return new ChatResponse(
                    "Name: "+ user.getName() +
                    "\nAcc No: "+ acc.getAccountNumber() +
                    "\nBalance: ₹"+ acc.getBalance()
            );
        }

        // 4) CONTACT
        if(msg.equals("contact")){
            return new ChatResponse(
                "Email: support@saarthibank.com\nPhone: +91 98765-43210"
            );
        }

        return new ChatResponse("Click a button — Balance / Transactions / Account Details / Contact");
    }
}