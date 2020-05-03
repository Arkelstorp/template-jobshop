package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.encodings.ResourceOrder;


import java.util.*;

public class SimulatedAnnealingSolver extends DescentSolver {

    GreedySolver.Priority priority ;
    int nbStep ;
    double mulTemperature ;

    public SimulatedAnnealingSolver(GreedySolver.Priority priority, int nbStep, double mulTemperature) {
        this.priority = priority ;
        this.nbStep = nbStep ;
        this.mulTemperature = mulTemperature ;
    }

    @Override
    public Result solve(Instance instance, long deadline) {

        GreedySolver greedy = new GreedySolver(priority) ;
        Schedule schedule = greedy.solve(instance, deadline).schedule ;

        int currentSpan = schedule.makespan() ;
        ResourceOrder currentResourceOrder= new ResourceOrder(schedule) ;

        double temperature = currentSpan*mulTemperature;
        double minTemperature = 0.1 ;
        int currentStep = 0 ;

        Random ran = new Random();

        while(deadline - System.currentTimeMillis() > 1) {
            List<DescentSolver.Swap> listSwap = new ArrayList<>();
            List<DescentSolver.Block> listBlock = blocksOfCriticalPath(currentResourceOrder);
            int size = listBlock.size();
            for (int i = 0; i < size; i++) {
                listSwap.addAll(neighbors(listBlock.get(i)));
            }

            size = listSwap.size();

            if ((size != 0) && (temperature >= minTemperature)) {
                ResourceOrder testResourceOrder = currentResourceOrder.copy();
                listSwap.get(ran.nextInt(size)).applyOn(testResourceOrder);
                int testSpan = testResourceOrder.toSchedule().makespan();

                if ((testSpan <= currentSpan) || (Math.random() < Math.exp(-(testSpan-currentSpan)/temperature))) {
                    currentResourceOrder = testResourceOrder;
                    currentSpan = testSpan;
                }

                currentStep++;

                if (currentStep >= nbStep) {
                    temperature *= 0.9;
                    currentStep = 0;
                }
            } else {
                break ;
            }
        }

        return new Result(instance, currentResourceOrder.toSchedule(), Result.ExitCause.Timeout) ;

    }
}
