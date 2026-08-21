package com.example.workmanagement.dto;

import java.time.LocalDate;

public class TransactionDTO {

    private String personName;
    private String type; // "WORK" أو "DEPOSIT"
    private Integer phonesWithCharger;
    private Integer phonesWithoutCharger;
    private Double amount;
    private LocalDate date;

    public TransactionDTO() {
    }

    public TransactionDTO(String personName, String type, Integer phonesWithCharger, Integer phonesWithoutCharger, Double amount, LocalDate date) {
        this.personName = personName;
        this.type = type;
        this.phonesWithCharger = phonesWithCharger;
        this.phonesWithoutCharger = phonesWithoutCharger;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}