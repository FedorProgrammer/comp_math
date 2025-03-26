public class InitData {
    public double a;
    public double b;
    public int count;

    InitData(double a, double b, int count) {
        this.a = a;
        this.b = b;
        this.count = count;
    }

    InitData(InitData data) {
        this.a = data.a;
        this.b = data.b;
        this.count = data.count;
    }
}
