package rh.integration;

import akka.actor.ActorRef;

public class DefaultResponseHandler implements ResponseHandler {
    @Override
    public void handle(ActorRef thisActor, ActorRef sender, Object response) {
        sender.tell(response, thisActor);
    }
}
