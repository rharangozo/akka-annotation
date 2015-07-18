package rh.example.routing;

import akka.actor.ActorRef;
import rh.integration.Actor;
import rh.integration.OnMessage;
import rh.integration.Self;

@Actor({"worker1", "worker2"})
public class SlaveActor {

    @Self
    private ActorRef self;

    @OnMessage
    public int lengthOf(String line) {
        System.out.println("Actor [" + self.path() + "] is counting the words in the line...");
        return line.trim().split(" ").length;
    }
}
