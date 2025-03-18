public class Result {

    public double root;
    public int iterations;
    public Interval interval;

    public Result(double root, int iterations, Interval interval) {
        this.root = root;
        this.iterations = iterations;
        this.interval = interval;
    }
}
