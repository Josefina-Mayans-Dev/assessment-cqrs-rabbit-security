package ec.com.sofka.aggregate.events;

import ec.com.sofka.account.Account;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.utils.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRegistered extends DomainEvent {
    private String id;
    private TransactionTypes transactionTypes;
    private BigDecimal amount;
    private BigDecimal fee;
    private LocalDateTime date;
    private String description;
    private String accountId;

    public TransactionRegistered(String id, TransactionTypes transactionTypes, BigDecimal amount, BigDecimal fee,
                                 LocalDateTime date, String description, String accountId) {
        super(EventsEnum.TRANSACTION_REGISTERED.name());
        this.id = id;
        this.transactionTypes = transactionTypes;
        this.amount = amount;
        this.fee = fee;
        this.date = date;
        this.description = description;
        this.accountId = accountId;
    }

    public TransactionRegistered() {
        super(EventsEnum.TRANSACTION_REGISTERED.name());

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
}
