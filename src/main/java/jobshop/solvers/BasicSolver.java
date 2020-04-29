package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.JobNumbers;

public class BasicSolver implements Solver {
    @Override
    public Result solve(Instance instance, long deadline) {

        JobNumbers sol = new JobNumbers(instance);
        for(int t = 0 ; t<instance.numTasks ; t++) {
            for(int j = 0 ; j<instance.numJobs ; j++) {
                sol.jobs[sol.nextToSet++] = j;
            }
        }

        return new Result(instance, sol.toSchedule(), Result.ExitCause.Blocked);
    }
}
