package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.BudgetNotFoundException;
import com.example.expense_tracker.exception.CurrencyNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.BudgetResponseDto;
import com.example.expense_tracker.model.dto.CreateBudgetDto;
import com.example.expense_tracker.model.dto.UpdateBudgetDto;
import com.example.expense_tracker.model.entity.BudgetEntity;
import com.example.expense_tracker.model.entity.CurrencyEntity;
import com.example.expense_tracker.model.entity.UserEntity;
import com.example.expense_tracker.model.entity.UserRoleEntity;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.repository.BudgetRepository;
import com.example.expense_tracker.repository.CurrencyRepository;
import com.example.expense_tracker.repository.UserRepository;
import com.example.expense_tracker.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ModelMapper modelMapper;

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetServiceImpl(budgetRepository, userRepository,
                currencyRepository, modelMapper);
    }

    @Test
    void getUserBudgets_Should_ReturnListOfBudgets_When_UserExists() {
        String email = "test@gmail.com";
        UserEntity user = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), user, "2025-08");
        
        when(budgetRepository.findByUserEmail(email)).thenReturn(List.of(budget));
        when(modelMapper.map(any(BudgetEntity.class), eq(BudgetResponseDto.class)))
            .thenReturn(new BudgetResponseDto());

        List<BudgetResponseDto> result = budgetService.getUserBudgets(email);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(budgetRepository, times(1)).findByUserEmail(email);
    }

    @Test
    void getBudgetById_Should_ReturnBudget_When_BudgetExists() {
        Long budgetId = 1L;
        UserEntity user = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), user, "2025-08");
        
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(modelMapper.map(any(BudgetEntity.class), eq(BudgetResponseDto.class)))
            .thenReturn(new BudgetResponseDto());

        BudgetResponseDto result = budgetService.getBudgetById(budgetId);

        assertNotNull(result);
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void getBudgetById_Should_ThrowException_When_BudgetNotFound() {
        Long budgetId = 999L;
        
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        assertThrows(BudgetNotFoundException.class, 
            () -> budgetService.getBudgetById(budgetId));
        
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void createBudget_Should_CreateBudget_When_ValidData() {
        String email = "test@gmail.com";
        CreateBudgetDto dto = new CreateBudgetDto();
        dto.setCurrencyCode("BGN");

        UserEntity user = createUser();
        CurrencyEntity currency = createCurrency("BGN");
        BudgetEntity savedBudget = createBudget(BigDecimal.valueOf(1000), user, "2025-08");
        
        when(modelMapper.map(any(CreateBudgetDto.class), eq(BudgetEntity.class)))
            .thenReturn(savedBudget);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(currencyRepository.findByCode("BGN")).thenReturn(Optional.of(currency));
        when(modelMapper.map(any(BudgetEntity.class), eq(BudgetResponseDto.class)))
            .thenReturn(new BudgetResponseDto());

        BudgetResponseDto result = budgetService.createBudget(dto, email);

        assertNotNull(result);

        verify(userRepository, times(1)).findByEmail(email);
        verify(currencyRepository, times(1)).findByCode("BGN");
        verify(budgetRepository, times(1)).save(any(BudgetEntity.class));
    }

    @Test
    void createBudget_Should_ThrowException_When_UserNotFound() {
        String email = "nonexistent@gmail.com";
        CreateBudgetDto dto = new CreateBudgetDto();
        dto.setCurrencyCode("BGN");
        
        when(modelMapper.map(any(CreateBudgetDto.class), eq(BudgetEntity.class)))
            .thenReturn(new BudgetEntity());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, 
            () -> budgetService.createBudget(dto, email));
        
        verify(userRepository, times(1)).findByEmail(email);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void createBudget_Should_ThrowException_When_CurrencyNotFound() {
        String email = "test@gmail.com";
        CreateBudgetDto dto = new CreateBudgetDto();
        dto.setCurrencyCode("INVALID");
        
        UserEntity user = createUser();
        
        when(modelMapper.map(any(CreateBudgetDto.class), eq(BudgetEntity.class)))
            .thenReturn(new BudgetEntity());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(currencyRepository.findByCode(dto.getCurrencyCode())).thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class, 
            () -> budgetService.createBudget(dto, email));
        
        verify(currencyRepository, times(1)).findByCode("INVALID");
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void updateBudget_Should_UpdateBudget_When_BudgetExists() {
        Long budgetId = 1L;
        UpdateBudgetDto dto = new UpdateBudgetDto();
        dto.setBudgetLimit(BigDecimal.valueOf(1500));

        UserEntity user = createUser();
        BudgetEntity existingBudget = createBudget(BigDecimal.valueOf(1000), user, "2025-08");
        
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(existingBudget));
        when(modelMapper.map(any(BudgetEntity.class), eq(BudgetResponseDto.class)))
            .thenReturn(new BudgetResponseDto());

        BudgetResponseDto result = budgetService.updateBudget(budgetId, dto);

        assertNotNull(result);
        assertEquals(dto.getBudgetLimit(), existingBudget.getBudgetLimit());

        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).save(existingBudget);
    }

    @Test
    void deleteBudget_Should_DeleteBudget_When_BudgetExists() {
        Long budgetId = 1L;
        UserEntity user = createUser();
        BudgetEntity budget = createBudget(BigDecimal.valueOf(1000), user, "2025-08");
        
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        doNothing().when(budgetRepository).deleteById(budgetId);

        budgetService.deleteBudget(budgetId);

        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).deleteById(budgetId);
    }

    private BudgetEntity createBudget(BigDecimal limit, UserEntity owner, String month) {
        BudgetEntity entity = new BudgetEntity();
        entity.setId(1L);
        entity.setBudgetLimit(limit);
        entity.setUser(owner);
        entity.setMonth(month);
        entity.setSpent(BigDecimal.ZERO);
        entity.setCurrency(createCurrency("BGN"));

        return entity;
    }

    private UserEntity createUser() {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName(UserRoleEnum.USER);

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstname("Test");
        entity.setLastname("Testov");
        entity.setEmail("test@gmail.com");
        entity.setRoles(List.of(userRole));

        return entity;
    }

    private CurrencyEntity createCurrency(String code) {
        CurrencyEntity currency = new CurrencyEntity();
        currency.setId(1L);
        currency.setCode(code);

        return currency;
    }
}