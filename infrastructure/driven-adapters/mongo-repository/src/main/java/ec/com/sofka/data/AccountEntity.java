package ec.com.sofka.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Document(collection = "bank_account")
public class AccountEntity {
    @Id
    private String id;

    @Field("account_id")
    private String accountId;

    @Field("account_number")
    private String accountNumber;

    @Field("account_holder")
    private String accountHolder;

    @Field("global_balance")
    private BigDecimal balance;

    public AccountEntity(){

    }


    public AccountEntity(String accountId, BigDecimal balance, String accountHolder, String accountNumber) {
       this.accountId = accountId;
        this.balance = balance;
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
    }

    public AccountEntity(String id, String accountId, String accountNumber, BigDecimal balance, String accountHolder) {
        this.id = id;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountHolder = accountHolder;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
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

}

