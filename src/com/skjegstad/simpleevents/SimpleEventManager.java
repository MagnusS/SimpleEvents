/*
 
SimpleEventManager.java
 
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


import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Class for managing event pipes.
 * 
 * @author Magnus Skjegstad
 */
public class SimpleEventManager {
    
    private ConcurrentHashMap<String,SimpleEventPipe> eventPipes = new ConcurrentHashMap();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    
    protected String defaultContext = "DEFAULT_CONTEXT";  
    protected String instanceName = "EVENTMGR-" + Thread.currentThread().getId();

    /**
     * Create new instance of SimpleEventManager.
     * 
     */
    public SimpleEventManager() {
    }

    /**
     * Create new instance of SimpleEventManager with the given name. The name is
     * used mainly for debugging purposes.
     * 
     * @param name unique name of the event manager.
     */
    public SimpleEventManager(String name) {
        instanceName = name;
    }

    /**
     * Returns the name of this instance set by the constructor. 
     * 
     * @return 
     */
    public String getInstanceName() {
        return instanceName;
    }
        
            
    /**
     * 
     * Get an instance of the named event pipe. Named pipes can be used by
     * to get access to the same event pipe from different parts of the code.
     * 
     * @param name name of event pipe.
     * @return new or existing event pipe with the given name.
     */
    public SimpleEventPipe getPipe(String name) {
        SimpleEventPipe pipe = eventPipes.get(name);
        if (pipe == null) {
            eventPipes.putIfAbsent(name, new SimpleEventPipe(executorService));
            pipe = eventPipes.get(name);
        }
        
        return pipe;
    }
    
    /**
     * 
     * Returns the default event pipe.
     * 
     * @return default event pipe.
     */
    public SimpleEventPipe getDefaultPipe() {
        return getPipe(defaultContext);
    }
    
    /**
     * Create a new event pipe with a unique name.
     * 
     * @param owner name of the entity requesting the event pipe.
     * @return unique event pipe name.
     */
    public SimpleEventPipe getUniquePipe(String owner) {
        return getPipe(owner + "-" + UUID.randomUUID().toString());
    }
    
    /**
     * Shutdown all event pipes associated with this event manager. Running
     * event listeners will receive an interrupt.
     * 
     */
    public void shutdown() {
        executorService.shutdownNow();
        eventPipes = new ConcurrentHashMap();
    }
    
}
