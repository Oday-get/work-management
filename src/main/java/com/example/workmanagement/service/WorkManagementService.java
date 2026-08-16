package com.example.workmanagement.service;

import com.example.workmanagement.model.Person;
import com.example.workmanagement.model.Work;
import com.example.workmanagement.repository.PersonRepository;
import com.example.workmanagement.repository.WorkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkManagementService {

    private final PersonRepository personRepository;
    private final WorkRepository workRepository;

    public WorkManagementService(PersonRepository personRepository, WorkRepository workRepository) {
        this.personRepository = personRepository;
        this.workRepository = workRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public List<Work> getAllWorks() {
        return workRepository.findAll();
    }

    public boolean addPerson(String name) {
        if (personRepository.findByNameIgnoreCase(name).isPresent()) {
            return false; // الاسم موجود مسبقاً
        }
        personRepository.save(new Person(name));
        return true;
    }

    public void addWork(String date, String name, int numphone, int numphone2) {
        workRepository.save(new Work(date, name, numphone, numphone2));
    }

    public List<Work> searchByName(String searchName) {
        return workRepository.findByNameIgnoreCase(searchName);
    }
}