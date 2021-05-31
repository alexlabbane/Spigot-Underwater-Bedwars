package com.alexlabbane.underwaterbedwars.util;

/**
 * Custom queue used for traps bought by a BedwarsTeam
 * @author Alex Labbane
 *
 */
public class TrapQueue {
	// Data members
	private TeamTrap[] queue;
	private int size;
	private int front;
	private int back;
	
	/************* Static members *************/

	public static final int MAX_NUM_TRAPS = 3;
	
	/**
	 * Create a new TrapQueue
	 */
	public TrapQueue() {
		this.queue = new TeamTrap[MAX_NUM_TRAPS];
		this.size = 0;
		this.front = 0;
		this.back = 0;
	}
	
	/************* Getters/Setters *************/
	
	public int size() { return this.size; }
	public boolean full() { return this.size >= MAX_NUM_TRAPS; }
	
	/**
	 * Get the trap at the front of the queue
	 * @return	the trap at the front of the queue; null if there is none
	 */
	public TeamTrap front() {
		if(this.size == 0)
			return null;
		
		return this.queue[this.front];
	}
	
	/**
	 * Remove the trap at the front of the queue. Throws an
	 * IndexOutOfBoundsException if queue is empty
	 * @return	the trap that was removed
	 */
	public TeamTrap pop() {
		if(this.size == 0)
			throw new IndexOutOfBoundsException("Empty queue");
		
		TeamTrap front = this.front();
		this.front = (this.front + 1) % MAX_NUM_TRAPS;
		this.size--;
		
		return front;
	}
	
	/**
	 * Push a new trap to the back of the queue. Does nothing
	 * if the queue is full
	 * @param newTrap	the trap to push
	 */
	public void push(TeamTrap newTrap) {
		if(this.size >= MAX_NUM_TRAPS)
			return;
		
		this.queue[this.back] = newTrap;
		this.back = (this.back + 1) % MAX_NUM_TRAPS;
		this.size++;
	}
	
	/**
	 * Get the element in the i-th position in the queue. Throws
	 * IndexOutOfBoundsException if there is no such trap
	 * @param i		the index to get
	 * @return		the trap at index i
	 */
	public TeamTrap getAtPosition(int i) {
		if(i >= this.size)
			throw new IndexOutOfBoundsException("Position " + i + " is out of bounds in the queue!");
		
		return this.queue[(this.front + i) % MAX_NUM_TRAPS];
	}
}
