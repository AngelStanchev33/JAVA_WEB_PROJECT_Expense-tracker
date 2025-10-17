package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.CurrencyNotFoundException;
import com.example.expense_tracker.exception.ExpenseNotFoundException;
import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.dto.UpdateExpenseDto;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.repository.CategoryRepository;
import com.example.expense_tracker.repository.CurrencyRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.ExpenseService;
import com.example.expense_tracker.exception.CategoryNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final CurrencyRepository currencyRepository;

    public ExpenseServiceImpl(ModelMapper modelMapper, UserRepository userRepository, CategoryRepository
            categoryRepository, ExpenseRepository expenseRepository, CurrencyRepository currencyRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto, String email) {
        ExpenseEntity expenseEntity = modelMapper.map(
                createExpenseDto, ExpenseEntity.class);
        expenseEntity.setUser(userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email)));
        try {
            CategoryEnum categoryEnum = CategoryEnum.valueOf(createExpenseDto.getCategory());
            expenseEntity.setCategory(categoryRepository.findByCategoryEnum(categoryEnum)
                    .orElseThrow(() -> new CategoryNotFoundException(createExpenseDto.getCategory())));
        } catch (IllegalArgumentException e) {
            throw new CategoryNotFoundException(createExpenseDto.getCategory());
        }
        expenseEntity.setCurrency(currencyRepository.findByCode(createExpenseDto.getCurrencyCode())
                .orElseThrow(() -> new CurrencyNotFoundException(createExpenseDto.getCurrencyCode())));


        expenseRepository.save(expenseEntity);

        return buildExpenseResponseDto(expenseEntity);
    }

    @Override
    public List<ExpenseResponseDto> getUserExpenses(String userEmail) {
        return expenseRepository
                .findAllByUserEmail(userEmail)
                .stream()
                .map(this::buildExpenseResponseDto)
                .toList();
    }

    @Override
    public ExpenseResponseDto getExpenseById(Long id) {
        ExpenseEntity expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        return buildExpenseResponseDto(expense);
    }

    @Override
    public ExpenseResponseDto updateExpense(Long id, UpdateExpenseDto updateExpenseDto) {
        ExpenseEntity expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        if (updateExpenseDto.getAmount() != null) {
            expense.setAmount(updateExpenseDto.getAmount());
        }
        if (updateExpenseDto.getCategory() != null) {
            expense.setCategory(categoryRepository
                    .findByCategoryEnum(CategoryEnum.valueOf(updateExpenseDto.getCategory()))
                    .orElseThrow(() -> new CategoryNotFoundException(updateExpenseDto.getCategory())));
        }
        if (updateExpenseDto.getDate() != null) {
            expense.setExpenseDate(updateExpenseDto.getDate());
        }
        if (updateExpenseDto.getDescription() != null) {
            expense.setDescription(updateExpenseDto.getDescription());
        }
        if (updateExpenseDto.getCurrency() != null) {
            expense.setCurrency(currencyRepository.findByCode(updateExpenseDto.getCurrency())
                    .orElseThrow(() -> new CurrencyNotFoundException(updateExpenseDto.getCurrency())));
        }

        expense = expenseRepository.save(expense);

        return buildExpenseResponseDto(expense);
    }

    @Override
    public void deleteExpense(Long expenseId) {
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        expenseRepository.deleteById(expenseId);

    }

    @Override
    public boolean isOwner(Long id, String username) {
        return expenseRepository.findByIdAndUserEmail(id, username).isPresent();

    }


    private ExpenseResponseDto buildExpenseResponseDto(ExpenseEntity entity) {
        return modelMapper.map(entity, ExpenseResponseDto.class)
                .setCategory(entity.getCategory().getCategoryEnum().name())
                .setUser(entity.getUser().getEmail())
                .setCurrencyCode(entity.getCurrency().getCode());
    }

}
