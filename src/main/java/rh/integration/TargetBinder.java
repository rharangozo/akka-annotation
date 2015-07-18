package rh.integration;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class TargetBinder {

    private Object target;
    private Map<Class, Method> typeMethodMapping = new HashMap<>();
    private ActorContext context;

    public static TargetBinder create(Object target, ActorContext context) {
        TargetBinder targetBinder = new TargetBinder();
        targetBinder.target = target;
        targetBinder.context = context;
        targetBinder.init();
        return targetBinder;
    }

    public void invoke(Object message, UntypedActor untypedActor) {
        Method method = typeMethodMapping.get(message.getClass());
        if(method != null) {
            try {
                Object ret = method.invoke(target, message);
                if(ret != null) {
                    ActorRef sender = untypedActor.getSender();
                    context.getDefaultResponseHandler().handle(untypedActor.getSelf(), sender, ret);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void init() {
        for(Method method : target.getClass().getMethods()) {
            if(method.isAnnotationPresent(OnMessage.class)) {
                Parameter[] parameters = method.getParameters();
                if(parameters.length!=1) {
                    throw new RuntimeException("Illegal number of parameters!");
                }
                typeMethodMapping.put(parameters[0].getType(), method);
            }
        }
    }
}
