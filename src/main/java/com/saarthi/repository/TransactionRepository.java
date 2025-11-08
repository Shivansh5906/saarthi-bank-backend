package com.saarthi.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saarthi.model.Transaction;
import com.saarthi.model.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ✅ This returns ALL transactions of the user sorted newest first
    List<Transaction> findByUserOrderByTimestampDesc(User user);
}
