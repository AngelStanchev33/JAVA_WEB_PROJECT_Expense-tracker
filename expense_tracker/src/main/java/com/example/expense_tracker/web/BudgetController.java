package com.example.expense_tracker.web;

import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.EventPublishingService;
import com.example.expense_tracker.service.ExRateService;
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

    private final EventPublishingService eventPublishingService;

    public BudgetController(BudgetService budgetService, EventPublishingService eventPublishingService) {
        this.budgetService = budgetService;
        this.eventPublishingService = eventPublishingService;
    }


    @GetMapping("/my")
    public ResponseEntity<List<BudgetResponseDto>> getBudgets(Authentication authentication) {
        String email = authentication.getName();

        List<BudgetResponseDto> userBudgets = budgetService.getUserBudgets(email);

        return ResponseEntity.ok(userBudgets);
    }

    @PreAuthorize("hasRole('ADMIN') or isBudgetOwner(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> getBudgetById(@PathVariable Long id) {
        BudgetResponseDto budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @PostMapping("/create")
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody @Valid CreateBudgetDto createBudgetDto,
                                                          Authentication authentication) {
        String userEmail = authentication.getName();

        BudgetResponseDto budget = budgetService.createBudget(createBudgetDto, userEmail);

        eventPublishingService.publishBudgetCreatedEvent(budget);

        return ResponseEntity.ok(budget);
    }

    @PreAuthorize("hasRole('ADMIN') or isBudgetOwner(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> editBudget(@RequestBody @Valid UpdateBudgetDto updateBudgetDto,
                                                        @PathVariable Long id) {
        BudgetResponseDto budgetResponseDto = budgetService.updateBudget(id, updateBudgetDto);

        eventPublishingService.publishBudgetCreatedEvent(budgetResponseDto);

        return ResponseEntity.ok(budgetResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN') or isBudgetOwner(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.ok().build();
    }
}
