public class Interval {
    public double a;
    public double b;

    Interval(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public double length() {
        return Math.abs(b - a);
    }

}