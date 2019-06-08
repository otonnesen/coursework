/* Nice.java
   CSC 225 - Summer 2018

   Some starter code for programming assignment 2, which reads
   the input data and calls the relevant operations of the
   NiceSimulator.

   B. Bird - 06/18/2018
*/


import java.io.*;
import java.util.LinkedList;

/* You shouldn't need to understand or modify any of the code below.
   However, you can modify it (or completely rewrite it) if you want, 
   as long as its outward behavior is the same when you hand it in.
*/

public class Nice{

	private static abstract class NiceOp{
		public void apply(NiceSimulator sim){
		}
		public int getTimestep(){ return timestep; }
		protected int timestep = -1;
	}
	
	private static class NiceOpAdd extends NiceOp{
		public NiceOpAdd(int timestep, int task_id, int time_allocation){
			this.timestep = timestep;
			this.task_id = task_id;
			this.time_allocation = time_allocation;
		}
		public void apply(NiceSimulator sim){
			if (sim.taskValid(task_id)){
				System.err.printf("Error: Time %d: Attempt to add a duplicate task with ID %d\n", timestep, task_id);
				System.exit(0);
			}
			System.out.printf("Time %d: Adding task %d (time remaining %d)\n", timestep, task_id, time_allocation);
			sim.add(task_id, time_allocation);
		}
		private int task_id;
		private int time_allocation;
	}
	
	private static class NiceOpKill extends NiceOp{
		public NiceOpKill(int timestep, int task_id){
			this.timestep = timestep;
			this.task_id = task_id;
		}
		public void apply(NiceSimulator sim){
			if (!sim.taskValid(task_id)){
				System.err.printf("Error: Time %d: Attempt to kill an invalid task with ID %d\n", timestep, task_id);
				System.exit(0);
			}
			System.out.printf("Time %d: Killing task %d (priority %d, time remaining %d)\n", timestep, task_id, sim.getPriority(task_id), sim.getRemaining(task_id));
			sim.kill(task_id);
		}
		private int task_id;
	}
	
	private static class NiceOpRenice extends NiceOp{
		public NiceOpRenice(int timestep, int task_id, int new_nice){
			this.timestep = timestep;
			this.task_id = task_id;
			this.new_nice = new_nice;
		}
		public void apply(NiceSimulator sim){
			if (!sim.taskValid(task_id)){
				System.err.printf("Error: Time %d: Attempt to renice an invalid task with ID %d\n", timestep, task_id);
				System.exit(0);
			}
			System.out.printf("Time %d: Renicing task %d (old priority %d, new priority %d, time remaining %d)\n", timestep, task_id, sim.getPriority(task_id), new_nice, sim.getRemaining(task_id));
			sim.renice(task_id, new_nice);
		}
		private int task_id;
		private int new_nice;
	}

	public static void main(String[] args){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
		String nextLine = null;
		
		
		int maxTasks = 0;
		LinkedList<NiceOp> allOperations = new LinkedList<NiceOp>();
		
		try{
			int lineNumber = 1;
			
			//Read the first line (containing the maximum number of tasks
			nextLine = br.readLine();
			if (nextLine == null){
				System.err.printf("Error: Input is empty\n");
				return;
			}
						
			try{
				maxTasks = Integer.parseInt(nextLine.trim());
			}catch(NumberFormatException e){
				System.err.printf("Error: Line %d: Invalid number \"%s\"\n",lineNumber,nextLine.trim());
				return;
			}
			if (maxTasks <= 0){
				System.err.printf("Error: Line %d: Invalid task limit %d\n",lineNumber,maxTasks);
				return;
			}
			
			
			int previous_timestep = -1;
			//Read the remaining lines
			while((nextLine = br.readLine()) != null){
				lineNumber++;
				nextLine = nextLine.trim();
				if (nextLine.equals(""))
					continue; //Ignore blank lines
				String[] tokens = nextLine.split("\\s+"); //Split the line by whitespace
				if (tokens.length < 2){
					System.err.printf("Error: Line %d: Invalid format\n",lineNumber);
					return;
				}
				int timestep = 0;
				try{
					timestep = Integer.parseInt(tokens[0]);
				}catch(NumberFormatException e){
					System.err.printf("Error: Line %d: Invalid timestep %s\n",lineNumber,tokens[0]);
					return;
				}
				
				if (timestep < previous_timestep){
					System.err.printf("Error: Line %d: Time %d is out of order (previous operation has time %d)\n",lineNumber,timestep, previous_timestep);
					return;
				}
				previous_timestep = timestep;
				
				int taskID = -1;
				try{
					taskID = Integer.parseInt(tokens[2]);
					if (taskID < 0 || taskID >= maxTasks)
						throw new NumberFormatException();
				}catch(NumberFormatException e){
					System.err.printf("Error: Line %d: Invalid task ID %s\n",lineNumber,tokens[2]);
					return;
				}
				
				NiceOp op = null;
				if (tokens[1].equals("add")){
					int time_allocation = -1;
					try{
						time_allocation = Integer.parseInt(tokens[3]);
						if (time_allocation < 0)
							throw new NumberFormatException();
					}catch(NumberFormatException e){
						System.err.printf("Error: Line %d: Invalid initial allocation %s\n",lineNumber,tokens[3]);
						return;
					}
					op = new NiceOpAdd(timestep,taskID,time_allocation);
				}else if (tokens[1].equals("kill")){
					op = new NiceOpKill(timestep,taskID);
				}else if (tokens[1].equals("nice") || tokens[1].equals("renice")){
					int new_nice = -1;
					try{
						new_nice = Integer.parseInt(tokens[3]);
					}catch(NumberFormatException e){
						System.err.printf("Error: Line %d: Invalid nice value %s\n",lineNumber,tokens[3]);
						return;
					}
					op = new NiceOpRenice(timestep,taskID,new_nice);
					
				}else{
					System.err.printf("Error: Line %d: Invalid operation %s\n", lineNumber, tokens[1]);
					return;
				}
				allOperations.addLast(op);
				
			}
		}catch(IOException e){
			System.err.printf("Error reading input\n");
			return;
		}
		
		//Instantiate the NiceSimulator class
		NiceSimulator sim = new NiceSimulator(maxTasks);
		
		//Simulate until an idle timestep occurs
		for(int current_time = 0; ; current_time++){
			
			while(!allOperations.isEmpty() && allOperations.getFirst().getTimestep() <= current_time){
				NiceOp op = allOperations.removeFirst();
				assert(op.getTimestep() == current_time);
				op.apply(sim);
			}
			
			int job_finished = sim.simulate();
			if (job_finished == NiceSimulator.SIMULATE_NONE_FINISHED){
				//No job was finished on this time step, but the CPU was not idle.
				
			}else if (job_finished == NiceSimulator.SIMULATE_IDLE){
				System.out.printf("Time %d: CPU Idle (ending simulation)\n",current_time);
				break;
			}else{
				System.out.printf("Time %d: Job %d finished\n",current_time,job_finished);
			}
			
		}
		
	}

}
