package ec.com.sofka.gateway.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class AccountDTO {
    private String id;
    private String accountNumber;
    private String accountHolder;
    private BigDecimal balance;
    private List<TransactionDTO> transactions = new ArrayList<>();

    public AccountDTO(String id) {
        this.id = id;
    }

    public AccountDTO(BigDecimal balance, String accountHolder, String accountNumber) {
        this.balance = balance;
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
    }

    public AccountDTO(String id,String accountNumber,BigDecimal balance, String accountHolder) {
        this.accountNumber = accountNumber;
        this.id = id;
        this.balance = balance;
        this.accountHolder = accountHolder;
    }

    public String getId() {
        return id;
    }



    public String getAccountNumber() {
        return accountNumber;
    }


    public String getAccountHolder() {
        return accountHolder;
    }


    public BigDecimal getBalance() {
        return balance;
    }


    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

}
