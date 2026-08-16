package com.example.workmanagement.controller;

import com.example.workmanagement.model.Work;
import com.example.workmanagement.service.WorkManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@Controller
public class WorkController {

    private final WorkManagementService workService;

    public WorkController(WorkManagementService workService) {
        this.workService = workService;
    }

    // 1. عرض الصفحة الرئيسية مع الجداول والقوائم
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("persons", workService.getAllPersons());
        model.addAttribute("works", workService.getAllWorks());
        return "index"; // يشير إلى صفحة index.html
    }

    // 2. استقبال طلب إضافة شخص جديد
    @PostMapping("/add-person")
    public String addPerson(@RequestParam("name") String name, Model model) throws FileNotFoundException {
        boolean success = workService.addPerson(name);
        if (!success) {
            model.addAttribute("error", "الاسم موجود بالفعل، يرجى اختيار اسم آخر!");
        }
        return "redirect:/";
    }

    // 3. استقبال طلب إضافة عمل جديد
    @PostMapping("/add-work")
    public String addWork(@RequestParam("date") String date,
                          @RequestParam("name") String name,
                          @RequestParam("numphone") int numphone,
                          @RequestParam("numphone2") int numphone2) {
        workService.addWork(date, name, numphone, numphone2);
        return "redirect:/";
    }

    // 4. البحث عن سجلات شخص ومعرفة حسابه
    @GetMapping("/search")
    public String search(@RequestParam("searchName") String searchName, Model model) {
        List<Work> results = workService.searchByName(searchName);

        int totalPhone1 = 0;
        int totalPhone2 = 0;
        for (Work w : results) {
            totalPhone1 += w.getNumphone();
            totalPhone2 += w.getNumphone2();
        }
        int totalSalary = totalPhone1 + (totalPhone2 * 2);

        model.addAttribute("searchResults", results);
        model.addAttribute("searchName", searchName);
        model.addAttribute("totalPhone1", totalPhone1);
        model.addAttribute("totalPhone2", totalPhone2);
        model.addAttribute("totalSalary", totalSalary);

        model.addAttribute("persons", workService.getAllPersons());
        model.addAttribute("works", workService.getAllWorks());
        return "index";
    }
}