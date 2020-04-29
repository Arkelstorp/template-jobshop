package jobshop;

import jobshop.encodings.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Instance {

    /** Number of jobs in the instance */
    public final int numJobs;

    /** Number of tasks per job */
    public final int numTasks;

    /** Number of machines, assumed to be same as number of tasks. */
    public final int numMachines;

    public final int[][] durations;
    public final int[][] machines;

    public int duration(int job, int task) {
        return durations[job][task];
    }
    public int duration(Task t) {
        return duration(t.job, t.task);
    }
    public int machine(int job, int task) {
        return machines[job][task];
    }
    public int machine(Task t) {
        return this.machine(t.job, t.task);
    }

    public int task_with_machine(int job, int wanted_machine) {
        for(int task = 0 ; task < numTasks ; task++) {
            if(machine(job, task) == wanted_machine)
                return task;
        }
        throw new RuntimeException("No task targeting machine "+wanted_machine+" on job "+job);
    }

    Instance(int numJobs, int numTasks) {
        this.numJobs = numJobs;
        this.numTasks = numTasks;
        this.numMachines = numTasks;

        durations = new int[numJobs][numTasks];
        machines = new int[numJobs][numTasks];
    }

    public static Instance fromFile(Path path) throws IOException {
        Iterator<String> lines = Files.readAllLines(path).stream()
                .filter(l -> !l.startsWith("#"))
                .collect(Collectors.toList())
                .iterator();

        Scanner header = new Scanner(lines.next());
        int num_jobs = header.nextInt();
        int num_tasks = header.nextInt();
        Instance pb = new Instance(num_jobs, num_tasks);

        for(int job = 0 ; job<num_jobs ; job++) {
            Scanner line = new Scanner(lines.next());
            for(int task = 0 ; task < num_tasks ; task++) {
                pb.machines[job][task] = line.nextInt();
                pb.durations[job][task] = line.nextInt();
            }
        }

        return pb;
    }
}
