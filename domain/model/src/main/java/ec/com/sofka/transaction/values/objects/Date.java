package ec.com.sofka.transaction.values.objects;

import ec.com.sofka.generics.interfaces.IValueObject;

import java.time.LocalDateTime;

public class Date implements IValueObject<LocalDateTime> {
    private final LocalDateTime value;

    private Date(final LocalDateTime value) {
        this.value = validate(value);
    }

    public static ec.com.sofka.transaction.values.objects.Date of(final LocalDateTime value) {
        return new ec.com.sofka.transaction.values.objects.Date(value);
    }

    @Override
    public LocalDateTime getValue() {
        return this.value;
    }

    //hello validations
    private LocalDateTime validate(final LocalDateTime value){
        if(value == null){
            throw new IllegalArgumentException("Timestamp can't be null");
        }

        if(value.compareTo(LocalDateTime.now()) < 0){
            throw new IllegalArgumentException("Timestamp cannot be in the past");
        }


        return value;
    }
}


