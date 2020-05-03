package jobshop;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import jobshop.solvers.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class Main {

    /** All solvers available in this program */
    private static HashMap<String, Solver> solvers;
    static {
        solvers = new HashMap<>();
        solvers.put("basic", new BasicSolver());
        solvers.put("random", new RandomSolver());
        solvers.put("spt", new GreedySolver(GreedySolver.Priority.SPT)) ;
        solvers.put("lpt", new GreedySolver(GreedySolver.Priority.LPT)) ;
        solvers.put("srpt", new GreedySolver(GreedySolver.Priority.SRPT)) ;
        solvers.put("lrpt", new GreedySolver(GreedySolver.Priority.LRPT)) ;
        solvers.put("est_spt", new GreedySolver(GreedySolver.Priority.EST_SPT)) ;
        solvers.put("est_lpt", new GreedySolver(GreedySolver.Priority.EST_LPT)) ;
        solvers.put("est_srpt", new GreedySolver(GreedySolver.Priority.EST_SRPT)) ;
        solvers.put("est_lrpt", new GreedySolver(GreedySolver.Priority.EST_LRPT)) ;
        solvers.put("descent_spt", new DescentSolver(GreedySolver.Priority.SPT)) ;
        solvers.put("descent_lpt", new DescentSolver(GreedySolver.Priority.LPT)) ;
        solvers.put("descent_srpt", new DescentSolver(GreedySolver.Priority.SRPT)) ;
        solvers.put("descent_lrpt", new DescentSolver(GreedySolver.Priority.LRPT)) ;
        solvers.put("descent_est_spt", new DescentSolver(GreedySolver.Priority.EST_SPT)) ;
        solvers.put("descent_est_lpt", new DescentSolver(GreedySolver.Priority.EST_LPT)) ;
        solvers.put("descent_est_srpt", new DescentSolver(GreedySolver.Priority.EST_SRPT)) ;
        solvers.put("descent_est_lrpt", new DescentSolver(GreedySolver.Priority.EST_LRPT)) ;
        solvers.put("taboo_spt_10", new TabooSolver(GreedySolver.Priority.SPT, 10)) ;
        solvers.put("taboo_spt_100", new TabooSolver(GreedySolver.Priority.SPT, 100)) ;
        solvers.put("taboo_spt_1000", new TabooSolver(GreedySolver.Priority.SPT, 1000)) ;
        solvers.put("taboo_spt_10000", new TabooSolver(GreedySolver.Priority.SPT, 10000)) ;
        solvers.put("taboo_lpt_10", new TabooSolver(GreedySolver.Priority.LPT, 10)) ;
        solvers.put("taboo_lpt_100", new TabooSolver(GreedySolver.Priority.LPT, 100)) ;
        solvers.put("taboo_lpt_1000", new TabooSolver(GreedySolver.Priority.LPT, 1000)) ;
        solvers.put("taboo_lpt_10000", new TabooSolver(GreedySolver.Priority.LPT, 10000)) ;
        solvers.put("taboo_srpt_10", new TabooSolver(GreedySolver.Priority.SRPT, 10)) ;
        solvers.put("taboo_srpt_100", new TabooSolver(GreedySolver.Priority.SRPT, 100)) ;
        solvers.put("taboo_srpt_1000", new TabooSolver(GreedySolver.Priority.SRPT, 1000)) ;
        solvers.put("taboo_srpt_10000", new TabooSolver(GreedySolver.Priority.SRPT, 10000)) ;
        solvers.put("taboo_lrpt_10", new TabooSolver(GreedySolver.Priority.LRPT, 10)) ;
        solvers.put("taboo_lrpt_100", new TabooSolver(GreedySolver.Priority.LRPT, 100)) ;
        solvers.put("taboo_lrpt_1000", new TabooSolver(GreedySolver.Priority.LRPT, 1000)) ;
        solvers.put("taboo_lrpt_10000", new TabooSolver(GreedySolver.Priority.LRPT, 10000)) ;
        solvers.put("taboo_est_spt_10", new TabooSolver(GreedySolver.Priority.EST_SPT, 10)) ;
        solvers.put("taboo_est_spt_100", new TabooSolver(GreedySolver.Priority.EST_SPT, 100)) ;
        solvers.put("taboo_est_spt_1000", new TabooSolver(GreedySolver.Priority.EST_SPT, 1000)) ;
        solvers.put("taboo_est_spt_10000", new TabooSolver(GreedySolver.Priority.EST_SPT, 10000)) ;
        solvers.put("taboo_est_lpt_10", new TabooSolver(GreedySolver.Priority.EST_LPT, 10)) ;
        solvers.put("taboo_est_lpt_100", new TabooSolver(GreedySolver.Priority.EST_LPT, 100)) ;
        solvers.put("taboo_est_lpt_1000", new TabooSolver(GreedySolver.Priority.EST_LPT, 1000)) ;
        solvers.put("taboo_est_lpt_10000", new TabooSolver(GreedySolver.Priority.EST_LPT, 10000)) ;
        solvers.put("taboo_est_srpt_10", new TabooSolver(GreedySolver.Priority.EST_SRPT, 10)) ;
        solvers.put("taboo_est_srpt_100", new TabooSolver(GreedySolver.Priority.EST_SRPT, 100)) ;
        solvers.put("taboo_est_srpt_1000", new TabooSolver(GreedySolver.Priority.EST_SRPT, 1000)) ;
        solvers.put("taboo_est_srpt_10000", new TabooSolver(GreedySolver.Priority.EST_SRPT, 10000)) ;
        solvers.put("taboo_est_lrpt_10", new TabooSolver(GreedySolver.Priority.EST_LRPT, 10)) ;
        solvers.put("taboo_est_lrpt_100", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100)) ;
        solvers.put("taboo_est_lrpt_1000", new TabooSolver(GreedySolver.Priority.EST_LRPT, 1000)) ;
        solvers.put("taboo_est_lrpt_10000", new TabooSolver(GreedySolver.Priority.EST_LRPT, 10000)) ;
        solvers.put("annealing_spt_10", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 10, 1)) ;
        solvers.put("annealing_spt_20", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 20, 1)) ;
        solvers.put("annealing_spt_30_1", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 30, 1)) ;
        solvers.put("annealing_spt_30_10000", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 30, 10000)) ;
        solvers.put("annealing_spt_30_100", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 30, 100)) ;
        solvers.put("annealing_spt_30_0.01", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 30, 0.01)) ;
        solvers.put("annealing_spt_40", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 40, 1)) ;
        solvers.put("annealing_spt_50", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 50, 1)) ;
        solvers.put("annealing_spt_60", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 60, 1)) ;
        solvers.put("annealing_spt_70", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 70, 1)) ;
        solvers.put("annealing_spt_80", new SimulatedAnnealingSolver(GreedySolver.Priority.SPT, 80, 1)) ;
        solvers.put("annealing_lpt", new SimulatedAnnealingSolver(GreedySolver.Priority.LPT, 10 ,1)) ;
        solvers.put("annealing_srpt", new SimulatedAnnealingSolver(GreedySolver.Priority.SRPT, 10 ,1)) ;
        solvers.put("annealing_lrpt_30_100", new SimulatedAnnealingSolver(GreedySolver.Priority.LRPT, 30 ,100)) ;
        solvers.put("annealing_est_spt", new SimulatedAnnealingSolver(GreedySolver.Priority.EST_SPT, 10 ,1)) ;
        solvers.put("annealing_est_lpt", new SimulatedAnnealingSolver(GreedySolver.Priority.EST_LPT, 10 ,1)) ;
        solvers.put("annealing_est_srpt", new SimulatedAnnealingSolver(GreedySolver.Priority.EST_SRPT, 10 ,1)) ;
        solvers.put("annealing_est_lrpt", new SimulatedAnnealingSolver(GreedySolver.Priority.EST_LRPT, 10 ,1)) ;
    }


    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("jsp-solver").build()
                .defaultHelp(true)
                .description("Solves jobshop problems.");

        parser.addArgument("-t", "--timeout")
                .setDefault(1L)
                .type(Long.class)
                .help("Solver timeout in seconds for each instance");
        parser.addArgument("--solver")
                .nargs("+")
                .required(true)
                .help("Solver(s) to use (space separated if more than one)");

        parser.addArgument("--instance")
                .nargs("+")
                .required(true)
                .help("Instance(s) to solve (space separated if more than one)");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        PrintStream output = System.out;

        long solveTimeMs = ns.getLong("timeout") * 1000;

        List<String> solversToTest = ns.getList("solver");
        for(String solverName : solversToTest) {
            if(!solvers.containsKey(solverName)) {
                System.err.println("ERROR: Solver \"" + solverName + "\" is not avalaible.");
                System.err.println("       Available solvers: " + solvers.keySet().toString());
                System.err.println("       You can provide your own solvers by adding them to the `Main.solvers` HashMap.");
                System.exit(1);
            }
        }
        List<String> instances = ns.<String>getList("instance");
        for(String instanceName : instances) {
            if(!BestKnownResult.isKnown(instanceName)) {
                System.err.println("ERROR: instance \"" + instanceName + "\" is not avalaible.");
                System.err.println("       available instances: " + Arrays.toString(BestKnownResult.instances));
                System.exit(1);
            }
        }

        float[] runtimes = new float[solversToTest.size()];
        float[] distances = new float[solversToTest.size()];

        try {
            output.print(  "                         ");
            for(String s : solversToTest)
                output.printf("%-30s", s);
            output.println();
            output.print("instance size  best      ");
            for(String s : solversToTest) {
                output.print("runtime makespan ecart        ");
            }
            output.println();


        for(String instanceName : instances) {
            int bestKnown = BestKnownResult.of(instanceName);


            Path path = Paths.get("instances/", instanceName);
            Instance instance = Instance.fromFile(path);

            output.printf("%-8s %-5s %4d      ",instanceName, instance.numJobs +"x"+instance.numTasks, bestKnown);

            for(int solverId = 0 ; solverId < solversToTest.size() ; solverId++) {
                String solverName = solversToTest.get(solverId);
                Solver solver = solvers.get(solverName);
                long start = System.currentTimeMillis();
                long deadline = System.currentTimeMillis() + solveTimeMs;
                Result result = solver.solve(instance, deadline);
                long runtime = System.currentTimeMillis() - start;

                if(!result.schedule.isValid()) {
                    System.err.println("ERROR: solver returned an invalid schedule");
                    System.exit(1);
                }

                assert result.schedule.isValid();
                int makespan = result.schedule.makespan();
                float dist = 100f * (makespan - bestKnown) / (float) bestKnown;
                runtimes[solverId] += (float) runtime / (float) instances.size();
                distances[solverId] += dist / (float) instances.size();

                output.printf("%7d %8s %5.1f        ", runtime, makespan, dist);
                output.flush();
            }
            output.println();

        }


        output.printf("%-8s %-5s %4s      ", "AVG", "-", "-");
        for(int solverId = 0 ; solverId < solversToTest.size() ; solverId++) {
            output.printf("%7.1f %8s %5.1f        ", runtimes[solverId], "-", distances[solverId]);
        }



        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
