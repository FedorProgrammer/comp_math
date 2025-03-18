public class SimpleIterationMethod {

    public static class SimpleIterationException extends Exception {
        public SimpleIterationException(String message) {
            super(message);
        }
    }

    public static Result solve(double x0, double eps) throws SimpleIterationException {
        if (eps <= 0) {
            throw new SimpleIterationException("epsilon должен быть положительным числом");
        }

        if (x0 < Function.LEFT_BOUND || x0 > Function.RIGHT_BOUND) {
            throw new SimpleIterationException("Начальное приближение должно быть в пределах от " +
                    Function.LEFT_BOUND + " до " + Function.RIGHT_BOUND);
        }

        double q = Math.abs(derivativeOfPhi(x0));
        if (q >= 1) {
            throw new SimpleIterationException("Условие сходимости не выполняется: |φ'(x)| = " + q + " >= 1");
        }

        double epsilon;
        int iteration = 0;

        double x = x0;
        double xNext;
        double diff;

        Interval interval = new Interval(x0, x0);

        do {
            xNext = phi(x);
            diff = Math.abs(xNext - x);

            interval.a = Math.min(x, xNext);
            interval.b = Math.max(x, xNext);

            q = Math.abs(derivativeOfPhi(x));
            epsilon = ((1 - q) / q) * eps;

            x = xNext;
            iteration++;
        } while (diff >= epsilon);

        return new Result(xNext, iteration, interval);
    }

    public static Result harwik(double x0, int maxIterations) throws SimpleIterationException {
        int iteration = 0;

        double x = x0;
        double prevDiff = Double.POSITIVE_INFINITY;

        Interval interval = new Interval(x0, x0);

        while (iteration < maxIterations) {
            double xNext = phi(x);
            double currentDiff = Math.abs(xNext - x);

            interval.a = Math.min(x, xNext);
            interval.b = Math.max(x, xNext);

            if (currentDiff >= prevDiff) {
                return new Result(x, iteration, interval);
            }

            x = xNext;
            prevDiff = currentDiff;
            iteration++;
        }

        throw new SimpleIterationException(
                "Максимальное число итераций для получения интеравала неопределенности должно быть больше "
                        + maxIterations);
    }

    private static double phi(double x) {
        double alpha = 2 / (Function.FIRST_DERIVATIVE_MIN + Function.FIRST_DERIVATIVE_MAX);
        return x - alpha * Function.f(x);
    }

    private static double derivativeOfPhi(double x) {
        double alpha = 2 / (Function.FIRST_DERIVATIVE_MIN + Function.FIRST_DERIVATIVE_MAX);
        return 1 - alpha * Function.firstDerivative(x);
    }

    public static void main(String[] args) {
        double initialGuess = 0.7;
        double epsilon = 1e-12;
        int N = 150;

        try {
            System.out.println("Метод простых итераций для нахождения корня функции");

            Result result = solve(initialGuess, epsilon);
            System.out.println("Используемое epsilon: " + epsilon);
            System.out.println("Найденный корень: " + result.root);
            System.out.println("Число итераций: " + result.iterations);
            System.out.println("Последний интервал: [" + result.interval.a + ", " + result.interval.b + "]");

            System.out.println("----------------------------------------");

            double delta = Math.abs(Function.f(result.interval.b) - Function.f(result.interval.a));
            System.out.println("ν: " + epsilon / delta);
            System.out.println("ν_Δ: " + 1 / (1 - Math.abs(derivativeOfPhi(result.root))));

            System.out.println("----------------------------------------");

            System.out.println("Метод простых итераций с проверкой на разболтовку (Harwik)");

            Result harwikResult = harwik(initialGuess, N);
            System.out.println("Найденный корень: " + harwikResult.root);
            System.out.println("Начало разболтовки: " + harwikResult.iterations + " итерация");
            System.out.println(
                    "Интервал неопределенности: [" + harwikResult.interval.a + ", " + harwikResult.interval.b + "]");

            System.out.println("----------------------------------------");

            System.out.println("  eps   |  delta  |        x0          | N |   ν_Δ   |    ν");

            for (double eps = 0.00001; eps < 1; eps *= 10) {
                Result res = solve(initialGuess, eps);

                delta = Math.abs(Function.f(res.interval.b) - Function.f(res.interval.a));
                double nu = eps / delta;
                double nu_delta = Math.abs(1 / Math.abs(Function.firstDerivative(res.root)));

                System.out.printf("%.5f | %.5f | %.16f | %d | %.5f | %.5f%n",
                        eps, delta, res.root, res.iterations, nu_delta, nu);
            }

        } catch (SimpleIterationException e) {
            System.err.println(e.getMessage());
        }
    }
}