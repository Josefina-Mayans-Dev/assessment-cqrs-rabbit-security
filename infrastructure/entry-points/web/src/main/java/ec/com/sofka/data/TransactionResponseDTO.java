package ec.com.sofka.data;

import ec.com.sofka.account.Account;
import ec.com.sofka.utils.enums.TransactionTypes;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {
    private String actionId;

    @NotNull
    private BigDecimal fee;

    private BigDecimal amount;

    private TransactionTypes transactionTypes;

    private LocalDateTime date;

    private String description;

    private String customerId;


    public TransactionResponseDTO(String actionId, BigDecimal fee, BigDecimal amount, TransactionTypes transactionTypes, LocalDateTime date, String description, String customerId) {
        this.actionId = actionId;
        this.fee = fee;
        this.amount = amount;
        this.transactionTypes = transactionTypes;
        this.date = date;
        this.description = description;
        this.customerId = customerId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }


    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionTypes getTransactionTypes() {
        return transactionTypes;
    }

    public void setTransactionTypes(TransactionTypes transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
