package jobshop;

public abstract class Encoding {

    public final Instance instance;

    public Encoding(Instance instance) {
        this.instance = instance;
    }

    public abstract Schedule toSchedule();
}
