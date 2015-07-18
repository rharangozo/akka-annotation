package rh.example;

import akka.actor.ActorRef;
import rh.integration.Actor;
import rh.integration.Reference;
import rh.integration.OnMessage;
import rh.integration.Self;

@Actor("AnotherActor")
public class AnotherActor {

    public enum MSG {
        OK;
    }

    @Reference(SimpleActor.class)
    private ActorRef simpleActor;

    @Self
    private ActorRef self;

    @OnMessage
    public void msgHandler(String msg) {
        System.out.println("I was told : " + msg);
        simpleActor.tell("message", self);
    }

    @OnMessage
    public void acknowledge(MSG msg) {
        System.out.println("ACKNOWLEDGE");
    }
}
