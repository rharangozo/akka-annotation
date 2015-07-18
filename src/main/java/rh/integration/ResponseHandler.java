package rh.integration;

import akka.actor.ActorRef;

public interface ResponseHandler {
    public void handle(ActorRef thisActor, ActorRef sender, Object response);
}
