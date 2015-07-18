package rh.example.multiple;

import akka.actor.ActorRef;
import rh.integration.Actor;
import rh.integration.OnMessage;
import rh.integration.Self;

@Actor({"Actor1", "Actor2"})
public class TwoActors {

    @Self
    private ActorRef self; //TODO: inject actor 1 ALWAYS!

    @OnMessage
    public void printWhoAreYou(String msg) {
        System.out.println("I am " + self.path() + " Message : " + msg);
    }
}
