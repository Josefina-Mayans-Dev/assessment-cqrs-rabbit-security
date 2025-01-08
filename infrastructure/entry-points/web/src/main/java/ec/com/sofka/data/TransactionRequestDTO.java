package ec.com.sofka.data;

import ec.com.sofka.utils.enums.TransactionTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransactionRequestDTO {
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    private TransactionTypes transactionTypes;


    @NotBlank(message = "Account number cannot be blank")
    private String accountNumber;

    @NotBlank
    private String description;

    @NotBlank(message = "Customer id cannot be blank")
    private String customerId;

    public TransactionRequestDTO(BigDecimal amount, TransactionTypes transactionTypes, String accountNumber, String description, String customerId) {
        this.amount = amount;
        this.transactionTypes = transactionTypes;
        this.accountNumber = accountNumber;
        this.description = description;
        this.customerId = customerId;
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


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
