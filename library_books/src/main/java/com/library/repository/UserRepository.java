package com.library.repository;

import com.library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,Long> {
//    Page<User> findByFullNameContaining(String fullName, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNoContainingIgnoreCaseOrAddressContainingIgnoreCase(String searchQuery, String searchQuery1,String searchQuery2,String searchQuery3, Pageable page);
    boolean existsByPhoneNo(String phoneNo);
    boolean existsByEmail(String email);
}
