package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.ExpenseNotFoundException;
import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.dto.UpdateExpenseDto;
import com.example.expense_tracker.model.entity.ExpenseEntity;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.model.event.ExpenseCreatedEvent;
import com.example.expense_tracker.repository.CategoryRepository;
import com.example.expense_tracker.repository.ExpenseRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.ExpenseService;
import com.example.expense_tracker.exception.CategoryNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ExpenseServiceImpl(ModelMapper modelMapper, UserRepository userRepository, CategoryRepository
            categoryRepository, ExpenseRepository expenseRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public ExpenseResponseDto createExpense(CreateExpenseDto createExpenseDto, String email) {
        ExpenseResponseDto expenseResponseDto = modelMapper.map(createExpenseDto, ExpenseResponseDto.class)
                .setUser(email);

        ExpenseEntity expenseEntity = saveInDb(expenseResponseDto);

        expenseResponseDto.setId(expenseEntity.getId());

        publishExpenseCreatedEvent(expenseResponseDto);

        return expenseResponseDto;
    }

    @Override
    public List<ExpenseResponseDto> getUserExpenses(String userEmail) {
        return expenseRepository
                .findAllByUserEmail(userEmail)
                .stream()
                .map(entity -> modelMapper.map(entity, ExpenseResponseDto.class)
                        .setCategory(entity.getCategory().getCategoryEnum().name())
                        .setUser(entity.getUser().getEmail()))
                .toList();
    }

    @Override
    public ExpenseResponseDto updateExpense(Long id, UpdateExpenseDto updateExpenseDto) {
        ExpenseEntity expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        expense.setAmount(updateExpenseDto.getAmount());
        expense.setCategory(categoryRepository
                .findByCategoryEnum(CategoryEnum.valueOf(
                        updateExpenseDto.getCategory()))
                .orElseThrow(() -> new CategoryNotFoundException(updateExpenseDto.getCategory())));
        expense.setExpenseDate(updateExpenseDto.getDate());
        expense.setDescription(updateExpenseDto.getDescription());

        expenseRepository.save(expense);

        return modelMapper.map(expense, ExpenseResponseDto.class)
                .setCategory(expense.getCategory().getCategoryEnum().name());

    }

    @Override
    public void deleteExpense(Long expenseId) {
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));

        expenseRepository.delete(expenseEntity);

    }

    @Override
    public boolean isOwner(Long id, String username) {
        return expenseRepository.findByIdAndUserEmail(id, username).isPresent();

    }

    private ExpenseEntity saveInDb(ExpenseResponseDto dto) {
        ExpenseEntity expenseEntity = modelMapper.map(dto, ExpenseEntity.class);
        expenseEntity.setUser(userRepository
                .findByEmail(dto.getUser())
                .orElseThrow(() -> new UserNotFoundException(dto.getUser())));
        expenseEntity.setCategory(categoryRepository.findByCategoryEnum(CategoryEnum.valueOf(dto.getCategory().toUpperCase()))
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategory())));
        return expenseRepository.save(expenseEntity);
    }

    private void publishExpenseCreatedEvent(ExpenseResponseDto expenseResponseDto) {
        applicationEventPublisher.publishEvent(new ExpenseCreatedEvent(
                        expenseResponseDto.getId(),
                        expenseResponseDto.getUser(),
                        expenseResponseDto.getAmount(),
                        expenseResponseDto.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                )
        );
    }
}
