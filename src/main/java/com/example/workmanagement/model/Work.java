package com.example.workmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "works")
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String name;
    private int numphone;
    private int numphone2;

    public Work() {} // Constructor فارغ متطلب لـ JPA

    public Work(String date, String name, int numphone, int numphone2) {
        this.date = date;
        this.name = name;
        this.numphone = numphone;
        this.numphone2 = numphone2;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumphone() { return numphone; }
    public void setNumphone(int numphone) { this.numphone = numphone; }

    public int getNumphone2() { return numphone2; }
    public void setNumphone2(int numphone2) { this.numphone2 = numphone2; }
}