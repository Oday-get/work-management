package com.example.workmanagement.repository;

import com.example.workmanagement.model.DepositRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {
    List<DepositRequest> findByPersonIdOrderByCreatedAtDesc(Long personId);
    List<DepositRequest> findByStatusOrderByCreatedAtDesc(DepositRequest.Status status);
}