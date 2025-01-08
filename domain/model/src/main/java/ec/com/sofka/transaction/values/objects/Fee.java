package ec.com.sofka.transaction.values.objects;

import ec.com.sofka.generics.interfaces.IValueObject;

import java.math.BigDecimal;

public class Fee implements IValueObject<BigDecimal> {
    private final BigDecimal value;

    private Fee(final BigDecimal value) {
        this.value = validate(value);
    }

    public static ec.com.sofka.transaction.values.objects.Fee of(final BigDecimal value) {
        return new ec.com.sofka.transaction.values.objects.Fee(value);
    }

    @Override
    public BigDecimal getValue() {
        return this.value;
    }

    //hello validations
    private BigDecimal validate(final BigDecimal value){
        if(value == null){
            throw new IllegalArgumentException("Fee cannot be null");
        }

        if(value.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Fee must be equal or greater than 0");
        }


        return value;
    }
}
