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

            return new ChatResponse("Your balance is ₹" + acc.getBalance() );
        }

        // 2️⃣ LAST 5 TRANSACTIONS — clean output only
        if(msg.equals("transactions")){
            var list = transactionRepo.findTop5ByOrderByIdDesc();
            if(list.isEmpty()) return new ChatResponse("No recent transactions ❗");

            String data = list.stream()
                    .map(t -> t.getType() + " ₹" + t.getAmount())
                    .collect(Collectors.joining("<br>"));

            return new ChatResponse("Last 5 Transactions" + data);
        }

        // 3️⃣ ACCOUNT DETAILS
        if(msg.equals("account details")){
            var acc = accountRepo.findById(1L).orElse(null);
            if(acc == null) return new ChatResponse("Account not found ❗");

            var user = acc.getUser();

            String response =
                    "Account Details" +
                    "Name: "+ user.getName()  +
                    "Acc No: "+ acc.getAccountNumber()  +
                    "Balance: ₹"+ acc.getBalance();

            return new ChatResponse(response);
        }

        // 4️⃣ CONTACT
        if(msg.equals("contact")){
            return new ChatResponse("Email: shivanshchitranshi1009@gmail.com Phone: +91 7388292550");
        }

        return new ChatResponse("Click a button — Balance / Transactions / Account Details / Contact");
    }
}
