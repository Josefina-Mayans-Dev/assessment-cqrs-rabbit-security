package ec.com.sofka.transaction.values;

import ec.com.sofka.generics.utils.Identity;

public class TransactionId extends Identity {
    public TransactionId() {
        super();
    }

    //wtf why private??
    private TransactionId(final String id) {
        super(id);
    }


    //who tf are you?? I am the method to access/make instances the id with the private modifier :D
    public static ec.com.sofka.transaction.values.TransactionId of(final String id) {
        return new ec.com.sofka.transaction.values.TransactionId(id);
    }
}

