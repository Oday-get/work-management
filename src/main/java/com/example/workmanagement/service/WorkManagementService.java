package com.example.workmanagement.service;

import com.example.workmanagement.dto.TransactionDTO;
import com.example.workmanagement.model.*;
import com.example.workmanagement.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WorkManagementService {

    private final PersonRepository personRepository;
    private final WorkRepository workRepository;
    private final DepositRepository depositRepository;
    private final DepositRequestRepository depositRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkManagementService(PersonRepository personRepository,
                                 WorkRepository workRepository,
                                 DepositRepository depositRepository,
                                 DepositRequestRepository depositRequestRepository,
                                 PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.workRepository = workRepository;
        this.depositRepository = depositRepository;
        this.depositRequestRepository = depositRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Transactional
    public Person addPerson(String name, String username, String password, Double initialBalance) {
        if (personRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("اسم المستخدم موجود مسبقاً، يرجى اختيار اسم آخر.");
        }

        Person person = new Person();
        person.setName(name);
        person.setUsername(username);
        person.setPassword(passwordEncoder.encode(password));
        person.setBalance(initialBalance != null ? initialBalance : 0.0);
        return personRepository.save(person);
    }

    @Transactional
    public Deposit deposit(Long personId, Double amount) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("الشخص غير موجود"));

        person.setBalance(person.getBalance() + amount);
        personRepository.save(person);

        Deposit deposit = new Deposit();
        deposit.setPerson(person);
        deposit.setAmount(amount);
        deposit.setDepositDate(LocalDate.now());
        return depositRepository.save(deposit);
    }

    @Transactional
    public Work addWork(Long personId, int phonesWithCharger, int phonesWithoutCharger, LocalDate date) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("الشخص غير موجود"));

        double totalDeduction = (phonesWithCharger * 1.0) + (phonesWithoutCharger * 2.0);
        person.setBalance(person.getBalance() - totalDeduction);
        personRepository.save(person);

        Work work = new Work();
        work.setPerson(person);
        work.setPhonesWithCharger(phonesWithCharger);
        work.setPhonesWithoutCharger(phonesWithoutCharger);
        work.setWorkDate(date != null ? date : LocalDate.now());
        return workRepository.save(work);
    }

    // --- إدارة طلبات الإيداع ---

    @Transactional
    public DepositRequest createDepositRequest(Long personId, Double amount) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("الشخص غير موجود"));

        DepositRequest request = new DepositRequest(person, amount);
        return depositRequestRepository.save(request);
    }

    public List<DepositRequest> getPendingDepositRequests() {
        return depositRequestRepository.findByStatusOrderByCreatedAtDesc(DepositRequest.Status.PENDING);
    }

    public List<DepositRequest> getUserDepositRequests(Long personId) {
        return depositRequestRepository.findByPersonIdOrderByCreatedAtDesc(personId);
    }

    @Transactional
    public void approveDepositRequest(Long requestId) {
        DepositRequest request = depositRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));

        if (request.getStatus() != DepositRequest.Status.PENDING) {
            throw new IllegalStateException("هذا الطلب تمت معالجته مسبقاً.");
        }

        deposit(request.getPerson().getId(), request.getAmount());

        request.setStatus(DepositRequest.Status.APPROVED);
        depositRequestRepository.save(request);
    }

    @Transactional
    public void rejectDepositRequest(Long requestId) {
        DepositRequest request = depositRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("الطلب غير موجود"));

        if (request.getStatus() != DepositRequest.Status.PENDING) {
            throw new IllegalStateException("هذا الطلب تمت معالجته مسبقاً.");
        }

        request.setStatus(DepositRequest.Status.REJECTED);
        depositRequestRepository.save(request);
    }

    public List<TransactionDTO> getAllTransactions(Long personIdFilter) {
        List<TransactionDTO> list = new ArrayList<>();

        List<Work> works = (personIdFilter != null)
                ? workRepository.findByPersonId(personIdFilter)
                : workRepository.findAll();

        for (Work w : works) {
            double cost = (w.getPhonesWithCharger() * 1.0) + (w.getPhonesWithoutCharger() * 2.0);
            list.add(new TransactionDTO(
                    w.getPerson().getName(),
                    "WORK",
                    w.getPhonesWithCharger(),
                    w.getPhonesWithoutCharger(),
                    cost,
                    w.getWorkDate()
            ));
        }

        List<Deposit> deposits = (personIdFilter != null)
                ? depositRepository.findByPersonId(personIdFilter)
                : depositRepository.findAll();

        for (Deposit d : deposits) {
            list.add(new TransactionDTO(
                    d.getPerson().getName(),
                    "DEPOSIT",
                    0,
                    0,
                    d.getAmount(),
                    d.getDepositDate()
            ));
        }

        // ⬆️ الترتيب التنازلي: الأحدث فوق والأقدم تحت
        list.sort(Comparator.comparing(TransactionDTO::getDate).reversed());

        return list;
    }
}