package ec.com.sofka.gateway.dto;

import ec.com.sofka.utils.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private String id;
    private BigDecimal amount;
    private BigDecimal fee;
    private TransactionTypes transactionTypes;
    private LocalDateTime date;
    private String description;
    private String accountId;

    public TransactionDTO(BigDecimal amount, BigDecimal fee, TransactionTypes transactionTypes, LocalDateTime date, String description, String accountId) {
        this.amount = amount;
        this.fee = fee;
        this.transactionTypes = transactionTypes;
        this.date = date;
        this.description = description;
        this.accountId = accountId;
    }

    public TransactionDTO(String id, BigDecimal amount, BigDecimal fee, TransactionTypes transactionTypes, LocalDateTime date, String description, String accountId) {
        this.id = id;
        this.amount = amount;
        this.fee = fee;
        this.transactionTypes = transactionTypes;
        this.date = date;
        this.description = description;
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }


    public TransactionTypes getTransactionTypes() {
        return transactionTypes;
    }

    public String getAccountId() {
        return accountId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
