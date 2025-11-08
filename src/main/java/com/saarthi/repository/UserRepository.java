package com.saarthi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.saarthi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
}
