package rh.integration;

import akka.actor.UntypedActor;

public class ProxyActor extends UntypedActor {

    private TargetBinder targetBinder;

    public ProxyActor(TargetBinder targetBinder) {
        this.targetBinder = targetBinder;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        try {
            targetBinder.invoke(message, this);
        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
