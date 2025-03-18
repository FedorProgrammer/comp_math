public class SecantMethod {

    public static class SecantMethodException extends Exception {
        public SecantMethodException(String message) {
            super(message);
        }
    }

    public static Result solve(double epsilon) throws SecantMethodException {
        if (epsilon <= 0) {
            throw new SecantMethodException("epsilon должен быть положительным числом");
        }

        int iteration = 0;

        double a = Function.LEFT_BOUND;
        double b = Function.RIGHT_BOUND;
        double c;

        do {
            c = a - (Function.f(a) * (b - a)) / (Function.f(b) - Function.f(a));

            if (Function.f(a) * Function.f(c) < 0) {
                b = c;
            } else {
                a = c;
            }

            iteration++;
        } while (Function.f(c) >= epsilon);

        return new Result(c, iteration, new Interval(Math.min(a, b), Math.max(a, b)));
    }

    public static Result harwik(int maxIterations) throws SecantMethodException {
        if (maxIterations <= 0) {
            throw new SecantMethodException("Максимальное число итераций должно быть положительным");
        }

        int iteration = 0;

        double a = Function.LEFT_BOUND;
        double b = Function.RIGHT_BOUND;

        double prevDiff = Double.POSITIVE_INFINITY;

        while (iteration < maxIterations) {
            double c = a - (Function.f(a) * (b - a)) / (Function.f(b) - Function.f(a));

            if (Function.f(a) * Function.f(c) < 0) {
                b = c;
            } else {
                a = c;
            }

            double currentDiff = Math.abs(b - a);
            if (currentDiff >= prevDiff) {
                return new Result(c, iteration, new Interval(Math.min(a, b), Math.max(a, b)));
            }

            prevDiff = currentDiff;
            iteration++;
        }

        throw new SecantMethodException(
                "Максимальное число итераций для получения интеравала неопределенности должно быть больше "
                        + maxIterations);
    }

    public static void main(String[] args) {
        double epsilon = 1e-6;
        int maxIterations = 100;

        try {
            System.out.println("Метод хорд для нахождения корня функции");

            Result result = solve(epsilon);
            System.out.println("Используемый epsilon: " + epsilon);
            System.out.println("Найденный корень: " + result.root);
            System.out.println("Число итераций: " + result.iterations);

            System.out.println("----------------------------------------");

            double delta = Math.abs(Function.f(result.interval.b) - Function.f(result.interval.a));
            System.out.println("ν: " + epsilon / delta);
            System.out.println("ν_Δ: " + 1 / Math.abs(Function.firstDerivative(result.root)));

            System.out.println("----------------------------------------");

            System.out.println("Метод хорд с проверкой на разболтовку (Harwik)");

            Result harwikResult = harwik(maxIterations);
            System.out.println("Найденный корень: " + harwikResult.root);
            System.out.println("Разболтовка началась на " + harwikResult.iterations + " итерации");
            System.out.println(
                    "Интервал неопределенности: [" + harwikResult.interval.a + ", " + harwikResult.interval.b + "]");
        } catch (SecantMethodException e) {
            System.err.println(e.getMessage());
        }
    }
}