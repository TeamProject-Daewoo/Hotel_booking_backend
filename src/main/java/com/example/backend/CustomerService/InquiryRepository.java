package com.example.backend.CustomerService;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
      List<Inquiry> findByUser_Username(String username, Sort sort);

  
}
