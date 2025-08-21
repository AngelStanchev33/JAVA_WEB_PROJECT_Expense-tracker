package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.impl.BudgetServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<BudgetResponseDto>> getBudgets(Authentication authentication) {
        String email = authentication.getName();

        List<BudgetResponseDto> userBudgets = budgetService.getUserBudgets(email);

        return ResponseEntity.ok(userBudgets);
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody @Valid CreateBudgetDto createBudgetDto,
                                                          Authentication authentication) {
        String userEmail = authentication.getName();

        BudgetResponseDto budget = budgetService.createBudget(createBudgetDto, userEmail);

        return ResponseEntity.ok(budget);
    }

    @PreAuthorize("isBudgetOwner(#id)")
    @PutMapping("/update/{id}")
    public ResponseEntity<BudgetResponseDto> editBudget(@RequestBody @Valid UpdateBudgetDto updateBudgetDto,
                                                        @PathVariable Long id) {
        BudgetResponseDto budgetResponseDto = budgetService.updateBudget(id, updateBudgetDto);

        return ResponseEntity.ok(budgetResponseDto);
    }

    @PreAuthorize("isBudgetOwner(#id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        System.out.println("im in");

        budgetService.deleteBudget(id);

        return ResponseEntity.ok("budget with id " + id + " is deleted");
    }
}
