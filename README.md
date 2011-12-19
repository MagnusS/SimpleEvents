What is this
------------

A simple event library written in Java.


Getting started
---------------

1) Create an instance of the event manager. This could be declared globally or static in a larger project.

```java

SimpleEventManager eventsMgr = new SimpleEventManager();

```

2) Get an event pipe from the event manager. 

```java

SimpleEventPipe pipe = eventMgr.getPipe("my-pipe");

```

3) Register event listeners. Event listeners can be called synchronously in the same thread as the event was triggered or 
asynchronously in a separate thread. To create an asynchronous listener, replace SimpleEventListener with AsyncSimpleEventListener. 

```java

pipe.listen(new SimpleEventListener() {
	public void receiveEvent(SimpleEvent e) {
		System.out.println("In event listener!");
	}
});

```

4) Trigger events. Events are received by all listeners on this event pipe.

```java

pipe.trigger(new SimpleEvent {});

```

5) (optional) Create custom events by extending SimpleEvent. To separate between different event types, use instanceof in the listener or use different pipes.


That's all.



Magnus Skjegstad, 2011
magnus@skjegstad.com

