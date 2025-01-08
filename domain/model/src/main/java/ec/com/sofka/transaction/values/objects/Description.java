package ec.com.sofka.transaction.values.objects;

import ec.com.sofka.generics.interfaces.IValueObject;

public class Description implements IValueObject<String> {
    private final String value;

    private Description(final String value) {
        this.value = validate(value);
    }

    public static ec.com.sofka.transaction.values.objects.Description of(final String value) {
        return new ec.com.sofka.transaction.values.objects.Description(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    private String validate(final String value){
        if(value == null){
            throw new IllegalArgumentException("Description can't be null");
        }

        if(value.isBlank()){
            throw new IllegalArgumentException("Description can't be empty");
        }

        if(value.length() < 5){
            throw new IllegalArgumentException("Description must have at least 5 characters");
        }

        return value;
    }
}

