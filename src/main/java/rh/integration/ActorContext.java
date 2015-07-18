package rh.integration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinGroup;
import akka.routing.RoundRobinPool;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActorContext {

    private static FieldDependencyResolver fieldDependencyResolver;
    private Map<Class, List<ActorRef>> register;
    private Map<Class, List<Object>> objectMap;
    private Map<Class, ActorRef> routers;
    private ActorSystem actorSystem;

    public static ActorContext scan(String basePackage, ActorSystem system) {

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Actor.class));

        Map<Class, List<ActorRef>> register = new HashMap<>();
        Map<Class, List<Object>> objectMap = new HashMap<>();
        Map<Class, ActorRef> routers = new HashMap<>();

        ActorContext context = new ActorContext();

        scanner.findCandidateComponents(basePackage).forEach(
                actor -> {
                    List<ActorRef> refs = new ArrayList<ActorRef>();
                    try {

                        Class<?> clazz = Class.forName(actor.getBeanClassName());

                        //TODO: InstanceHandler?
                        String[] names = clazz.getAnnotation(Actor.class).value();
                        List instances = new ArrayList(Math.max(1, names.length));


                        if (names.length == 1 && names[0].isEmpty()) {
                            Object instance = clazz.newInstance();
                            instances.add(instance);
                            TargetBinder targetBinder = TargetBinder.create(instance, context);
                            ActorRef ref = system.actorOf(Props.create(ProxyActor.class, targetBinder));
                            refs.add(ref);
                        } else {
                            for (String name : names) {
                                Object instance = clazz.newInstance();
                                instances.add(instance);
                                TargetBinder targetBinder = TargetBinder.create(instance, context);
                                ActorRef ref = system.actorOf(Props.create(ProxyActor.class, targetBinder), name);
                                refs.add(ref);
                            }

                            if (names.length > 1) {
                                List<String> paths = refs.stream().map(ref -> ref.path().toString()).collect(Collectors.toList());
                                ActorRef router = system.actorOf(new RoundRobinGroup(paths).props());
                                routers.put(clazz, router);
                            }
                        }

                        objectMap.put(clazz, instances);
                        register.put(clazz, refs);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

        context.register = register;
        context.objectMap = objectMap;
        context.routers = routers;
        context.actorSystem = system;

        fieldDependencyResolver = FieldDependencyResolver.create(context);

        register.forEach((clazz, references) -> {
            for (Field field : clazz.getDeclaredFields()) {
                for (Object instance : objectMap.get(clazz)) {
                    Object obj = fieldDependencyResolver.resolve(instance, field);
                    if (obj != FieldResolver.NON_RESOLVABLE) {
                        field.setAccessible(true);
                        try {
                            field.set(instance, obj);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        });

        return context;
    }

    public ActorRef getActor(Class clazz) {
        ActorRef router = routers.get(clazz);
        if (router != null) {
            return router;
        }
        List<ActorRef> actors = register.get(clazz);
        if (actors.size() > 1) {
            throw new RuntimeException("Ambiguous!!!!");
        }
        return actors.get(0);
    }

    public List<ActorRef> getActors(Class clazz) {
        return register.get(clazz);
    }

    ResponseHandler defaultResponseHandler = new DefaultResponseHandler();

    public ResponseHandler getDefaultResponseHandler() {
        return defaultResponseHandler;
    }

    public ActorRef getActorFor(Object instance) {
        int index = objectMap.get(instance.getClass()).indexOf(instance);
        return register.get(instance.getClass()).get(index);
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
