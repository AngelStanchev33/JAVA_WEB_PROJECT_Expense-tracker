package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.BudgetService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public BudgetServiceImpl(BudgetRepository budgetRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BudgetResponseDto createBudget(CreateBudgetDto createBudgetDto, String email) {
        BudgetResponseDto budgetResponseDto = modelMapper.map(createBudgetDto, BudgetResponseDto.class)
                .setUser(email)
                .setSpent(BigDecimal.ZERO)
                .setRemaining(createBudgetDto.getBudgetLimit())
                .setSpentPercentage((double) 0);

        BudgetEntity budgetEntity = saveInDb(budgetResponseDto);

        budgetResponseDto.setId(budgetEntity.getId());

        return budgetResponseDto;
    }

    @Override
    public List<BudgetResponseDto> getUserBudgets(String email) {
        return budgetRepository.findByUserEmail(email)
                .stream()
                .map(entity -> modelMapper.map(entity, BudgetResponseDto.class)
                        .setUser(entity.getUser().getEmail()))
                .toList();
    }


    private BudgetEntity saveInDb(BudgetResponseDto budgetResponseDto) {
        BudgetEntity budgetEntity = modelMapper.map(budgetResponseDto, BudgetEntity.class);
        budgetEntity.setUser(userRepository.findByEmail(budgetResponseDto.getUser())
                .orElseThrow(() -> new UserNotFoundException(budgetResponseDto.getUser())));
        System.out.println();

        budgetRepository.save(budgetEntity);

        return budgetEntity;
    }
}
