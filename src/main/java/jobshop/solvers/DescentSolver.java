package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;

public class DescentSolver implements Solver {

    /** A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask= 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    static class Block {
        /** machine on which the block is identified */
        final int machine;
        /** index of the first task of the block */
        final int firstTask;
        /** index of the last task of the block */
        final int lastTask;

        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swam with : machine = 1, t1= 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    static class Swap {
        // machine on which to perform the swap
        final int machine;
        // index of one task to be swapped
        final int t1;
        // index of the other task to be swapped
        final int t2;

        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            this.t1 = t1;
            this.t2 = t2;
        }

        /** Apply this swap on the given resource order, transforming it into a new solution. */
        public void applyOn(ResourceOrder order) {

            Schedule schedule = order.toSchedule() ;
            List<Task> tasks = schedule.criticalPath() ;

            Task task1 = tasks.get(t1).copy() ;
            Task task2 = tasks.get(t2).copy() ;

            int i = 0;
            boolean found1 = Boolean.FALSE ;
            boolean found2 = Boolean.FALSE ;
            while(!found1 || !found2) {
                if(task1.equals(order.tasksByMachine[machine][i])) {
                    found1 = Boolean.TRUE ;
                    order.tasksByMachine[machine][i] = task2.copy() ;
                } else if (task2.equals(order.tasksByMachine[machine][i])) {
                    found2 = Boolean.TRUE ;
                    order.tasksByMachine[machine][i] = task1.copy() ;
                }
                i++ ;
            }
        }
    }


    @Override
    public Result solve(Instance instance, long deadline) {

        GreedySolver greedy = new GreedySolver(GreedySolver.Priority.SPT) ;
        Schedule schedule = greedy.solve(instance, deadline).schedule ;

        int currentSpan = schedule.makespan() ;
        ResourceOrder currentResourceOrder= new ResourceOrder(schedule) ;

        int bestSpan = currentSpan ;
        ResourceOrder bestResourceOrder = currentResourceOrder.copy() ;

        while(deadline - System.currentTimeMillis() > 1) {
            List<Swap> listSwap = new ArrayList<>() ;
            List<Block> listBlock = blocksOfCriticalPath(currentResourceOrder) ;
            int size = listBlock.size() ;
            for(int i = 0; i < size; i++) {
                listSwap.addAll(neighbors(listBlock.get(i))) ;
            }

            size = listSwap.size() ;

            for(int i = 0; i < size; i++) {
                ResourceOrder testResourceOrder = currentResourceOrder.copy() ;
                listSwap.get(i).applyOn(testResourceOrder);
                int testSpan = testResourceOrder.toSchedule().makespan() ;
                if (testSpan < bestSpan) {
                    bestSpan = testSpan ;
                    bestResourceOrder = testResourceOrder.copy() ;
                }
            }
            if (bestSpan < currentSpan) {
                currentResourceOrder = bestResourceOrder.copy();
                currentSpan = bestSpan;
            } else {
                return new Result(instance, bestResourceOrder.toSchedule(), Result.ExitCause.Blocked) ;
            }
        }
        return new Result(instance, currentResourceOrder.toSchedule(), Result.ExitCause.Timeout) ;
    }

    /** Returns a list of all blocks of the critical path. */
    List<Block> blocksOfCriticalPath(ResourceOrder order) {

        List<Block> result = new ArrayList<>() ;
        Schedule schedule = order.toSchedule();
        List<Task> tasks = schedule.criticalPath();

        int blockStart = 0 ;
        int blockEnd = 0 ;
        int currentMachine = order.instance.machine(tasks.get(0)) ;

        int size = tasks.size() ;

        for(int i=1; i<size; i++) {
            if (currentMachine == order.instance.machine(tasks.get(i))) {
                blockEnd++ ;
            } else {
                if (blockStart != blockEnd) {
                    result.add(new Block(currentMachine, blockStart, blockEnd)) ;
                }
                blockStart = i ;
                blockEnd = i ;
                currentMachine = order.instance.machine(tasks.get(i)) ;
            }
        }
        return result ;
    }

    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    List<Swap> neighbors(Block block) {
        List<Swap> result = new ArrayList<>() ;
        if (block.lastTask - block.firstTask+1 == 2) {
            result.add(new Swap(block.machine, block.firstTask, block.lastTask));
        } else {
            result.add(new Swap(block.machine, block.firstTask, block.firstTask+1));
            result.add(new Swap(block.machine, block.lastTask-1, block.lastTask)) ;
        }
        return result ;
    }

}