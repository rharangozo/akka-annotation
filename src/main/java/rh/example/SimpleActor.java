package rh.example;

import rh.integration.Actor;
import rh.integration.OnMessage;

@Actor("SimpleActor")
public class SimpleActor {

    @OnMessage
    public AnotherActor.MSG messageHandler(String msg) {
        System.out.println("I have got the message : " + msg);
        return AnotherActor.MSG.OK;
    }

    @OnMessage
    public void numberMessageHandler(Integer num) {
        System.out.println("Number : " + num.toString());
    }
}
