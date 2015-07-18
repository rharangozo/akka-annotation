package rh.integration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldDependencyResolver {

    private List<FieldResolver> resolvers;

    public static FieldDependencyResolver create(ActorContext context) {
        FieldDependencyResolver resolver = new FieldDependencyResolver();
        resolver.resolvers = new ArrayList<>();

        ActorReferenceFieldResolver actorReferenceFieldResolver = new ActorReferenceFieldResolver();
        actorReferenceFieldResolver.setContext(context);

        SelfFieldResolver selfFieldResolver = new SelfFieldResolver();
        selfFieldResolver.setContext(context);

        ActorSystemResolver actorSystemResolver = new ActorSystemResolver();
        actorSystemResolver.setContext(context);

        resolver.resolvers.add(actorReferenceFieldResolver);
        resolver.resolvers.add(selfFieldResolver);
        resolver.resolvers.add(actorSystemResolver);

        return resolver;
    }

    public Object resolve(Object instance, Field field) {
        for(FieldResolver resolver : resolvers) {
            Object valueToInject = resolver.resolve(instance, field);
            if(valueToInject!=FieldResolver.NON_RESOLVABLE) {
                return valueToInject;
            }
        }
        return FieldResolver.NON_RESOLVABLE;
    }
}
