package ec.com.sofka.utils.enums;

import java.math.BigDecimal;

public enum TransactionTypes {
    DEPOSIT_ATM(BigDecimal.valueOf(2.0)),
    DEPOSIT_OTHER_ACCOUNT(BigDecimal.valueOf(1.5)),
    WITHDRAW_ATM(BigDecimal.valueOf(1.0)),
    ONLINE_PURCHASE(BigDecimal.valueOf(5.0)),
    BRANCH_DEPOSIT(BigDecimal.valueOf(0.0)),
    ONSITE_CARD_PURCHASE(BigDecimal.valueOf(0.0));

    private final BigDecimal fee;

    TransactionTypes(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public static TransactionTypes fromString(String type) {
        for (TransactionTypes t : TransactionTypes.values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type: " + type);
    }
}
