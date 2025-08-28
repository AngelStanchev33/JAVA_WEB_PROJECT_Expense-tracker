package com.example.expense_tracker.web;

import com.example.expense_tracker.exception.NotOwnerException;
import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExRatesDTO;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.dto.UpdateExpenseDto;
import com.example.expense_tracker.service.EventPublishingService;
import com.example.expense_tracker.service.ExRateService;
import com.example.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final EventPublishingService eventPublishingService;

    public ExpenseController(ExpenseService expenseService, EventPublishingService eventPublishingService) {
        this.expenseService = expenseService;
        this.eventPublishingService = eventPublishingService;
    }


    @PostMapping("/create")
    public ResponseEntity<ExpenseResponseDto> createExpense(
            @Valid @RequestBody CreateExpenseDto dto,
            Authentication authentication) {

        String userEmail = authentication.getName();
        ExpenseResponseDto response = expenseService.createExpense(dto, userEmail);

        eventPublishingService.publishExpenseCreatedEvent(response);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/my")
    public ResponseEntity<List<ExpenseResponseDto>> getUserExpenses(Authentication authentication) {
        String userEmail = authentication.getName();
        List<ExpenseResponseDto> expenses = expenseService.getUserExpenses(userEmail);

        return ResponseEntity.ok(expenses);
    }

    @PreAuthorize("isExpenseOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable Long id) {
        ExpenseResponseDto expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    @PreAuthorize("isExpenseOwner(#id)")
    @PutMapping("/update/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable Long id, @Valid
    @RequestBody UpdateExpenseDto updateExpenseDto) {
        ExpenseResponseDto expenseResponseDto = expenseService.updateExpense(id, updateExpenseDto);

        eventPublishingService.publishExpenseCreatedEvent(expenseResponseDto);

        return ResponseEntity.ok(expenseResponseDto);
    }

    @PreAuthorize("isExpenseOwner(#id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        expenseService.deleteExpense(id);

        return ResponseEntity.ok("expense with id " + id + " was deleted");
    }
}