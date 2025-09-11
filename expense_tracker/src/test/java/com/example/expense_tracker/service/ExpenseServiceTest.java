package com.example.expense_tracker.service;

import com.example.expense_tracker.exception.CategoryNotFoundException;
import com.example.expense_tracker.exception.CurrencyNotFoundException;
import com.example.expense_tracker.exception.ExpenseNotFoundException;
import com.example.expense_tracker.exception.UserNotFoundException;
import com.example.expense_tracker.model.dto.CreateExpenseDto;
import com.example.expense_tracker.model.dto.ExpenseResponseDto;
import com.example.expense_tracker.model.dto.UpdateExpenseDto;
import com.example.expense_tracker.model.entity.*;
import com.example.expense_tracker.model.enums.CategoryEnum;
import com.example.expense_tracker.model.enums.UserRoleEnum;
import com.example.expense_tracker.repository.*;
import com.example.expense_tracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ModelMapper modelMapper;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseServiceImpl(modelMapper, userRepository, 
                categoryRepository, expenseRepository, currencyRepository);
    }

    @Test
    void createExpense_Should_CreateExpense_When_ValidData() {
        String email = "test@gmail.com";
        CreateExpenseDto dto = createValidCreateExpenseDto();
        
        UserEntity user = createUser(email);
        CategoryEntity category = createCategory();
        CurrencyEntity currency = createCurrency("BGN");
        ExpenseEntity mappedExpense = createExpenseEntity(user, category, currency);
        ExpenseEntity savedExpense = createExpenseEntity(user, category, currency);
        savedExpense.setId(1L);

        when(modelMapper.map(dto, ExpenseEntity.class)).thenReturn(mappedExpense);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findByCategoryEnum(CategoryEnum.FOOD)).thenReturn(Optional.of(category));
        when(currencyRepository.findByCode("BGN")).thenReturn(Optional.of(currency));
        when(expenseRepository.save(mappedExpense)).thenReturn(savedExpense);
        when(modelMapper.map(any(ExpenseEntity.class), eq(ExpenseResponseDto.class))).thenReturn(new ExpenseResponseDto());

        ExpenseResponseDto result = expenseService.createExpense(dto, email);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(categoryRepository, times(1)).findByCategoryEnum(CategoryEnum.FOOD);
        verify(currencyRepository, times(1)).findByCode("BGN");
        verify(expenseRepository, times(1)).save(mappedExpense);
    }

    @Test
    void createExpense_Should_ThrowException_When_UserNotFound() {
        String email = "nonexistent@gmail.com";
        CreateExpenseDto dto = createValidCreateExpenseDto();
        ExpenseEntity mappedExpense = new ExpenseEntity();

        when(modelMapper.map(dto, ExpenseEntity.class)).thenReturn(mappedExpense);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, 
            () -> expenseService.createExpense(dto, email));
        
        verify(userRepository, times(1)).findByEmail(email);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_Should_ThrowException_When_CategoryNotFound() {
        String email = "test@gmail.com";
        CreateExpenseDto dto = createValidCreateExpenseDto();
        dto.setCategory("INVALID_CATEGORY");
        
        UserEntity user = createUser(email);
        ExpenseEntity mappedExpense = new ExpenseEntity();

        when(modelMapper.map(dto, ExpenseEntity.class)).thenReturn(mappedExpense);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(CategoryNotFoundException.class,
            () -> expenseService.createExpense(dto, email));
        
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void createExpense_Should_ThrowException_When_CurrencyNotFound() {
        String email = "test@gmail.com";
        CreateExpenseDto dto = createValidCreateExpenseDto();
        dto.setCurrencyCode("INVALID");
        
        UserEntity user = createUser(email);
        CategoryEntity category = createCategory();
        ExpenseEntity mappedExpense = new ExpenseEntity();

        when(modelMapper.map(dto, ExpenseEntity.class)).thenReturn(mappedExpense);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findByCategoryEnum(CategoryEnum.FOOD)).thenReturn(Optional.of(category));
        when(currencyRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(CurrencyNotFoundException.class,
            () -> expenseService.createExpense(dto, email));
        
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void getUserExpenses_Should_ReturnListOfExpenses_When_UserHasExpenses() {
        String email = "test@gmail.com";
        UserEntity user = createUser(email);
        CategoryEntity category = createCategory();
        CurrencyEntity currency = createCurrency("BGN");
        ExpenseEntity expense1 = createExpenseEntity(user, category, currency);
        ExpenseEntity expense2 = createExpenseEntity(user, category, currency);
        
        when(expenseRepository.findAllByUserEmail(email)).thenReturn(List.of(expense1, expense2));
        when(modelMapper.map(any(ExpenseEntity.class), eq(ExpenseResponseDto.class)))
            .thenReturn(new ExpenseResponseDto());

        List<ExpenseResponseDto> result = expenseService.getUserExpenses(email);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findAllByUserEmail(email);
    }

    @Test
    void getExpenseById_Should_ReturnExpense_When_ExpenseExists() {
        Long expenseId = 1L;
        UserEntity user = createUser("test@gmail.com");
        CategoryEntity category = createCategory();
        CurrencyEntity currency = createCurrency("BGN");
        ExpenseEntity expense = createExpenseEntity(user, category, currency);
        expense.setId(expenseId);
        
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(modelMapper.map(expense, ExpenseResponseDto.class)).thenReturn(new ExpenseResponseDto());

        ExpenseResponseDto result = expenseService.getExpenseById(expenseId);

        assertNotNull(result);
        verify(expenseRepository, times(1)).findById(expenseId);
    }

    @Test
    void getExpenseById_Should_ThrowException_When_ExpenseNotFound() {
        Long expenseId = 999L;
        
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, 
            () -> expenseService.getExpenseById(expenseId));
        
        verify(expenseRepository, times(1)).findById(expenseId);
    }

    @Test
    void updateExpense_Should_UpdateExpense_When_ExpenseExists() {
        Long expenseId = 1L;
        UpdateExpenseDto dto = createValidUpdateExpenseDto();
        
        UserEntity user = createUser("test@gmail.com");
        CategoryEntity oldCategory = createCategory();
        CategoryEntity newCategory = createCategory();
        newCategory.setCategoryEnum(CategoryEnum.BILLS);
        CurrencyEntity oldCurrency = createCurrency("BGN");
        CurrencyEntity newCurrency = createCurrency("EUR");
        
        ExpenseEntity existingExpense = createExpenseEntity(user, oldCategory, oldCurrency);
        existingExpense.setId(expenseId);
        
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(categoryRepository.findByCategoryEnum(CategoryEnum.BILLS)).thenReturn(Optional.of(newCategory));
        when(currencyRepository.findByCode("EUR")).thenReturn(Optional.of(newCurrency));
        when(expenseRepository.save(existingExpense)).thenReturn(existingExpense);
        when(modelMapper.map(existingExpense, ExpenseResponseDto.class)).thenReturn(new ExpenseResponseDto());

        ExpenseResponseDto result = expenseService.updateExpense(expenseId, dto);

        assertNotNull(result);
        assertEquals(dto.getAmount(), existingExpense.getAmount());
        assertEquals(dto.getDescription(), existingExpense.getDescription());
        assertEquals(dto.getDate(), existingExpense.getExpenseDate());
        
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).save(existingExpense);
        verify(categoryRepository, times(1)).findByCategoryEnum(CategoryEnum.BILLS);
        verify(currencyRepository, times(1)).findByCode("EUR");
    }

    @Test
    void updateExpense_Should_ThrowException_When_ExpenseNotFound() {
        Long expenseId = 999L;
        UpdateExpenseDto dto = createValidUpdateExpenseDto();
        
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, 
            () -> expenseService.updateExpense(expenseId, dto));
        
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void deleteExpense_Should_DeleteExpense_When_ExpenseExists() {
        Long expenseId = 1L;
        UserEntity user = createUser("test@gmail.com");
        CategoryEntity category = createCategory();
        CurrencyEntity currency = createCurrency("BGN");
        ExpenseEntity expense = createExpenseEntity(user, category, currency);
        expense.setId(expenseId);
        
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        doNothing().when(expenseRepository).deleteById(expenseId);

        expenseService.deleteExpense(expenseId);

        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, times(1)).deleteById(expenseId);
    }

    @Test
    void deleteExpense_Should_ThrowException_When_ExpenseNotFound() {
        Long expenseId = 999L;
        
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, 
            () -> expenseService.deleteExpense(expenseId));
        
        verify(expenseRepository, times(1)).findById(expenseId);
        verify(expenseRepository, never()).deleteById(any());
    }


    private CreateExpenseDto createValidCreateExpenseDto() {
        CreateExpenseDto dto = new CreateExpenseDto();
        dto.setDescription("Test Expense");
        dto.setAmount(new BigDecimal("100.50"));
        dto.setCategory("FOOD");
        dto.setExpenseDate(LocalDate.now());
        dto.setCurrencyCode("BGN");
        return dto;
    }

    private UpdateExpenseDto createValidUpdateExpenseDto() {
        UpdateExpenseDto dto = new UpdateExpenseDto();
        dto.setDescription("Updated Expense");
        dto.setAmount(new BigDecimal("75.25"));
        dto.setCategory("BILLS");
        dto.setDate(LocalDate.now());
        dto.setCurrency("EUR");
        return dto;
    }

    private UserEntity createUser(String email) {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleName(UserRoleEnum.USER);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(email);
        user.setRoles(List.of(userRole));
        return user;
    }

    private CategoryEntity createCategory() {
        CategoryEntity category = new CategoryEntity();
        category.setId(1L);
        category.setCategoryEnum(CategoryEnum.FOOD);
        return category;
    }

    private CurrencyEntity createCurrency(String code) {
        CurrencyEntity currency = new CurrencyEntity();
        currency.setId(1L);
        currency.setCode(code);
        return currency;
    }

    private ExpenseEntity createExpenseEntity(UserEntity user, CategoryEntity category, CurrencyEntity currency) {
        ExpenseEntity expense = new ExpenseEntity();
        expense.setDescription("Test Expense");
        expense.setAmount(new BigDecimal("100.50"));
        expense.setExpenseDate(LocalDate.now());
        expense.setUser(user);
        expense.setCategory(category);
        expense.setCurrency(currency);
        return expense;
    }
}