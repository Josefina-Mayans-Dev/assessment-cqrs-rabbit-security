package ec.com.sofka.data;

import ec.com.sofka.utils.enums.TransactionTypes;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "transaction")
public class TransactionEntity {

    @Id
    private String id;

//    @Enumerated(EnumType.STRING)
    @Field("transaction_type")
    private TransactionTypes transactionTypes;

    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime date;
    private String description;

    @Field("bank_account_id")
    private String accountId;

    public TransactionEntity() {}

    public TransactionEntity(BigDecimal amount, BigDecimal fee, TransactionTypes transactionTypes, LocalDateTime date, String description, String accountId) {
        this.amount = amount;
        this.fee = fee;
        this.transactionTypes = transactionTypes;
        this.date = date;
        this.description = description;
        this.accountId = accountId;
    }

    public TransactionEntity(String id, TransactionTypes transactionTypes, BigDecimal amount, BigDecimal fee, LocalDateTime date,
                       String description,
                       String accountId) {
        this.id = id;
        this.transactionTypes = transactionTypes;
        this.amount = amount;
        this.fee = fee;
        this.date = date;
        this.description = description;
        this.accountId = accountId;
    }


    public String getId() {
        return id;
    }

    public TransactionTypes getTransactionTypes() {
        return transactionTypes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTransactionTypes(TransactionTypes transactionTypes) {
        this.transactionTypes = transactionTypes;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}