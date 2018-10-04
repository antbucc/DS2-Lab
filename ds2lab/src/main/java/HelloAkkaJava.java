import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Inbox;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HelloAkkaJava {
    public static class Greet implements Serializable {}
    public static class WhoToGreet implements Serializable {
        public final String who;
        public WhoToGreet(String who) {
            this.who = who;
        }
    }
    public static class Greeting implements Serializable {
        public final String message;
        public Greeting(String message) {
            this.message = message;
        }
    }

    public static class Greeter extends UntypedActor {
        String greeting = "";

        public void onReceive(Object message) {
            if (message instanceof WhoToGreet)
                greeting = "hello, " + ((WhoToGreet) message).who;

            else if (message instanceof Greet)
                // Send the current greeting back to the sender
                getSender().tell(new Greeting(greeting), getSelf());

            else unhandled(message);
        }
    }

    public static void main(String[] args) {
        try {
            // Create the 'helloakka' actor system
            final ActorSystem system = ActorSystem.create("helloakka");

            // Create the 'greeter' actor
            final ActorRef greeter = system.actorOf(Props.create(Greeter.class).withDispatcher("akka.actor.my-pinned-dispatcher"), "greeter");

            // Create the "actor-in-a-box"
            final Inbox inbox = Inbox.create(system);

            // Tell the 'greeter' to change its 'greeting' message
            greeter.tell(new WhoToGreet("akka"), ActorRef.noSender());

            // Ask the 'greeter for the latest 'greeting'
            // Reply should go to the "actor-in-a-box"
            inbox.send(greeter, new Greet());

            // Wait 5 seconds for the reply with the 'greeting' message
            final Greeting greeting1 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
            System.out.println("Greeting: " + greeting1.message);

            // Change the greeting and ask for it again
            greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());
            inbox.send(greeter, new Greet());
            final Greeting greeting2 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
            System.out.println("Greeting: " + greeting2.message);

            // after zero seconds, send a Greet message every second to the greeter with a sender of the GreetPrinter
            final ActorRef greetPrinter = system.actorOf(Props.create(GreetPrinter.class));
            system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), greeter, new Greet(), system.dispatcher(), greetPrinter);
        } catch (TimeoutException ex) {
            System.out.println("Got a timeout waiting for reply from an actor");
            ex.printStackTrace();
        }
    }

    public static class GreetPrinter extends UntypedActor {
        public void onReceive(Object message) {
            if (message instanceof Greeting)
                System.out.println(((Greeting) message).message);
        }
    }
}
