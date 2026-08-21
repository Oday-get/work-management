package com.example.workmanagement.controller;

import com.example.workmanagement.model.Deposit;
import com.example.workmanagement.model.Person;
import com.example.workmanagement.model.Work;
import com.example.workmanagement.repository.DepositRepository;
import com.example.workmanagement.repository.PersonRepository;
import com.example.workmanagement.repository.WorkRepository;
import com.example.workmanagement.service.WorkManagementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class WorkController {

    private final WorkManagementService service;
    private final PersonRepository personRepository;
    private final WorkRepository workRepository;
    private final DepositRepository depositRepository;

    public WorkController(WorkManagementService service,
                          PersonRepository personRepository,
                          WorkRepository workRepository,
                          DepositRepository depositRepository) {
        this.service = service;
        this.personRepository = personRepository;
        this.workRepository = workRepository;
        this.depositRepository = depositRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) Long personFilterId,
                        Model model) {

        model.addAttribute("persons", service.getAllPersons());
        model.addAttribute("transactions", service.getAllTransactions(personFilterId));
        model.addAttribute("selectedPersonId", personFilterId);
        model.addAttribute("pendingRequests", service.getPendingDepositRequests());

        // ==============================
        // التقارير والإحصائيات
        // ==============================

        List<Work> works = workRepository.findAll();
        List<Deposit> deposits = depositRepository.findAll();

        double totalWorkCost = 0.0;
        int totalPhonesWithCharger = 0;
        int totalPhonesWithoutCharger = 0;
        double totalDeposits = 0.0;

        // حساب إجمالي الأعمال وعدد الهواتف
        for (Work work : works) {

            int withCharger = work.getPhonesWithCharger() != null
                    ? work.getPhonesWithCharger()
                    : 0;

            int withoutCharger = work.getPhonesWithoutCharger() != null
                    ? work.getPhonesWithoutCharger()
                    : 0;

            totalPhonesWithCharger += withCharger;
            totalPhonesWithoutCharger += withoutCharger;

            // الهاتف بشاحن = 1 شيكل
            // الهاتف بدون شاحن = 2 شيكل
            totalWorkCost += (withCharger * 1.0) + (withoutCharger * 2.0);
        }

        // حساب إجمالي الإيداعات
        for (Deposit deposit : deposits) {

            if (deposit.getAmount() != null) {
                totalDeposits += deposit.getAmount();
            }
        }

        // إرسال البيانات إلى index.html
        model.addAttribute("totalWorkCost", totalWorkCost);
        model.addAttribute("totalPhonesWithCharger", totalPhonesWithCharger);
        model.addAttribute("totalPhonesWithoutCharger", totalPhonesWithoutCharger);
        model.addAttribute("totalDeposits", totalDeposits);

        return "index";
    }

    @PostMapping("/add-person")
    public String addPerson(@RequestParam String name,
                            @RequestParam String username,
                            @RequestParam String password,
                            @RequestParam(defaultValue = "0.0") Double balance,
                            RedirectAttributes redirectAttributes) {
        try {
            service.addPerson(name, username, password, balance);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "تمت إضافة الشخص بنجاح!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "حدث خطأ: " + e.getMessage()
            );
        }

        return "redirect:/";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long personId,
                          @RequestParam Double amount,
                          RedirectAttributes redirectAttributes) {
        try {
            service.deposit(personId, amount);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "تمت عملية الإيداع المباشر بنجاح!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "حدث خطأ أثناء تنفيذ الإيداع: " + e.getMessage()
            );
        }

        return "redirect:/";
    }

    @PostMapping("/add-work")
    public String addWork(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate workDate,

            @RequestParam("personId")
            List<Long> personIds,

            @RequestParam("phonesWithCharger")
            List<Integer> phonesWithChargers,

            @RequestParam("phonesWithoutCharger")
            List<Integer> phonesWithoutChargers,

            RedirectAttributes redirectAttributes) {

        try {

            int count = 0;

            for (int i = 0; i < personIds.size(); i++) {

                Long pId = personIds.get(i);

                Integer withC =
                        (phonesWithChargers != null
                                && i < phonesWithChargers.size())
                                ? phonesWithChargers.get(i)
                                : 0;

                Integer withoutC =
                        (phonesWithoutChargers != null
                                && i < phonesWithoutChargers.size())
                                ? phonesWithoutChargers.get(i)
                                : 0;

                if (pId != null && (withC > 0 || withoutC > 0)) {

                    service.addWork(
                            pId,
                            withC,
                            withoutC,
                            workDate
                    );

                    count++;
                }
            }

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "تمت إضافة (" + count + ") تسجيلات عمل بنجاح!"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "فشلت إضافة الأعمال: " + e.getMessage()
            );
        }

        return "redirect:/";
    }

    // ==============================
    // روابط المستخدم والإدارة
    // ==============================

    @GetMapping("/user/me")
    public String currentUserDashboard(Principal principal,
                                       Model model) {

        String username = principal.getName();

        Person person = personRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("المستخدم غير موجود")
                );

        model.addAttribute("person", person);

        model.addAttribute(
                "transactions",
                service.getAllTransactions(person.getId())
        );

        model.addAttribute(
                "userRequests",
                service.getUserDepositRequests(person.getId())
        );

        return "user";
    }

    @PostMapping("/user/request-deposit")
    public String requestDeposit(
            @RequestParam Double amount,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {

            String username = principal.getName();

            Person person = personRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new RuntimeException("المستخدم غير موجود")
                    );

            service.createDepositRequest(
                    person.getId(),
                    amount
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "تم إرسال طلب الإيداع بنجاح، وهو قيد المراجعة."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "فشل إرسال الطلب: " + e.getMessage()
            );
        }

        return "redirect:/user/me";
    }

    @PostMapping("/admin/approve-deposit")
    public String approveDeposit(
            @RequestParam Long requestId,
            RedirectAttributes redirectAttributes) {

        try {

            service.approveDepositRequest(requestId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "تم تأكيد الإيداع وإضافة المبلغ بنجاح!"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "فشلت عملية التأكيد: " + e.getMessage()
            );
        }

        return "redirect:/";
    }

    @PostMapping("/admin/reject-deposit")
    public String rejectDeposit(
            @RequestParam Long requestId,
            RedirectAttributes redirectAttributes) {

        try {

            service.rejectDepositRequest(requestId);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "تم رفض طلب الإيداع."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "حدث خطأ أثناء رفض الطلب: " + e.getMessage()
            );
        }

        return "redirect:/";
    }
}