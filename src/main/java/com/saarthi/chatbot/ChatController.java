package com.saarthi.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.saarthi.repository.UserRepository;
import com.saarthi.repository.AccountRepository;
import com.saarthi.repository.TransactionRepository;

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

        // 1️⃣ BALANCE
        if(msg.equals("balance")){
            var acc = accountRepo.findById(1L).orElse(null);
            if(acc == null) return new ChatResponse("No account found ❗");

            return new ChatResponse("<b>Your balance is ₹" + acc.getBalance() + "</b>");
        }

        // 2️⃣ LAST 5 TRANSACTIONS — clean output only
        if(msg.equals("transactions")){
            var list = transactionRepo.findTop5ByOrderByIdDesc();
            if(list.isEmpty()) return new ChatResponse("No recent transactions ❗");

            String data = list.stream()
                    .map(t -> t.getType() + " ₹" + t.getAmount())
                    .collect(Collectors.joining("<br>"));

            return new ChatResponse("<b>Last 5 Transactions</b><br>" + data);
        }

        // 3️⃣ ACCOUNT DETAILS
        if(msg.equals("account details")){
            var acc = accountRepo.findById(1L).orElse(null);
            if(acc == null) return new ChatResponse("Account not found ❗");

            var user = acc.getUser();

            String response =
                    "<b>Account Details</b><br>" +
                    "Name: "+ user.getName() +"<br>" +
                    "Acc No: "+ acc.getAccountNumber() +"<br>" +
                    "Balance: ₹"+ acc.getBalance();

            return new ChatResponse(response);
        }

        // 4️⃣ CONTACT
        if(msg.equals("contact")){
            return new ChatResponse("<b>Email:</b> shivanshchitranshi1009@gmail.com<br><b>Phone:</b> +91 7388292550");
        }

        return new ChatResponse("Click a button — Balance / Transactions / Account Details / Contact");
    }
}
