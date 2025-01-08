package ec.com.sofka.transaction.values.objects;

import ec.com.sofka.generics.interfaces.IValueObject;
import ec.com.sofka.utils.enums.TransactionTypes;

public class TransactionType implements IValueObject<TransactionTypes> {
    private final TransactionTypes value;

    private TransactionType(TransactionTypes value) {
        this.value = validate(value);
    }

    public static TransactionType of(TransactionTypes value) {
        return new TransactionType(value);
    }

    @Override
    public TransactionTypes getValue() {
        return this.value;
    }

    private TransactionTypes validate(final TransactionTypes value) {
        if (value == null) {
            throw new IllegalArgumentException("The transaction type can't be null");
        }

        return value;
    }

}
