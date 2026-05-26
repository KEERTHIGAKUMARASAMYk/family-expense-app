package com.example.expense_tracker;


import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/all/{id}")
    public List<Expense> getExpensesById(@PathVariable Long id) {
        return expenseService.getExpensesById(id);
    }
    
    @PostMapping("/add")
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseService.addExpense(expense);
    }

     @PostMapping("/addList")
    public List<Expense> addListOfExpense(@RequestBody List<Expense> expenses) {
        return expenseService.addListOfExpense(expenses);
    }

    @PutMapping("/update/{id}")
    public Expense updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense) {
        return expenseService.updateExpense(id, updatedExpense);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return "expense deleted successfully!";
    }   

    @GetMapping("/monthly-total")
    public double getMonthlySummary(@RequestParam int year, @RequestParam int month) {
        return expenseService.getMonthlySummary(year, month);
    }

    @GetMapping("/category-summary")
    public Map<String, Double> getCategorySummary(@RequestParam int year, @RequestParam int month) {
        return expenseService.getCategorySummary(year, month);
    }   

    @PostMapping("/upload")
    public String uploadExpenses(@RequestBody MultipartFile file) {
        expenseService.uploadExpenses(file);  
        return "Expenses uploaded successfully!";
    }
}