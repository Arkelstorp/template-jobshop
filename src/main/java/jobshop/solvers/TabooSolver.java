package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.List;

public class TabooSolver extends DescentSolver {

    @Override
    public Result solve(Instance instance, long deadline) {

        int[][] sTaboo = new int[instance.numJobs*instance.numTasks][instance.numJobs*instance.numTasks] ;
        int k = 0 ;
        int maxIter = 100 ;
        int dureeTaboo = 2 ;

        GreedySolver greedy = new GreedySolver(GreedySolver.Priority.SPT) ;
        Schedule schedule = greedy.solve(instance, deadline).schedule ;

        int currentSpan = schedule.makespan() ;
        ResourceOrder currentResourceOrder= new ResourceOrder(schedule) ;

        int bestSpan = currentSpan ;
        ResourceOrder bestResourceOrder = currentResourceOrder.copy() ;


        while((k <= maxIter) && (deadline - System.currentTimeMillis() > 1)) {
            List<Swap> listSwap = new ArrayList<>() ;
            List<Task> listTask = currentResourceOrder.toSchedule().criticalPath() ;
            List<Block> listBlock = blocksOfCriticalPath(currentResourceOrder) ;
            int size = listBlock.size() ;
            for(int i = 0; i < size; i++) {
                listSwap.addAll(neighbors(listBlock.get(i))) ;
            }

            size = listSwap.size() ;
            int indexBestSwap = -1;

            for(int i = 0; i < size; i++) {
                ResourceOrder testResourceOrder = currentResourceOrder.copy() ;
                Swap currentSwap = listSwap.get(i) ;
                Task t1 = listTask.get(currentSwap.t1) ;
                Task t2 = listTask.get(currentSwap.t2) ;
                currentSwap.applyOn(testResourceOrder) ;
                int testSpan = testResourceOrder.toSchedule().makespan() ;
                if ((testSpan < bestSpan) || ((testSpan == bestSpan) && (k >= sTaboo[t1.job*instance.numTasks+t1.task][t2.job*instance.numTasks+t2.task]))) {
                    bestSpan = testSpan;
                    bestResourceOrder = testResourceOrder.copy();
                    indexBestSwap = i ;
                }
            }

            if  (indexBestSwap != -1) {
                currentResourceOrder = bestResourceOrder.copy();

                Swap bestSwap = listSwap.get(indexBestSwap);
                Task t1 = listTask.get(bestSwap.t1);
                Task t2 = listTask.get(bestSwap.t2);
                int posT1 = t1.job * instance.numTasks + t1.task;
                int posT2 = t2.job * instance.numTasks + t2.task;
                // sTaboo[posT1][posT2] = k + dureeTaboo;
                sTaboo[posT2][posT1] = k + dureeTaboo;
            }
            k++ ;
        }
        return new Result(instance, bestResourceOrder.toSchedule(), Result.ExitCause.Timeout) ;
    }
}
