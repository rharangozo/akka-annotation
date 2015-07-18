package rh.integration;

import java.lang.reflect.Field;

public class ActorReferenceFieldResolver implements FieldResolver {

    private ActorContext context;

    @Override
    public Object resolve(Object instance, Field field) {
        if(field.isAnnotationPresent(Reference.class)){
            Class clazzToInject = field.getAnnotation(Reference.class).value();
            return context.getActor(clazzToInject);
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
