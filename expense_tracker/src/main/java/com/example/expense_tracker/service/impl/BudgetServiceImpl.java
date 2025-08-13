package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.BudgetNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.BudgetService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    public BudgetResponseDto updateBudget(Long budgetId, UpdateBudgetDto updateBudgetDto) {
        BudgetResponseDto budgetResponseDto = modelMapper.map(updateBudgetDto, BudgetResponseDto.class);

        BudgetEntity entity = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        entity.setBudgetLimit(updateBudgetDto.getBudgetLimit());
        entity.setMonth(updateBudgetDto.getMonth());

        budgetRepository.save(entity);

        return budgetResponseDto;
    }

    @Override
    public List<BudgetResponseDto> getUserBudgets(String email) {
        return budgetRepository.findByUserEmail(email)
                .stream()
                .map(entity -> {
                    BudgetResponseDto dto = modelMapper.map(entity, BudgetResponseDto.class)
                            .setUser(entity.getUser().getEmail());

                    BigDecimal remaining = entity.getBudgetLimit().subtract(entity.getSpent());
                    dto.setRemaining(remaining);

                    if (entity.getBudgetLimit().compareTo(BigDecimal.ZERO) > 0) {
                        double percentage = entity.getSpent()
                                .divide(entity.getBudgetLimit(), 0, java.math.RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))
                                .doubleValue();
                        dto.setSpentPercentage(percentage);
                    } else {
                        dto.setSpentPercentage(0.0);
                    }

                    return dto;
                })
                .toList();
    }


    private BudgetEntity saveInDb(BudgetResponseDto budgetResponseDto) {
        BudgetEntity budgetEntity = modelMapper.map(budgetResponseDto, BudgetEntity.class);
        budgetEntity.setUser(userRepository.findByEmail(budgetResponseDto.getUser())
                .orElseThrow(() -> new UserNotFoundException(budgetResponseDto.getUser())));

        budgetRepository.save(budgetEntity);

        return budgetEntity;
    }
}
