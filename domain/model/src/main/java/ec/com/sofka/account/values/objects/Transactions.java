package ec.com.sofka.account.values.objects;

import ec.com.sofka.generics.interfaces.IValueObject;
import ec.com.sofka.transaction.Transaction;

import java.math.BigDecimal;
import java.util.List;

//4. Creation of a value object: In Value objects is where validations must go.
//Objects values garantees the integrity of the data
public class Transactions implements IValueObject<List<Transaction>> {
    private final List<Transaction> value;

    private Transactions(final List<Transaction> value ) {
        this.value = validate(value);
    }

    public static Transactions of(final List<Transaction> value) {
        return new Transactions(value);
    }

    @Override
    public List<Transaction> getValue() {
        return this.value;
    }

    //hello validations
    private List<Transaction> validate(final List<Transaction> value){
        if(value == null){
            throw new IllegalArgumentException("The balance can't be null");
        }


        return value;
    }
}
