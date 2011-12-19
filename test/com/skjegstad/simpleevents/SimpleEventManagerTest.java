/*
 
SimpleEventManagerTest.java
 
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

import com.skjegstad.simpleevents.interfaces.SimpleAsyncEventListener;
import com.skjegstad.simpleevents.interfaces.SimpleEvent;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * Tests for SimpleEventManager
 * 
 * @author Magnus Skjegstad
 */
public class SimpleEventManagerTest {
    
    /**
     * Test of getInstanceName method, of class SimpleEventManager.
     */
    @Test
    public void testGetInstanceName() {
        System.out.println("getInstanceName");
        SimpleEventManager instance = new SimpleEventManager();
        assertEquals(instance.instanceName, instance.getInstanceName());
        instance = new SimpleEventManager("testInstance");
        assertEquals("testInstance", instance.getInstanceName());
        assertEquals("testInstance", instance.instanceName);
    }

    /**
     * Test of getPipe method, of class SimpleEventManager.
     */
    @Test
    public void testGetPipe() {
        System.out.println("getPipe");
        String name = "testPipe";
        SimpleEventManager instance = new SimpleEventManager();
        SimpleEventPipe expResult = instance.getPipe(name);
        SimpleEventPipe result = instance.getPipe(name);
        assertEquals(expResult, result);
        assertNotNull(result);
        assertTrue(result instanceof SimpleEventPipe);
    }

    /**
     * Test of getDefaultPipe method, of class SimpleEventManager.
     */
    @Test
    public void testGetDefaultPipe() {
        System.out.println("getDefaultPipe");
        SimpleEventManager instance = new SimpleEventManager();
        SimpleEventPipe expResult = instance.getPipe(instance.defaultContext);
        SimpleEventPipe result = instance.getDefaultPipe();
        assertEquals(expResult, result);
        assertNotNull(result);
        assertTrue(result instanceof SimpleEventPipe);
    }

    /**
     * Test of getUniquePipe method, of class SimpleEventManager.
     */
    @Test
    public void testGetUniquePipe() {
        System.out.println("getUniquePipe");
        String owner = "tester";
        SimpleEventManager instance = new SimpleEventManager();
        
        SimpleEventPipe result = instance.getUniquePipe(owner);
        SimpleEventPipe expResult = instance.getUniquePipe(owner);
        assertNotSame(expResult, result);
        
        assertNotNull(expResult);
        assertTrue(expResult instanceof SimpleEventPipe);
        assertNotNull(result);
        assertTrue(result instanceof SimpleEventPipe);
        
    }

    /**
     * Test of shutdown method, of class SimpleEventManager.
     */
    @Test
    public void testShutdown() throws InterruptedException {
        final AtomicLong interruptedListeners = new AtomicLong(0);
        
        System.out.println("shutdown");
        SimpleEventManager instance = new SimpleEventManager();
        
        // Test that asynchronous listeners are interrupted on shutdown
        
        // Create sleeping async listener
        SimpleAsyncEventListener asyncListener = new SimpleAsyncEventListener() {
            @Override
            public void receiveEvent(SimpleEvent event) {
                while (!Thread.interrupted()) {
                    try {
                        // do stuff
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
                interruptedListeners.incrementAndGet();
            }
        };
        
        instance.getDefaultPipe().listen(asyncListener);
        
        int tests = 10;
        for (int i = 0; i < tests; i++)
            instance.getDefaultPipe().trigger(new SimpleEvent() {});
        assertEquals(interruptedListeners.get(), 0);
        
        instance.shutdown();
        
        Thread.sleep(500);
        assertEquals(tests, interruptedListeners.get());
        
    }
}
