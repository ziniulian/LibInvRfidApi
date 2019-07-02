package com.invengo.lib.event;

import java.util.ArrayList;

public class EventListener<TEventListener> {
	
	// Event Listener List
	protected ArrayList<TEventListener> listenerList = new ArrayList<TEventListener>();
	
	// Add Event Listener
	public synchronized void addEventListener(TEventListener listener) {
		this.listenerList.add(listener);
	}
	
	// Remove Event Listener
	public synchronized void removeEventListener(TEventListener listener) {
		this.listenerList.remove(listener);
	}
	
	// Remove Last Event Listener
	public synchronized void removeLastEventListener() {
		int index = this.listenerList.size() - 1;
		
		if (index >= 0)
			this.listenerList.remove(index);
	}
	
	// Remove All Event Listener
	public synchronized void removeAll() {
		this.listenerList.clear();
	}
	
	// Get Listener Count
	public synchronized int getCount() {
		return this.listenerList.size();
	}
	
	public synchronized TEventListener getLastListener() {
		if (listenerList.size() < 1) return null;
		return listenerList.get(listenerList.size() - 1);
	}	
}
