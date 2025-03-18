public class NewtonMethod {

    public static class NewtonMethodException extends Exception {
        public NewtonMethodException(String message) {
            super(message);
        }
    }

    public static Result solve(double x0, double eps) throws NewtonMethodException {
        if (eps <= 0) {
            throw new NewtonMethodException("Точность должна быть положительным числом");
        }

        if (x0 < Function.LEFT_BOUND || x0 > Function.RIGHT_BOUND) {
            throw new NewtonMethodException("Начальное приближение должно быть в пределах от " +
                    Function.LEFT_BOUND + " до " + Function.RIGHT_BOUND);
        }

        double epsilon = Math.sqrt(2 * eps * Function.FIRST_DERIVATIVE_MIN / Function.SECOND_DERIVATIVE_MAX);

        int iteration = 0;
        
        double x = x0;
        double xNext;
        double diff;

        Interval interval = new Interval(x0, x0);

        do {
            xNext = x - Function.f(x) / Function.firstDerivative(x);
            diff = Math.abs(xNext - x);

            interval.a = Math.min(x, xNext);
            interval.b = Math.max(x, xNext);

            x = xNext;
            iteration++;
        } while (diff >= epsilon);

        return new Result(xNext, iteration, interval);
    }

    public static Result harwik(double x0, int maxIterations) throws NewtonMethodException {

        if (x0 < Function.LEFT_BOUND || x0 > Function.RIGHT_BOUND) {
            throw new NewtonMethodException("Начальное приближение должно быть в пределах от " +
                    Function.LEFT_BOUND + " до " + Function.RIGHT_BOUND);
        }

        int iteration = 0;

        double x = x0;
        double prevDiff = Double.POSITIVE_INFINITY;

        Interval interval = new Interval(x0, x0);

        while (iteration < maxIterations) {
            double xNext = x - Function.f(x) / Function.firstDerivative(x);
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

        throw new NewtonMethodException(
                "Максимальное число итераций для получения интеравала неопределенности должно быть больше "
                        + maxIterations);
    }

    public static void main(String[] args) {
        double initialGuess = 0.7;
        double epsilon = 1e-6;
        int N = 100;

        try {
            System.out.println("Метод Ньютона для нахождения корня функции");

            Result result = solve(initialGuess, epsilon);
            System.out.println("Используемое epsilon: " + epsilon);
            System.out.println("Найденный корень: " + result.root);
            System.out.println("Число итераций: " + result.iterations);
            System.out.println("Последний интервал: [" + result.interval.a + ", " + result.interval.b + "]");

            System.out.println("----------------------------------------");

            double delta = Math.abs(Function.f(result.interval.b) - Function.f(result.interval.a));
            System.out.println("ν: " + epsilon / delta);
            System.out.println("ν_Δ: " + 1 / Math.abs(Function.firstDerivative(result.root)));

            System.out.println("----------------------------------------");

            System.out.println("Метод Ньютона с проверкой на разболтовку (Harwik)");

            Result harwikResult = harwik(initialGuess, N);
            System.out.println("Найденный корень: " + harwikResult.root);
            System.out.println("Начало разболтовки: " + harwikResult.iterations + " итерация");
            System.out.println(
                    "Интервал неопределенности: [" + harwikResult.interval.a + ", " + harwikResult.interval.b + "]");
        } catch (NewtonMethodException e) {
            System.err.println(e.getMessage());
        }
    }
}