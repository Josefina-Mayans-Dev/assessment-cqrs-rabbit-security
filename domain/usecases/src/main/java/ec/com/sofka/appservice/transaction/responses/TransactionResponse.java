package ec.com.sofka.appservice.transaction.responses;

import ec.com.sofka.utils.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private final String actionId;
    private final BigDecimal amount;
    private final BigDecimal fee;
    private final TransactionTypes transactionTypes;
    private final LocalDateTime date;
    private final String accountId;
    private final String customerId;
    private final String description;

    public TransactionResponse(String actionId, BigDecimal amount, BigDecimal fee, TransactionTypes transactionTypes, LocalDateTime date, String accountId, String customerId, String description) {
        this.actionId = actionId;
        this.amount = amount;
        this.fee = fee;
        this.transactionTypes = transactionTypes;
        this.date = date;
        this.accountId = accountId;
        this.customerId = customerId;
        this.description = description;
    }

    public String getActionId() {
        return actionId;
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

    public LocalDateTime getDate() {
        return date;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDescription() {
        return description;
    }
}