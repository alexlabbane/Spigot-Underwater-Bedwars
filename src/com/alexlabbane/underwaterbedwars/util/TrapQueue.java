package com.alexlabbane.underwaterbedwars.util;

public class TrapQueue {
	// Constants
	public static final int MAX_NUM_TRAPS = 3;
	
	// Data members
	private TeamTrap[] queue;
	private int size;
	private int front;
	private int back;
	
	public TrapQueue() {
		this.queue = new TeamTrap[MAX_NUM_TRAPS];
		this.size = 0;
		this.front = 0;
		this.back = 0;
	}
	
	public int size() { return this.size; }
	public boolean full() { return this.size >= MAX_NUM_TRAPS; }
	
	public TeamTrap front() {
		if(this.size == 0)
			return null;
		
		return this.queue[this.front];
	}
	
	public TeamTrap pop() {
		if(this.size == 0)
			throw new IndexOutOfBoundsException("Empty queue");
		
		TeamTrap front = this.front();
		this.front = (this.front + 1) % MAX_NUM_TRAPS;
		this.size--;
		
		return front;
	}
	
	public void push(TeamTrap newTrap) {
		if(this.size >= MAX_NUM_TRAPS)
			return;
		
		this.queue[this.back] = newTrap;
		this.back = (this.back + 1) % MAX_NUM_TRAPS;
		this.size++;
	}
	
	/**
	 * Get the element in the i-th position in the queue
	 * @param i
	 * @return
	 */
	public TeamTrap getAtPosition(int i) {
		if(i >= this.size)
			throw new IndexOutOfBoundsException("Position " + i + " is out of bounds in the queue!");
		
		return this.queue[(this.front + i) % MAX_NUM_TRAPS];
	}
}
