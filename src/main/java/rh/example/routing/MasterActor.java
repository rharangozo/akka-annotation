package rh.example.routing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import rh.integration.Actor;
import rh.integration.OnMessage;
import rh.integration.Reference;
import rh.integration.Self;
import scala.concurrent.Future;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Actor("Master")
public class MasterActor {

    @Self
    private ActorRef self;

    @Reference(SlaveActor.class)
    private ActorRef slave;

    @Resource
    private ActorSystem system;

    @OnMessage
    public void printAnalysisOf(String text) {
        System.out.println("Text : " + text);

        String[] lines = text.split("\\n");
        System.out.println("Number of lines : " + lines.length);

        List<Future<Object>> futures = new ArrayList<>(lines.length);
        for(String line : lines) {
            futures.add(Patterns.ask(slave, line, 1000));
        }

        Future<Iterable<Object>> sequence = Futures.sequence(futures, system.dispatcher());
        Future<Integer> result = sequence.map(new Mapper<Iterable<Object>, Integer>() {
            @Override
            public Integer apply(Iterable<Object> list) {
                int i = 0;
                for (Object result : list) {
                    i += (Integer) result;
                }
                return i;
            }
        }, system.dispatcher());

        result.onComplete(new OnComplete<Integer>() {
            @Override
            public void onComplete(Throwable throwable, Integer integer) throws Throwable {
                System.out.println("Number of words : " + integer);
            }
        }, system.dispatcher());
    }

}
