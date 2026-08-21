package com.example.workmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "works")
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer phonesWithCharger;    // عدد الهواتف بشاحن (1 ₪)

    private Integer phonesWithoutCharger; // عدد الهواتف بدون شاحن (2 ₪)

    private Double totalSalary;           // المجموع الإجمالي بالشيقل

    private LocalDate workDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    public Work() {
    }

    public Work(Integer phonesWithCharger, Integer phonesWithoutCharger, Double totalSalary, LocalDate workDate, Person person) {
        this.phonesWithCharger = phonesWithCharger;
        this.phonesWithoutCharger = phonesWithoutCharger;
        this.totalSalary = totalSalary;
        this.workDate = workDate;
        this.person = person;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPhonesWithCharger() {
        return phonesWithCharger;
    }

    public void setPhonesWithCharger(Integer phonesWithCharger) {
        this.phonesWithCharger = phonesWithCharger;
    }

    public Integer getPhonesWithoutCharger() {
        return phonesWithoutCharger;
    }

    public void setPhonesWithoutCharger(Integer phonesWithoutCharger) {
        this.phonesWithoutCharger = phonesWithoutCharger;
    }

    public Double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(Double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}