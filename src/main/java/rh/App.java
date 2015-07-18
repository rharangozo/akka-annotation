package rh;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import rh.example.AnotherActor;
import rh.example.SimpleActor;
import rh.example.multiple.TwoActors;
import rh.example.routing.MasterActor;
import rh.integration.ActorContext;

import java.util.List;

public class App {
    public static void main(String[] args) {

        ActorContext context = ActorContext.scan("rh.example", ActorSystem.create());
//        ActorRef actor = context.getActor(SimpleActor.class);
        //actor.tell("hello world!", null);
//        actor.tell(4, null);

//        ActorRef anotherActor = context.getActor(AnotherActor.class);
//        anotherActor.tell("hello", null);

//        List<ActorRef> multiple = context.getActors(TwoActors.class);
//        multiple.forEach(actor->actor.tell("Who?",null));

//        ActorRef router = context.getActor(TwoActors.class);
//        for(int i = 0; i < 10; ++i) {
//            router.tell("Call " + i, null);
//        }

        ActorRef master = context.getActor(MasterActor.class);
        master.tell("Hello beautiful world!\n" +
                "And some further text in a new line\n" +
                "This is the last line", null);
    }
}
