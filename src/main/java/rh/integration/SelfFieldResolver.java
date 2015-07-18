package rh.integration;

import java.lang.reflect.Field;

public class SelfFieldResolver implements FieldResolver {

    private ActorContext context;

    @Override
    public Object resolve(Object instance, Field field) {
        if(field.isAnnotationPresent(Self.class)){
            return context.getActorFor(instance);
        }
        return FieldResolver.NON_RESOLVABLE;
    }

    public ActorContext getContext() {
        return context;
    }

    public void setContext(ActorContext context) {
        this.context = context;
    }
}
