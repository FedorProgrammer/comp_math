import java.util.function.Function;

/*
 * Задание: вычислить определенный интеграл
 * границы области интегрирования: [a, b] = [0, 1]
 * количество частей интервала интегрирования: 10
 * точность eps = 0.001
 */

public class IntegralSolver {
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

    public static void runge(Function<InitData, Double> method, int k, InitData data, double epsilon) {
        InitData dataH = new InitData(data);

        double I_h = method.apply(dataH);
        double I_05h = method.apply(new InitData(dataH.a, dataH.b, 2 * dataH.count));

        System.out.println("        I_h        |          h       ");

        double h = Math.abs(dataH.b - dataH.a) / dataH.count;
        double currentI_h = I_h;
        double currentI_05h = I_05h;
        double currentError = Math.abs((currentI_05h - currentI_h) / (Math.pow(2, k) - 1));

        while (currentError >= epsilon) {
            System.out.printf("%.16f | %.16f%n", currentI_h, h);

            dataH.count *= 2;

            h = Math.abs(dataH.b - dataH.a) / dataH.count;
            currentI_h = currentI_05h;
            currentI_05h = method.apply(new InitData(dataH.a, dataH.b, 2 * dataH.count));
            currentError = Math.abs((currentI_05h - currentI_h) / (Math.pow(2, k) - 1));
        }

        System.out.println("----------------------------------------");
        System.out.println("     I_result      |      h_result    ");
        System.out.printf("%.16f | %.16f%n", currentI_h, h);

    }

    public static void main(String[] args) {
        int a = 0;
        int b = 1;
        int count = 10;

        InitData data = new InitData(a, b, count);

        double epsilon = 1e-3; // epsilon issue (runge isn't working)

        System.out.println("Результат вычисления опрделенного интеграла: ");
        System.out.println("1) методом прямоугольников: " + rect(data));
        System.out.println("2) методом трапеций: " + trap(data));
        System.out.println("3) методом Симпсона: " + simps(data));

        System.out.println("----------------------------------------");

        System.out.println("Оценка по Рунге погрешности для метода прямоугольников: ");
        System.out.println("----------------------------------------");

        runge(IntegralSolver::rect, 2, data, epsilon);

        System.out.println("----------------------------------------");
        System.out.println("Оценка по Рунге погрешности для метода трапеций: ");
        System.out.println("----------------------------------------");

        runge(IntegralSolver::trap, 2, data, epsilon);

        System.out.println("----------------------------------------");
        System.out.println("Оценка по Рунге погрешности для метода Симпсона: ");
        System.out.println("----------------------------------------");

        runge(IntegralSolver::simps, 4, data, epsilon);

    }
}