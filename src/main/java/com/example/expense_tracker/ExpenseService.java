package com.example.expense_tracker;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

	public List<Expense> getAllExpenses() {

		return expenseRepository.findAll();
	}

    public List<Expense> getExpensesById(Long id) {
        return expenseRepository.findById(id)
                .map(List::of)
                .orElse(List.of());
    }

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> addListOfExpense(List<Expense> expenses) {
        return expenseRepository.saveAll(expenses);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {
        return expenseRepository.findById(id)
                .map(expense -> {
                    expense.setAmount(updatedExpense.getAmount());
                    expense.setDescription(updatedExpense.getDescription());
                    expense.setTransactionDate(updatedExpense.getTransactionDate());
                   // expense.setLocation(updatedExpense.getLocation());
                    return expenseRepository.save(expense);
                })
                .orElseThrow(() -> new RuntimeException("Expense not found with id " + id));
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id " + id);
        }
        expenseRepository.deleteById(id);
    }

    public double getMonthlySummary(int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = expenseRepository.findByTransactionDateBetween(startDate,endDate);
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<String, Double> getCategorySummary(int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());


        List<Expense> expenses = expenseRepository.findByTransactionDateBetween(startDate, endDate);
        return expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, 
                    Collectors.summingDouble(Expense::getAmount)));
            }

    @Transactional
    public void uploadExpenses(MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                
                    Expense expense = new Expense();
                    expense.setTransactionDate(LocalDate.parse(fields[0]));
                    expense.setDescription(fields[1]);
                     expense.setAmount(Double.parseDouble(fields[2]));
                    expense.setCategory(detectCategory(fields[1]));
                    expenseRepository.save(expense);
                
            }
        
    } catch (Exception e) {
            throw new RuntimeException("Failed to upload expenses: " + e.getMessage());
        }
       
    }

    private String detectCategory(String description) {
        String lowerDesc = description.toUpperCase();
        if (lowerDesc.contains("WMT SUPRCTR") || lowerDesc.contains("FRUITICANA") || lowerDesc.contains("REAL CDN")) {
            return "GROCERY";
        } else if (lowerDesc.contains("RESTAURANT") || lowerDesc.contains("cafe") || lowerDesc.contains("DOSA")|| lowerDesc.contains("INDIAN")) {
            return "RESTAURANT";
        }else if (lowerDesc.contains("COSTCO") || lowerDesc.contains("DOLLARAMA") || lowerDesc.contains("CAB")) {
            return "SHOPPING";
        }else if (lowerDesc.contains("uber") || lowerDesc.contains("SOUTH COAST") || lowerDesc.contains("COMPASS")) {
            return "TRANSPORT";
        }else if (lowerDesc.contains("BCAA") || lowerDesc.contains("PRIME") || lowerDesc.contains("COMPASS")) {
            return "MEMBERSHIP";
        } else {
            return "Other";
        }
        
    }

   

}
