package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.exception.BudgetNotFoundException;
import com.example.expense_tracker.exception.CurrencyNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.CurrencyRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.BudgetService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    private static final Logger logger = LoggerFactory.getLogger(BudgetServiceImpl.class);

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;
    private final ModelMapper modelMapper;

    public BudgetServiceImpl(BudgetRepository budgetRepository, UserRepository userRepository, CurrencyRepository currencyRepository, ModelMapper modelMapper) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.currencyRepository = currencyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BudgetResponseDto createBudget(CreateBudgetDto createBudgetDto, String email) {
        BudgetEntity budgetEntity = modelMapper.map(createBudgetDto, BudgetEntity.class);
        budgetEntity.setUser(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email)));
        budgetEntity.setCurrency(currencyRepository.findByCode(createBudgetDto.getCurrencyCode())
                .orElseThrow(() -> new CurrencyNotFoundException(createBudgetDto.getCurrencyCode())));

        budgetRepository.save(budgetEntity);

        return buildBudgetResponseDto(budgetEntity);
    }

    @Override
    public BudgetResponseDto updateBudget(Long budgetId, UpdateBudgetDto updateBudgetDto) {
        BudgetEntity entity = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        if (updateBudgetDto.getBudgetLimit() != null) {
            entity.setBudgetLimit(updateBudgetDto.getBudgetLimit());
        }
        if (updateBudgetDto.getMonth() != null) {
            entity.setMonth(updateBudgetDto.getMonth());
        }
        if (updateBudgetDto.getCurrency() != null) {
            entity.setCurrency(currencyRepository.findByCode(updateBudgetDto.getCurrency())
                    .orElseThrow(() -> new CurrencyNotFoundException(updateBudgetDto.getCurrency())));
        }

        budgetRepository.save(entity);

        return buildBudgetResponseDto(entity);
    }

    @Override
    public void deleteBudget(Long id) {
        BudgetEntity entity = budgetRepository.findById(id).orElseThrow(()
                -> new BudgetNotFoundException(id));

        budgetRepository.deleteById(id);
    }

    @Override
    public List<BudgetResponseDto> getUserBudgets(String email) {
        logger.info("Fetching budgets for user: {}", email);

        return budgetRepository.findByUserEmail(email)
                .stream()
                .map(this::buildBudgetResponseDto)
                .toList();
    }

    @Override
    public BudgetResponseDto getBudgetById(Long id) {
        BudgetEntity budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));
        return buildBudgetResponseDto(budget);
    }

    private BudgetResponseDto buildBudgetResponseDto(BudgetEntity entity) {
        BudgetResponseDto dto = modelMapper.map(entity, BudgetResponseDto.class);
        dto.setUser(entity.getUser().getEmail());
        dto.setCurrencyCode(entity.getCurrency().getCode());

        BigDecimal remaining = entity.getBudgetLimit().subtract(entity.getSpent());
        dto.setRemaining(remaining);

        if (entity.getBudgetLimit().compareTo(BigDecimal.ZERO) > 0) {
            double percentage = entity.getSpent()
                    .divide(entity.getBudgetLimit(), 2, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
            dto.setSpentPercentage(percentage);
        } else {
            dto.setSpentPercentage(0.0);
        }

        return dto;
    }

    @Override
    public boolean isOwner(Long budgetId, String email) {
        return budgetRepository.findByIdAndUserEmail(budgetId, email).isPresent();
    }

}
