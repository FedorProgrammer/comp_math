import java.util.function.Function;

public class IntegralSolver {

    public static double M2 = 1;
    public static double M4 = 3;

    public static double f(double x) {
        double term = -Math.pow(x, 2) / 2;

        return Math.exp(term);
    }

    public static double rect(InitData data) {
        double h = Math.abs(data.b - data.a) / data.count;

        double I = 0.0;
        double x = data.a;
        for (int i = 0; i < data.count; i++) {
            I += f(x + h / 2);
            x += h;
        }

        I *= h;
        return I;
    }

    public static double trap(InitData data) {
        double h = Math.abs(data.b - data.a) / data.count;

        double I = (f(data.a) + f(data.b)) / 2;
        double x = data.a + h;
        for (int i = 1; i < data.count; i++) {
            I += f(x);
            x += h;
        }

        I *= h;
        return I;
    }

    public static double simps(InitData data) {
        double h = Math.abs(data.b - data.a) / data.count;

        double I = (f(data.a) + f(data.b));
        double x = data.a + h;
        for (int i = 1; i < data.count + 1; i++) {
            I += 4 * f(x - h / 2);
            if (i < data.count) {
                I += 2 * f(x);
            }
            x += h;
        }

        I *= h / 6;
        return I;
    }

    public static double runge(Function<InitData, Double> method, int k, InitData data, double epsilon) {
        InitData dataH = new InitData(data);

        double I_h = method.apply(dataH);
        double I_05h = method.apply(new InitData(dataH.a, dataH.b, 2 * dataH.count));

        System.out.println("        I_h        |          h       ");

        double h = Math.abs(dataH.b - dataH.a) / dataH.count;
        double currentI_h = I_h;
        double currentI_05h = I_05h;
        double currentError = Math.abs((currentI_05h - currentI_h) / (Math.pow(2, k) - 1));

        double prevError = Double.POSITIVE_INFINITY;

        while (currentError >= epsilon && currentError < prevError) {
            System.out.printf("%.16f | %.16f%n", currentI_h, h);

            dataH.count *= 2;

            h = Math.abs(dataH.b - dataH.a) / dataH.count;
            currentI_h = currentI_05h;
            currentI_05h = method.apply(new InitData(dataH.a, dataH.b, 2 * dataH.count));

            prevError = currentError;
            currentError = Math.abs((currentI_05h - currentI_h) / (Math.pow(2, k) - 1));
        }

        System.out.println("----------------------------------------");
        System.out.println("     I_result      |      h_result    ");
        System.out.printf("%.16f | %.16f%n", currentI_05h, h / 2);

        return currentError;
    }

    public static void main(String[] args) {
        double a = 0;
        double b = 1;
        int count = 10;

        InitData data = new InitData(a, b, count);

        double epsilon = 1e-3;
        double h = Math.abs(b - a) / count;

        System.out.println("Результат вычисления определенного интеграла: ");
        System.out.println("1) методом прямоугольников: " + rect(data));
        System.out.println("2) методом трапеций: " + trap(data));
        System.out.println("3) методом Симпсона: " + simps(data));

        System.out.println("----------------------------------------");

        double rectCoef = M2 * (b - a) / 24;
        double trapCoef = M2 * (b - a) / 12;
        double simpsCoef = M4 * (b - a) / 2880;

        System.out.println("Априорные погрешности: ");
        System.out.println("1) для метода прямоугольников: " + rectCoef * Math.pow(h, 2));
        System.out.println("2) для метода трапеций: " + trapCoef * Math.pow(h, 2));
        System.out.println("3) для метода Симпсона: " + simpsCoef * Math.pow(h, 4));

        System.out.println("----------------------------------------");

        System.out.println("Максимальные оценки h: ");
        System.out.println("1) для метода прямоугольников: " + Math.sqrt(epsilon / rectCoef));
        System.out.println("2) для метода трапеций: " + Math.sqrt(epsilon / trapCoef));
        System.out.println("3) для метода Симпосна: " + Math.sqrt(Math.sqrt(epsilon / simpsCoef)));

        System.out.println("----------------------------------------");

        System.out.println("Оценка по Рунге погрешности для метода прямоугольников: ");
        System.out.println("----------------------------------------");

        double rectPosteriori = runge(IntegralSolver::rect, 2, data, epsilon);

        System.out.println("----------------------------------------");
        System.out.println("Оценка по Рунге погрешности для метода трапеций: ");
        System.out.println("----------------------------------------");

        double trapPosteriori = runge(IntegralSolver::trap, 2, data, epsilon);

        System.out.println("----------------------------------------");
        System.out.println("Оценка по Рунге погрешности для метода Симпсона: ");
        System.out.println("----------------------------------------");

        double simpsPosteriori = runge(IntegralSolver::simps, 4, data, epsilon);

        System.out.println("----------------------------------------");
        System.out.println("Апостериорные погрешности: ");
        System.out.println("1) для метода прямоугольников: " + rectPosteriori);
        System.out.println("2) для метода трапеций: " + trapPosteriori);
        System.out.println("3) для метода Симпсона: " + simpsPosteriori);

    }
}