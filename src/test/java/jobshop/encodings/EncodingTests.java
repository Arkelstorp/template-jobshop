package jobshop.encodings;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.solvers.BasicSolver;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import java.util.Arrays;

public class EncodingTests {

    @Test
    public void testJobNumbers() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // numéro de jobs : 1 2 2 1 1 2 (cf exercices)
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        System.out.println("JobNumbers schedule n°1 :");
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 12;
        JobNumbers enc2 = new JobNumbers(instance);
        enc2.fromSchedule(sched);
        System.out.println(Arrays.toString(enc.jobs));
        System.out.println(Arrays.toString(enc2.jobs));



        // numéro de jobs : 1 1 2 2 1 2
        enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        sched = enc.toSchedule();
        System.out.println("JobNumbers schedule n°2 :");
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 14;
        enc2.fromSchedule(sched);
        System.out.println(Arrays.toString(enc.jobs));
        System.out.println(Arrays.toString(enc2.jobs));
    }

    @Test
    public void testResourceOrder() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        ResourceOrder enc = new ResourceOrder(instance);
        enc.tasksByMachine[0][0] = new Task(0,0);
        enc.tasksByMachine[0][1] = new Task(1,1);
        enc.tasksByMachine[1][0] = new Task(1,0);
        enc.tasksByMachine[1][1] = new Task(0,1);
        enc.tasksByMachine[2][0] = new Task(0,2);
        enc.tasksByMachine[2][1] = new Task(1,2);

        Schedule sched = enc.toSchedule();
        System.out.println("ResourceOrder schedule n°1 :");
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 12;
        ResourceOrder enc2 = new ResourceOrder(sched);
        assert enc.toString().equals(enc2.toString());

        enc = new ResourceOrder(instance);
        enc.tasksByMachine[0][0] = new Task(0,0);
        enc.tasksByMachine[0][1] = new Task(1,1);
        enc.tasksByMachine[1][0] = new Task(0,1);
        enc.tasksByMachine[1][1] = new Task(1,0);
        enc.tasksByMachine[2][0] = new Task(0,2);
        enc.tasksByMachine[2][1] = new Task(1,2);

        sched = enc.toSchedule();
        System.out.println("ResourceOrder schedule n°2 :");
        System.out.println(sched);
        assert sched.isValid();
        assert sched.makespan() == 14;
        enc2 = new ResourceOrder(sched);
        assert enc.toString().equals(enc2.toString());
    }

    @Test
    public void testBasicSolver() throws IOException {
        Instance instance = Instance.fromFile(Paths.get("instances/aaa1"));

        // build a solution that should be equal to the result of BasicSolver
        JobNumbers enc = new JobNumbers(instance);
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;
        enc.jobs[enc.nextToSet++] = 0;
        enc.jobs[enc.nextToSet++] = 1;

        Schedule sched = enc.toSchedule();
        assert sched.isValid();
        assert sched.makespan() == 12;

        Solver solver = new BasicSolver();
        Result result = solver.solve(instance, System.currentTimeMillis() + 10);

        assert result.schedule.isValid();
        assert result.schedule.makespan() == sched.makespan(); // should have the same makespan
    }

}
