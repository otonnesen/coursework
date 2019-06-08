/* CSC 225 Assignment 2
 * Oliver Tonnesen
 * V00885732
 * June 28, 2018
 */

/* NiceSimulator.java
   CSC 225 - Summer 2018

   An empty shell of the operations needed by the NiceSimulator
   data structure.

   B. Bird - 06/18/2018
*/


import java.io.*;
import java.util.Scanner;

public class NiceSimulator{

	public static final int SIMULATE_IDLE = -2;
	public static final int SIMULATE_NONE_FINISHED = -1;

	private class Task {
		public int priority;
		public int remaining;
		public int taskID;
		public int heap_index;

		public Task(int p, int r, int t, int i) {
			this.priority = p;
			this.remaining = r;
			this.taskID = t;
			this.heap_index = i;
		}
	}

	private Task[] heap; // Heap structure
	private Task[] tasks;// taskID-indexed array for quick lookup of task data
	private int size; // Stores the index into which the next task must be put

	/* Constructor(maxTasks)
	   Instantiate the data structure with the provided maximum
	   number of tasks. No more than maxTasks different tasks will
	   be simultaneously added to the simulator, and additionally
	   you may assume that all task IDs will be in the range
	     0, 1, ..., maxTasks - 1
	*/
	public NiceSimulator(int maxTasks){
		this.heap = new Task[maxTasks];
		this.tasks = new Task[maxTasks];
		this.size = 0;
	}

	/* taskValid(taskID)
	   Given a task ID, return true if the ID is currently
	   in use by a valid task (i.e. a task with at least 1
	   unit of time remaining) and false otherwise.

	   Note that you should include logic to check whether
	   the ID is outside the valid range 0, 1, ..., maxTasks - 1
	   of task indices.
	*/
	public boolean taskValid(int taskID){
		if (taskID >= this.heap.length) {
			return false;
		}
		return this.tasks[taskID] != null;
	}

	/* getPriority(taskID)
	   Return the current priority value for the provided
	   task ID. You may assume that the task ID provided
	   is valid.
	*/
	public int getPriority(int taskID){
		return this.tasks[taskID].priority;
	}

	/* getRemaining(taskID)
	   Given a task ID, return the number of timesteps
	   remaining before the task completes. You may assume
	   that the task ID provided is valid.
	*/
	public int getRemaining(int taskID){
		return this.tasks[taskID].remaining;
	}


	/* add(taskID, time_required)
	   Add a task with the provided task ID and time requirement
	   to the system. You may assume that the provided task ID is in
	   the correct range and is not a currently-active task.
	   The new task will be assigned nice level 0.
	*/
	public void add(int taskID, int time_required) {
		Task t = new Task(0, time_required, taskID, this.size);
		this.tasks[taskID] = this.heap[this.size] = t;
		bubbleUp(this.size++);
	}


	/* kill(taskID)
	   Delete the task with the provided task ID from the system.
	   You may assume that the provided task ID is in the correct
	   range and is a currently-active task.
	*/
	public void kill(int taskID) {
		int heap_index = this.tasks[taskID].heap_index;
		this.swap(taskID, this.heap[--this.size].taskID);
		this.tasks[taskID] = null;
		this.heap[this.size] = null;

		bubbleDown(heap_index);
	}

	/*
	 * swap consists only of assignment statements,
	 * and so runs in O(1) time.
	 */
	private void swap(int id1, int id2) {

		int index = this.tasks[id1].heap_index;
		Task task = this.heap[index];

		this.heap[index] = this.heap[this.tasks[id2].heap_index];
		this.heap[this.tasks[id2].heap_index] = task;

		this.tasks[id1].heap_index = this.tasks[id2].heap_index;
		this.tasks[id2].heap_index = index;
	}

	/*
	 * The loop in bubbleUp runs at most log_2(n) times;
	 * the contents of the loop consist only of comparisons,
	 * assignments, and swap -- which runs in O(1) time -- so
	 * bubbleUp runs in O(log_2(n)) time.
	 */
	private void bubbleUp(int heap_index)  {
		Task child = this.heap[heap_index];
		heap_index = (heap_index-1)/2;
		Task parent = this.heap[heap_index];
		while (child.priority <= parent.priority) {
			if (child.taskID == parent.taskID) {
				return;
			}
			if (child.priority == parent.priority && child.taskID > parent.taskID) {
				return;
			}
			swap(child.taskID, parent.taskID);
			child = this.heap[heap_index];
			heap_index = (heap_index-1)/2;
			parent = this.heap[heap_index];
		}
	}

	/*
	 * minChild consists only of comparisons and
	 * so runs in O(1) time.
	 */
	private int minChild(int heap_index) {
		if (2*(heap_index+1)-1 > this.heap.length-1) {
			return -1;
		} else if (2*(heap_index+1) > this.heap.length-1) {
			if (this.heap[2*(heap_index+1)-1] == null) {
				return -1;
			} else {
				return 2*(heap_index+1)-1;
			}
		} else {
			if (this.heap[2*(heap_index+1)-1] == null) {
				return -1;
			} else if (this.heap[2*(heap_index+1)] == null) {
				return 2*(heap_index+1)-1;
			} else {
				if (this.heap[2*(heap_index+1)-1].priority == this.heap[2*(heap_index+1)].priority) {
					return this.heap[2*(heap_index+1)-1].taskID < this.heap[2*(heap_index+1)].taskID ?
						2*(heap_index+1)-1 : 2*(heap_index+1);
				}
				return this.heap[2*(heap_index+1)-1].priority < this.heap[2*(heap_index+1)].priority ?
					2*(heap_index+1)-1 : 2*(heap_index+1);
			}
		}
	}

	/*
	 * The loop in bubbleDown runs at most log_2(n) times;
	 * the contents of the loop consist only of comparisons,
	 * assignments, swap, and minChild -- both of which run in O(1) time -- so
	 * bubbleDown runs in O(log_2(n)) time.
	 */
	private void bubbleDown(int heap_index) {
		Task parent = this.heap[heap_index];
		heap_index = this.minChild(heap_index);
		if (heap_index == -1) {
			return;
		}
		Task child = this.heap[heap_index];
		while (parent.priority >= child.priority) {
			if (parent.priority == child.priority && parent.taskID < child.taskID) {
				return;
			}
			swap(parent.taskID, child.taskID);

			parent = this.heap[heap_index];
			heap_index = this.minChild(heap_index);
			if (heap_index == -1) {
				return;
			}
			child = this.heap[heap_index];
		}
	}

	/* renice(taskID, new_priority)
	   Change the priority of the the provided task ID to the new priority
       value provided. The change must take effect at the next simulate() step.
	   You may assume that the provided task ID is in the correct
	   range and is a currently-active task.
	*/
	public void renice(int taskID, int new_priority){
		int heap_index = this.tasks[taskID].heap_index;

		this.tasks[taskID].priority = new_priority;
		this.heap[heap_index].priority = new_priority;

		if (this.heap[heap_index].priority == this.heap[(heap_index-1)/2].priority) {
			if (this.heap[heap_index].taskID < this.heap[(heap_index-1)/2].taskID) {
				bubbleUp(heap_index);
			} else {
				bubbleDown(heap_index);
			}
		} else if (this.heap[heap_index].priority < this.heap[(heap_index-1)/2].priority) {
			bubbleUp(heap_index);
		} else {
			bubbleDown(heap_index);
		}
	}


	/* simulate()
	   Run one step of the simulation:
		 - If no tasks are left in the system, the CPU is idle, so return
		   the value SIMULATE_IDLE.
		 - Identify the next task to run based on the criteria given in the
		   specification (tasks with the lowest priority value are ranked first,
		   and if multiple tasks have the lowest priority value, choose the
		   task with the lowest task ID).
		 - Subtract one from the chosen task's time requirement (since it is
		   being run for one step). If the task now requires 0 units of time,
		   it has finished, so remove it from the system and return its task ID.
		 - If the task did not finish, return SIMULATE_NONE_FINISHED.
	*/
	public int simulate(){
		if (this.heap[0] == null) {
			return SIMULATE_IDLE;
		} else if (--this.heap[0].remaining == 0) {
			int id = this.heap[0].taskID;
			this.kill(id);
			return id;
		} else {
			return SIMULATE_NONE_FINISHED;
		}
	}
}
