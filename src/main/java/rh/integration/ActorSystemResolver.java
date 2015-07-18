package rh.integration;

import akka.actor.ActorSystem;

import javax.annotation.Resource;
import java.lang.reflect.Field;

public class ActorSystemResolver implements FieldResolver {

    private ActorContext context;

    @Override
    public Object resolve(Object instance, Field field) {
        if(field.isAnnotationPresent(Resource.class) && field.getType().equals(ActorSystem.class)){
            return context.getActorSystem();
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
