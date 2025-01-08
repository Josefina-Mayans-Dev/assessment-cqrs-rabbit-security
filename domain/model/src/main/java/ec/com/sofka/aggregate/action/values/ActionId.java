package ec.com.sofka.aggregate.action.values;

import ec.com.sofka.generics.utils.Identity;

public class ActionId extends Identity {
    public ActionId(){
        super();
    }

    private ActionId(final String id){
        super(id);
    }

    public static ActionId of(final String id){
        return new ActionId(id);
    }
}
