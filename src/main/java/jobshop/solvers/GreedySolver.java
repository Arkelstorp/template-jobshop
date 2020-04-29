package jobshop.solvers;

import jobshop.*;
import jobshop.encodings.JobNumbers;

public class GreedySolver implements Solver{

    public GreedySolver(Priority priority) {
        this.priority = priority ;
    }

    @Override
    public Result solve(Instance instance, long deadline) {

        JobNumbers sol = new JobNumbers(instance) ;
        int[] nextTask = new int[instance.numJobs] ;
        int[] nextFreeMachineSpot = new int[instance.numMachines] ;
        int[] nextFreeJobSpot = new int[instance.numJobs] ;
        int[] remainingTime = new int[instance.numJobs];

        if (priority == Priority.SPT) {

            for(int i=0;i<instance.numJobs*instance.numTasks;i++) {
                int shortestTask = Integer.MAX_VALUE ;
                int indexShortestTask = 0 ;
                for(int j=0;j<instance.numJobs;j++) {
                    if(nextTask[j]<instance.numTasks) {
                        if (instance.durations[j][nextTask[j]] < shortestTask) {
                            shortestTask = instance.durations[j][nextTask[j]];
                            indexShortestTask = j;
                        }
                    }
                }
                sol.jobs[sol.nextToSet++] = indexShortestTask ;
                nextTask[indexShortestTask]++ ;
            }

        }
        else if(priority == Priority.LPT) {

            for(int i=0;i<instance.numJobs*instance.numTasks;i++) {
                int longestTask = 0 ;
                int indexLongestTask = 0 ;
                for(int j=0;j<instance.numJobs;j++) {
                    if(nextTask[j]<instance.numTasks) {
                        if (instance.durations[j][nextTask[j]] > longestTask) {
                            longestTask = instance.durations[j][nextTask[j]];
                            indexLongestTask = j;
                        }
                    }
                }
                sol.jobs[sol.nextToSet++] = indexLongestTask ;
                nextTask[indexLongestTask]++ ;
            }
        }
        else if(priority == Priority.SRPT) {

            for (int i = 0; i < instance.numJobs; i++) {
                for (int j = 0; j < instance.numTasks; j++) {
                    remainingTime[i] += instance.durations[i][j];
                }
            }

            for (int i = 0; i < instance.numJobs * instance.numTasks; i++) {
                int shortestRemainingTime = Integer.MAX_VALUE;
                int indexShortestRemainingTime = 0;
                for (int j = 0; j < instance.numJobs; j++) {
                    if (nextTask[j] < instance.numTasks) {
                        if (remainingTime[j] < shortestRemainingTime) {
                            shortestRemainingTime = remainingTime[j];
                            indexShortestRemainingTime = j;
                        }
                    }
                }
                sol.jobs[sol.nextToSet++] = indexShortestRemainingTime;
                remainingTime[indexShortestRemainingTime] -= instance.durations[indexShortestRemainingTime][nextTask[indexShortestRemainingTime]];
                nextTask[indexShortestRemainingTime]++;
            }

        }
        else if(priority == Priority.LRPT) {

            for (int i = 0; i < instance.numJobs; i++) {
                for (int j = 0; j < instance.numTasks; j++) {
                    remainingTime[i] += instance.durations[i][j];
                }
            }

            for (int i = 0; i < instance.numJobs * instance.numTasks; i++) {
                int longestRemainingTime = 0;
                int indexLongestRemainingTime = 0;
                for (int j = 0; j < instance.numJobs; j++) {
                    if (nextTask[j] < instance.numTasks) {
                        if (remainingTime[j] > longestRemainingTime) {
                            longestRemainingTime = remainingTime[j];
                            indexLongestRemainingTime = j;
                        }
                    }
                }
                sol.jobs[sol.nextToSet++] = indexLongestRemainingTime;
                remainingTime[indexLongestRemainingTime] -= instance.durations[indexLongestRemainingTime][nextTask[indexLongestRemainingTime]];
                nextTask[indexLongestRemainingTime]++;
            }
        }
        else if(priority == Priority.EST_SPT) {

            for (int i = 0; i < instance.numJobs * instance.numTasks; i++) {
                int earliestStartingTime = Integer.MAX_VALUE;
                int shortestTask = Integer.MAX_VALUE ;
                int indexEarliestStartingTime = 0;
                for (int j = 0; j < instance.numJobs; j++) {
                    if (nextTask[j] < instance.numTasks) {
                        int thisEarliestStartingTime = Math.max(nextFreeMachineSpot[instance.machines[j][nextTask[j]]], nextFreeJobSpot[j]);
                        if ((thisEarliestStartingTime < earliestStartingTime)||((thisEarliestStartingTime == earliestStartingTime)&&(instance.durations[j][nextTask[j]] < shortestTask))) {
                            earliestStartingTime = thisEarliestStartingTime;
                            indexEarliestStartingTime = j;
                            shortestTask = instance.durations[j][nextTask[j]] ;
                        }
                    }
                }
                sol.jobs[sol.nextToSet++] = indexEarliestStartingTime;
                nextFreeJobSpot[indexEarliestStartingTime] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextFreeMachineSpot[instance.machines[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]]] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextTask[indexEarliestStartingTime]++;
            }
        }
        else if(priority == Priority.EST_LPT) {
            for (int i = 0; i < instance.numJobs * instance.numTasks; i++) {
                int earliestStartingTime = Integer.MAX_VALUE;
                int longestTask = 0;
                int indexEarliestStartingTime = 0;
                for (int j = 0; j < instance.numJobs; j++) {
                    if (nextTask[j] < instance.numTasks) {
                        int thisEarliestStartingTime = Math.max(nextFreeMachineSpot[instance.machines[j][nextTask[j]]], nextFreeJobSpot[j]);
                        if ((thisEarliestStartingTime < earliestStartingTime) || ((thisEarliestStartingTime == earliestStartingTime) && (instance.durations[j][nextTask[j]] > longestTask))) {
                            earliestStartingTime = thisEarliestStartingTime;
                            indexEarliestStartingTime = j;
                            longestTask = instance.durations[j][nextTask[j]];
                        }
                    }
                }
                sol.jobs[sol.nextToSet++] = indexEarliestStartingTime;
                nextFreeJobSpot[indexEarliestStartingTime] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextFreeMachineSpot[instance.machines[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]]] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextTask[indexEarliestStartingTime]++;
            }
        }
        else if(priority == Priority.EST_SRPT) {

            for (int i = 0; i < instance.numJobs; i++) {
                for (int j = 0; j < instance.numTasks; j++) {
                    remainingTime[i] += instance.durations[i][j];
                }
            }

            for (int i = 0; i < instance.numJobs * instance.numTasks; i++) {
                int earliestStartingTime = Integer.MAX_VALUE;
                int shortestRemainingTime = Integer.MAX_VALUE ;
                int indexEarliestStartingTime = 0;
                for (int j = 0; j < instance.numJobs; j++) {
                    if (nextTask[j] < instance.numTasks) {
                        int thisEarliestStartingTime = Math.max(nextFreeMachineSpot[instance.machines[j][nextTask[j]]], nextFreeJobSpot[j]);
                        if ((thisEarliestStartingTime < earliestStartingTime)||((thisEarliestStartingTime == earliestStartingTime)&&(remainingTime[j] < shortestRemainingTime))) {
                            earliestStartingTime = thisEarliestStartingTime;
                            indexEarliestStartingTime = j;
                            shortestRemainingTime = remainingTime[j] ;
                        }
                    }
                }
                remainingTime[indexEarliestStartingTime] -= instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]] ;
                sol.jobs[sol.nextToSet++] = indexEarliestStartingTime;
                nextFreeJobSpot[indexEarliestStartingTime] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextFreeMachineSpot[instance.machines[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]]] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextTask[indexEarliestStartingTime]++;
            }

        }
        else if(priority == Priority.EST_LRPT) {

            for (int i = 0; i < instance.numJobs; i++) {
                for (int j = 0; j < instance.numTasks; j++) {
                    remainingTime[i] += instance.durations[i][j];
                }
            }

            for (int i = 0; i < instance.numJobs * instance.numTasks; i++) {
                int earliestStartingTime = Integer.MAX_VALUE;
                int longestRemainingTime = 0 ;
                int indexEarliestStartingTime = 0;
                for (int j = 0; j < instance.numJobs; j++) {
                    if (nextTask[j] < instance.numTasks) {
                        int thisEarliestStartingTime = Math.max(nextFreeMachineSpot[instance.machines[j][nextTask[j]]], nextFreeJobSpot[j]);
                        if ((thisEarliestStartingTime < earliestStartingTime)||((thisEarliestStartingTime == earliestStartingTime)&&(remainingTime[j] > longestRemainingTime))) {
                            earliestStartingTime = thisEarliestStartingTime;
                            indexEarliestStartingTime = j;
                            longestRemainingTime = remainingTime[j] ;
                        }
                    }
                }
                remainingTime[indexEarliestStartingTime] -= instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]] ;
                sol.jobs[sol.nextToSet++] = indexEarliestStartingTime;
                nextFreeJobSpot[indexEarliestStartingTime] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextFreeMachineSpot[instance.machines[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]]] = earliestStartingTime + instance.durations[indexEarliestStartingTime][nextTask[indexEarliestStartingTime]];
                nextTask[indexEarliestStartingTime]++;
            }
        }
        return new Result(instance, sol.toSchedule(), Result.ExitCause.Blocked);
    }

    public enum Priority {
        SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT
    }

    Priority priority ;
}
