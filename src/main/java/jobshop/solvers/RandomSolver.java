package jobshop.solvers;

import jobshop.*;
import jobshop.encodings.JobNumbers;

import java.util.Optional;
import java.util.Random;

public class RandomSolver implements Solver {

    @Override
    public Result solve(Instance instance, long deadline) {
        Random generator = new Random(0);

        JobNumbers sol = new JobNumbers(instance);

        for(int j = 0 ; j<instance.numJobs ; j++) {
            for(int t = 0 ; t<instance.numTasks ; t++) {
                sol.jobs[sol.nextToSet++] = j;
            }
        }
        Schedule best = sol.toSchedule();
        while(deadline - System.currentTimeMillis() > 1) {
            shuffleArray(sol.jobs, generator);
            Schedule s = sol.toSchedule();
            if(s.makespan() < best.makespan()) {
                best = s;
            }
        }


        return new Result(instance, best, Result.ExitCause.Timeout);
    }

    /** Simple Fisher–Yates array shuffling */
    private static void shuffleArray(int[] array, Random random)
    {
        int index;
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }
}


