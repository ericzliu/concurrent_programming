package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import edu.rice.pcdp.PCDP;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 * <p>
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     * <p>
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        AtomicInteger counter = new AtomicInteger(1);
        PCDP.finish(() -> {
            SieveActorActor actor = new SieveActorActor(counter);
            for (int number = 3; number <= limit; number += 2) {
                actor.send(number);
            }
        });
        return counter.get();
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        private final AtomicInteger counter;
        private Integer factor = null;
        private SieveActorActor next = null;

        SieveActorActor(AtomicInteger counter) {
            this.counter = counter;
        }

        /**
         * Process a single message sent to this actor.
         * <p>
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            Integer number = (Integer) msg;
            if (this.factor == null) {
                this.factor = number;
                this.counter.addAndGet(1);
            }
            if (number % this.factor != 0) {
                if (this.next == null) {
                    this.next = new SieveActorActor(counter);
                }
                this.next.send(number);
            }
        }
    }
}
