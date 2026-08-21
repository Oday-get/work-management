package com.example.workmanagement.repository;

import com.example.workmanagement.model.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    // البحث والتصفية عن الأعمال الخاصة بشخص معين بواسطة ID
    List<Work> findByPersonId(Long personId);
}