package com.example.workmanagement.config;

import com.example.workmanagement.model.Person;
import com.example.workmanagement.repository.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (personRepository.findByUsername("oday20").isEmpty()) {
            Person admin = new Person();
            admin.setName("مدير النظام");
            admin.setUsername("oday20");
            admin.setPassword(passwordEncoder.encode("od2602ji"));
            admin.setRole("ROLE_ADMIN");
            admin.setBalance(0.0);

            personRepository.save(admin);
            System.out.println("✅ تم إنشاء حساب المدير الأساسي بنجاح: oday20");
        }
    }
}