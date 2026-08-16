package com.example.workmanagement.repository;

import com.example.workmanagement.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findByNameIgnoreCase(String name);
}