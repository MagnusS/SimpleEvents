/*
 
SimpleEventPipeTest.java
 
Copyright (C) 2011 Magnus Skjegstad

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
*/
package com.skjegstad.simpleevents;

import com.skjegstad.simpleevents.interfaces.SimpleEventListener;
import com.skjegstad.simpleevents.interfaces.SimpleEvent;
import com.skjegstad.simpleevents.interfaces.SimpleAsyncEventListener;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Executors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * Tests for SimpleEventPipe.
 * 
 * @author Magnus Skjegstad
 */
public class SimpleEventPipeTest {


    /**
     * Test of listen method, of class SimpleEventPipe.
     */
    @Test
    public void testListen() throws InterruptedException {
        final AtomicLong receivedEvents = new AtomicLong(0);
        System.out.println("listen");
        
        // Sync. event listener
        SimpleEventListener syncListen = new SimpleEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                receivedEvents.addAndGet(1);
            }
        };
        
        SimpleEventPipe instance = new SimpleEventPipe(Executors.newCachedThreadPool());
        instance.listen(syncListen);        
        assertEquals(receivedEvents.get(), 0);        
        instance.trigger(new SimpleEvent() {});
        assertEquals(receivedEvents.get(), 1);
        
        // Async. event listener
        SimpleEventListener asyncListen = new SimpleAsyncEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                receivedEvents.addAndGet(1);
            }
        };                
        instance.listen(asyncListen);
        instance.trigger(new SimpleEvent() {});
        Thread.sleep(500); // give async event time to run
        assertEquals(receivedEvents.get(), 3);
        

    }

    /**
     * Test of unlisten method, of class SimpleEventPipe.
     */
    @Test
    public void testUnlisten() {
        final AtomicLong receivedEvents = new AtomicLong(0);
        System.out.println("unisten");
        
        // Sync. event listener
        SimpleEventListener syncListen = new SimpleEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                receivedEvents.addAndGet(1);
            }
        };
        
        SimpleEventPipe instance = new SimpleEventPipe(Executors.newCachedThreadPool());
        instance.listen(syncListen);        
        assertEquals(receivedEvents.get(), 0);        
        instance.trigger(new SimpleEvent() {});
        assertEquals(receivedEvents.get(), 1);
        instance.unlisten(syncListen);
        instance.trigger(new SimpleEvent() {});
        assertEquals(receivedEvents.get(), 1);
    }

    /**
     * Test of trigger method, of class SimpleEventPipe.
     */
    @Test
    public void testTrigger() throws InterruptedException {
        final AtomicLong receivedEvents = new AtomicLong(0);
        System.out.println("trigger");
        
        class myEvent implements SimpleEvent {}        
        
        // Sync. event listener
        SimpleEventListener syncListen = new SimpleEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                if (event instanceof myEvent)
                    receivedEvents.addAndGet(1);
            }
        };
        
        SimpleEventPipe instance = new SimpleEventPipe(Executors.newCachedThreadPool());
        instance.listen(syncListen);        
        assertEquals(receivedEvents.get(), 0);        
        instance.trigger(new myEvent() {});
        assertEquals(receivedEvents.get(), 1);
        
        // Async. event listener
        SimpleEventListener asyncListen = new SimpleAsyncEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                if (event instanceof myEvent)
                    receivedEvents.addAndGet(1);
            }
        };                
        instance.listen(asyncListen);
        instance.trigger(new myEvent() {});
        Thread.sleep(500); // give async event time to run
        assertEquals(receivedEvents.get(), 3);
    }
    
    /**
     * Test events per second.
     * 
     */
    @Test
    public void testBenchmark() {
        long tests = 1000;
        final AtomicLong receivedEvents = new AtomicLong(0);
                
        System.out.println("benchmark");
        
        // Sync. event listener
        SimpleEventListener syncListen = new SimpleEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                receivedEvents.incrementAndGet();
            }
        };
        
        SimpleEventPipe instance = new SimpleEventPipe(Executors.newCachedThreadPool());
        instance.listen(syncListen);        
        
        SimpleEvent event = new SimpleEvent() {};
                
        long startTs = 0;
        long endTs = 0; 
        // Increase number of tests until it takes more than 1 second to run
        while (endTs - startTs < 1000) {
            receivedEvents.set(0);
            startTs = System.currentTimeMillis();            
            for (int i = 0; i < tests; i++) {
                instance.trigger(event);
            }        
            endTs = System.currentTimeMillis();
            if (endTs - startTs < 1000)
                tests = tests * 2;
        }        
        assertEquals(receivedEvents.get(), tests);
        
        System.out.println((tests / (double)((endTs - startTs)/1000.0)) + " sync. events per second (ran " + tests + " tests for " + ((endTs - startTs)/1000.0) + " seconds)");
    }
}
