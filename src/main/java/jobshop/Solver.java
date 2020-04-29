package jobshop;

public interface Solver {

    Result solve(Instance instance, long deadline);

}
