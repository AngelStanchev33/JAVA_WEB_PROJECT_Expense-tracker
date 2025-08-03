package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.entity.CategoryEntity;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.repository.CategoryRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.ExpenseService;
import com.example.expense_tracker.service.exeption.CategoryNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ModelMapper modelMapper, UserRepository userRepository, CategoryRepository
            categoryRepository, ExpenseRepository expenseRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    public ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto, String email) {
        ExpenseResponseDto expenseResponseDto = modelMapper.map(createExpenseDto, ExpenseResponseDto.class)
                .setUser(email);

        ExpenseEntity expenseEntity = saveInDb(expenseResponseDto);

        expenseResponseDto.setId(expenseEntity.getId());

        return expenseResponseDto;
    }

    @Override
    public List<ExpenseResponseDto> getUserExpenses(String userEmail) {
        return expenseRepository
                .findByUserEmail(userEmail)
                .stream()
                .map(expenseEntity -> modelMapper.map(expenseEntity, ExpenseResponseDto.class)
                        .setCategory(expenseEntity.getCategory().getCategoryEnum().name()))
                .toList();
    }

    private ExpenseEntity saveInDb(ExpenseResponseDto dto) {
        ExpenseEntity expenseEntity = modelMapper.map(dto, ExpenseEntity.class);
        expenseEntity.setUser(userRepository
                .findByEmail(dto.getUser())
                .orElseThrow(() -> new UsernameNotFoundException("the email" + dto.getUser() + "is not found!")));
        expenseEntity.setCategory(categoryRepository.findByCategoryEnum(CategoryEnum.valueOf(dto.getCategory().toUpperCase())).orElseThrow(
                () -> new CategoryNotFoundException("Category was not found")));
        return expenseRepository.save(expenseEntity);
    }
}
