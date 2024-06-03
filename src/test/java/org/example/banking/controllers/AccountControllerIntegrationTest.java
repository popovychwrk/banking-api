package org.example.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.banking.TestDataUtil;
import org.example.banking.domain.dto.AccountDto;
import org.example.banking.domain.entities.AccountEntity;
import org.example.banking.services.AccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    private final AccountService accountService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public AccountControllerIntegrationTest(MockMvc mockMvc, AccountService accountService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.accountService = accountService;
    }

    @Test
    public void testThatCreateAccountSuccessfullyReturnsHttp201Created() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        testAccountEntityA.setId(null);
        String accountJson = objectMapper.writeValueAsString(testAccountEntityA);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testThatCreateAccountSuccessfullyReturnsSavedAccount() throws Exception {
        AccountEntity testAccount = TestDataUtil.createTestAccountEntityA();
        testAccount.setId(null);
        String accountJson = objectMapper.writeValueAsString(testAccount);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value(testAccount.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(testAccount.getBalance().doubleValue()));
    }

    @Test
    public void testThatFindAllAccountsReturnsHttp200() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/accounts"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatFindAllAccountsReturnsPageAllAccounts() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity testAccountEntityB = TestDataUtil.createTestAccountEntityB();
        AccountEntity testAccountEntityC = TestDataUtil.createTestAccountEntityC();
        accountService.createAccount(testAccountEntityA);
        accountService.createAccount(testAccountEntityB);
        accountService.createAccount(testAccountEntityC);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/accounts"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(3)));
    }

    @Test
    public void testThatFindAccountByIdReturnsHttp200WhenAccountExists() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(testAccountEntityA);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/accounts/" + testAccountEntityA.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatFindAccountByIdReturnsHttp404WhenAccountDoesNotExist() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/accounts/"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatFindAccountByIdReturnsAccount() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(testAccountEntityA);
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/accounts/" + testAccountEntityA.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(testAccountEntityA.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(testAccountEntityA.getBalance().doubleValue()));
    }

    @Test
    public void testThatFullUpdateAccountSuccessfullyReturnsHttp200Ok() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(testAccountEntityA);
        testAccountEntityA.setBalance(testAccountEntityA.getBalance().add(testAccountEntityA.getBalance()));
        String accountJson = objectMapper.writeValueAsString(testAccountEntityA);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/accounts/" + testAccountEntityA.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatFullUpdateAccountReturnsHttp404WhenNoAuthorExists() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        testAccountEntityA.setBalance(testAccountEntityA.getBalance().add(testAccountEntityA.getBalance()));
        String accountJson = objectMapper.writeValueAsString(testAccountEntityA);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/accounts/" + testAccountEntityA.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatFullUpdateUpdatesExistingAccount() throws Exception {
        AccountEntity savedAuthor = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(savedAuthor);

        AccountEntity authorDto = TestDataUtil.createTestAccountEntityB();
        authorDto.setId(savedAuthor.getId());

        String accountJson = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/accounts/" + savedAuthor.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(authorDto.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(authorDto.getBalance().doubleValue()));
    }

    @Test
    public void testThatPartialUpdateAccountSuccessfullyReturnsHttp200Ok() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity savedAccount = accountService.createAccount(testAccountEntityA);

        AccountDto testAccountDtoA = TestDataUtil.createTestAccountDtoA();
        testAccountDtoA.setAccountNumber("123123123");
        String accountJson = objectMapper.writeValueAsString(testAccountDtoA);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/accounts/" + savedAccount.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatPartialUpdateAccountReturnsHttp404WhenNoAccountExists() throws Exception {
        AccountDto testAccountDtoA = TestDataUtil.createTestAccountDtoA();
        testAccountDtoA.setAccountNumber("123123123");
        String accountJson = objectMapper.writeValueAsString(testAccountDtoA);
        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/accounts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatPartialUpdateAccountReturnsUpdatedAccount() throws Exception {
        AccountEntity savedAccount = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(savedAccount);

        AccountDto testAccountDto = TestDataUtil.createTestAccountDtoA();
        testAccountDto.setAccountNumber("123123123");
        String accountJson = objectMapper.writeValueAsString(testAccountDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/api/accounts/" + savedAccount.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(accountJson))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedAccount.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(testAccountDto.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(testAccountDto.getBalance().doubleValue()));
    }

    @Test
    public void testThatDeleteAccountSuccessfullyReturnsHttp204NoContent() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity savedAccount = accountService.createAccount(testAccountEntityA);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/accounts/" + savedAccount.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testThatDeleteAccountReturnsHttp404WhenNoAccountExists() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatDepositSuccessfullyReturnsHttp200() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity savedAccount = accountService.createAccount(testAccountEntityA);
        BigDecimal amount = new BigDecimal("100.00");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts/deposit/" + savedAccount.getId())
                                .param("amount", amount.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatDepositReturnsUpdatedAccount() throws Exception {
        AccountEntity savedAccount = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(savedAccount);
        BigDecimal amount = new BigDecimal("100.00");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts/deposit/" + savedAccount.getId())
                                .param("amount", amount.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedAccount.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(savedAccount.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(savedAccount.getBalance().add(amount).doubleValue()));
    }

    @Test
    public void testThatWithdrawSuccessfullyReturnsHttp200() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity savedAccount = accountService.createAccount(testAccountEntityA);
        BigDecimal amount = new BigDecimal("100.00");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts/withdraw/" + savedAccount.getId())
                                .param("amount", amount.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatWithdrawReturnsUpdatedAccount() throws Exception {
        AccountEntity savedAccount = TestDataUtil.createTestAccountEntityA();
        accountService.createAccount(savedAccount);
        BigDecimal amount = new BigDecimal("100.00");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts/withdraw/" + savedAccount.getId())
                                .param("amount", amount.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedAccount.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(savedAccount.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(savedAccount.getBalance().subtract(amount).doubleValue()));
    }

    @Test
    public void testThatTransferSuccessfullyReturnsHttp200() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity testAccountEntityB = TestDataUtil.createTestAccountEntityB();
        AccountEntity savedAccountA = accountService.createAccount(testAccountEntityA);
        AccountEntity savedAccountB = accountService.createAccount(testAccountEntityB);
        BigDecimal amount = new BigDecimal("100.00");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts/transfer/")
                                .param("fromAccountId", savedAccountA.getId().toString())
                                .param("toAccountId", savedAccountB.getId().toString())
                                .param("amount", amount.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatTransferReturnsUpdatedAccounts() throws Exception {
        AccountEntity testAccountEntityA = TestDataUtil.createTestAccountEntityA();
        AccountEntity testAccountEntityB = TestDataUtil.createTestAccountEntityB();
        AccountEntity savedAccountA = accountService.createAccount(testAccountEntityA);
        AccountEntity savedAccountB = accountService.createAccount(testAccountEntityB);
        BigDecimal amount = new BigDecimal("100.00");
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/accounts/transfer/")
                                .param("fromAccountId", savedAccountA.getId().toString())
                                .param("toAccountId", savedAccountB.getId().toString())
                                .param("amount", amount.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedAccountA.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber")
                        .value(savedAccountA.getAccountNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance")
                        .value(savedAccountA.getBalance().subtract(amount).doubleValue()));

    }
}
