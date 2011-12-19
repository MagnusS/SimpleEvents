/*
 
SimpleEventListener.java
 
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
package com.skjegstad.simpleevents.interfaces;

import com.skjegstad.simpleevents.interfaces.SimpleEvent;

/**
 *
 * Custom event listeners must extend this interface. By default event listeners are
 * run synchronously. For asynchronous event listeners, see @see SimpleAsyncEventListener.
 * 
 * @author Magnus Skjegstad
 */
public interface SimpleEventListener {
    public void receiveEvent(SimpleEvent event);
}
