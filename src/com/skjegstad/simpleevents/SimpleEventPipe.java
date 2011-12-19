/*
 
SimpleEventPipe.java
 
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 *
 * The simple event pipe. 
 * 
 * @author Magnus Skjegstad
 */
public class SimpleEventPipe {
    private final List<SimpleEventListener> asyncEventListeners = Collections.synchronizedList(new ArrayList());
    private final List<SimpleEventListener> syncEventListeners = Collections.synchronizedList(new ArrayList());
    private ExecutorService executorService;

    /**
     * Create new SimpleEventPipe using the given ExecutorService. 
     * 
     * @param executorService ExecutorService used to start asynchronous events.
     */
    public SimpleEventPipe(ExecutorService executorService) {
        this.executorService = executorService;
    }
   
                
    /**
     * Add a listener to this event pipe. 
     * <p>
     * If the listener extends
     * @link SimpleEventListener it is executed synchronously when an
     * event is triggered. For asynchronous listeners, extend
     * @link SimpleAsyncEventListener instead.
     * </p>
     * 
     * @param listener event pipe listener 
     */
    public void listen(SimpleEventListener listener) {                                
        if (listener instanceof SimpleAsyncEventListener)
            asyncEventListeners.add(listener);
        else
            syncEventListeners.add(listener);
    }
    
    /**
     * Remove the given listener from the event pipe. The listener
     * will no longer be triggered when new events are received.
     * 
     * @param listener listener to remove.
     * @return  true if the listener was succesfuly removed, otherwise false
     */
    public boolean unlisten(SimpleEventListener listener) {        
        if (listener instanceof SimpleAsyncEventListener)
            return asyncEventListeners.remove(listener);
        else
            return syncEventListeners.remove(listener);        
    }
    
    /**
     * Trigger a new event and run all registered event listeners. The event
     * must extend @link SimpleEvent.
     * 
     * @param event event to trigger.
     */
    public void trigger(final SimpleEvent event) {
        // Check async first;
        if (asyncEventListeners != null) {
            synchronized(asyncEventListeners) { // as the listeners are spawned in new threads, we can lock here without risking deadlocks
                for (final SimpleEventListener listener : asyncEventListeners) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            listener.receiveEvent(event);
                        }
                    });
                }                    
            }
        }
        
        // Then run synchronous
        if (syncEventListeners != null) {
            for (int i = 0; i < syncEventListeners.size(); i++) {
                try {
                    SimpleEventListener listener = syncEventListeners.get(i);
                    listener.receiveEvent(event);
                } catch (IndexOutOfBoundsException ex ) { // this could happen if an event is deleted while we are in the loop.
                    continue;
                }
            }
        }
    }

}
