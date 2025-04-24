public class Task {
    public static final double[] X = {
            1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0,
            10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0
    };

    public static final double[] Y = {
            1.2998, -0.6984, -0.9958, -0.8038, -0.7053, -0.8225, -1.0023, -0.9969, -0.6222, 0.1277,
            1.0290, 1.6596, 1.5334, 0.3572, -1.5693, -2.7917, 0.0435
    };

    // Вспомогательная формула для интерполяции Лагранжа
    private static double lagrangeBasis(double[] x, int i, double xi) {
        double result = 1.0;
        for (int j = 0; j < x.length; j++) {
            if (j != i) {
                result *= (xi - x[j]) / (x[i] - x[j]);
            }
        }
        return result;
    }

    // Интерполяция Лагранжа
    public static double lagrangeInterpolation(double[] x, double[] y, double xi) {
        double result = 0.0;
        for (int i = 0; i < x.length; i++) {
            result += y[i] * lagrangeBasis(x, i, xi);
        }
        return result;
    }

    // Вспомогательные методы для Ньютона
    private static long factorial(int n) {
        return (n <= 1) ? 1 : n * factorial(n - 1);
    }

    private static double[][] computeFiniteDifferences(double[] y) {
        int n = y.length;
        double[][] differences = new double[n][n];
        for (int i = 0; i < n; i++) {
            differences[0][i] = y[i];
        }

        for (int k = 1; k < n; k++) {
            for (int i = 0; i < n - k; i++) {
                differences[k][i] = differences[k - 1][i + 1] - differences[k - 1][i];
            }
        }
        return differences;
    }

    // Первый интерполяционный многочлен Ньютона
    public static double firstNewtonInterpolation(double[] x, double[] y, double xi) {
        int n = x.length;
        double h = x[1] - x[0];
        double[][] finiteDifferences = computeFiniteDifferences(y);
        double result = y[0];
        double product = 1.0;
        for (int i = 1; i < n; i++) {
            double ai = finiteDifferences[i][0] / (factorial(i) * Math.pow(h, i));
            product *= (xi - x[i - 1]);

            result += ai * product;
        }
        return result;
    }

    // Второй интерполяционный многочлен Ньютона
    public static double secondNewtonInterpolation(double[] x, double[] y, double xi) {
        int n = x.length;
        double h = x[1] - x[0];
        double[][] finiteDifferences = computeFiniteDifferences(y);
        double result = y[n - 1];
        double product = 1.0;
        for (int i = 1; i < n; i++) {
            double ai = (finiteDifferences[i][n - i - 1] / (factorial(i) * Math.pow(h, i)));
            product *= (xi - x[n - i]);

            result += ai * product;
        }
        return result;
    }

    // Линейная аппроксимация: y = a * x + b
    public static double[] linearApproximation(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }
        double a = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - a * sumX) / n;
        return new double[] { a, b };
    }

    // Показательная аппроксимация: y = a * (b ^ x)
    public static double[] exponentialApproximation(double[] x, double[] y) {
        double[] lnY = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            if (y[i] <= 0) {
                throw new IllegalArgumentException("Значения y должны быть положительными!");
            }
            lnY[i] = Math.log(y[i]);
        }
        double[] coefficients = linearApproximation(x, lnY);
        return new double[] { Math.exp(coefficients[1]), Math.exp(coefficients[0]) };
    }

    // Экспоненциальная аппроксимация: y = exp(a + bx)
    public static double[] expApproximation(double[] x, double[] y) {
        double[] lnY = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            if (y[i] <= 0) {
                throw new IllegalArgumentException("Значения y должны быть положительными!");
            }
            lnY[i] = Math.log(y[i]);
        }
        double[] coefficients = linearApproximation(x, lnY);
        return new double[] { coefficients[1], coefficients[0] };
    }

    // Оценка аппроксимации (R^2)
    public static double calculateRSquared(double[] y, double[] yPredicted) {
        double avgY = average(y);
        double denominator = 0, numerator = 0;
        for (int i = 0; i < y.length; i++) {
            numerator += Math.pow(y[i] - yPredicted[i], 2);
            denominator += Math.pow(y[i] - avgY, 2);
        }
        return 1 - (numerator / denominator);
    }

    // Среднее значение
    public static double average(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    // Дисперсия
    public static double variance(double[] values) {
        double sum = 0;
        double average = average(values);
        for (double value : values) {
            sum += Math.pow(value - average, 2);
        }
        return sum / values.length;
    }

    // Ковариация
    public static double covariance(double[] x, double[] y) {
        double avgX = average(x), avgY = average(y);
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - avgX) * (y[i] - avgY);
        }
        return sum / (x.length - 1);
    }

    // Корреляция
    public static double correlation(double[] x, double[] y) {
        return covariance(x, y) / (Math.sqrt(variance(x)) * Math.sqrt(variance(y)));
    }

    // Проверка степени корреляции
    public static boolean isCorrelationMajor(double r, int n) {
        return Math.abs(r) * Math.sqrt(n - 1) >= 3;
    }

    // Линейная регрессия y = a*x + b
    public static double[] linearRegressionYX(double[] x, double[] y) {
        double a = correlation(x, y) * (Math.sqrt(variance(y)) / Math.sqrt(variance(x)));
        double b = average(y) - a * average(x);
        return new double[] { a, b };
    }

    // Линейная регрессия x = c*y + d
    public static double[] linearRegressionXY(double[] x, double[] y) {
        double[] coefficients = linearRegressionYX(y, x);
        return new double[] { coefficients[0], coefficients[1] };
    }

    // Функции для получения y-состовляющих соответствующих аппроксимаций
    // Для линейной аппроксимации
    private static double[] predictLinear(double[] x, double[] coeff) {
        double[] predicted = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            predicted[i] = coeff[0] * x[i] + coeff[1];
        }
        return predicted;
    }

    // Для показательной аппроксимации
    private static double[] predictExponential(double[] x, double[] coeff) {
        double[] predicted = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            predicted[i] = coeff[0] * Math.pow(coeff[1], x[i]);
        }
        return predicted;
    }

    // Для экспоненциальной аппроксимации
    private static double[] predictExpApprox(double[] x, double[] coeff) {
        double[] predicted = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            predicted[i] = Math.exp(coeff[0] + coeff[1] * x[i]);
        }
        return predicted;
    }

    public static void main(String[] args) {
        double x1 = 1.5, x2 = 16.5;

        // Интерполяция
        System.out.println("--------------------------------");
        System.out.println("Интерполяция: ");
        System.out.println("--------------------------------");

        System.out.println("Лагранж:");
        System.out.printf("f(x=%.2f) = %.16f\n", x1, lagrangeInterpolation(X, Y, x1));
        System.out.printf("f(x=%.2f) = %.16f\n", x2, lagrangeInterpolation(X, Y, x2));

        System.out.println("--------------------------------");

        System.out.println("Ньютон (прямой ход):");
        System.out.printf("f(x=%.2f) = %.16f\n", x1, firstNewtonInterpolation(X, Y, x1));
        System.out.printf("f(x=%.2f) = %.16f\n", x2, firstNewtonInterpolation(X, Y, x2));

        System.out.println("--------------------------------");

        System.out.println("Ньютон (обратный ход):");
        System.out.printf("f(x=%.2f) = %.16f\n", x1, secondNewtonInterpolation(X, Y, x1));
        System.out.printf("f(x=%.2f) = %.16f\n", x2, secondNewtonInterpolation(X, Y, x2));

        System.out.println("--------------------------------\n");

        // Аппроксимация
        System.out.println("--------------------------------");
        System.out.println("Аппроксимация: ");
        System.out.println("--------------------------------");

        double[] linear = linearApproximation(X, Y);
        System.out.println("Линейная: y = " + linear[0] + "x + (" + linear[1] + ")");
        System.out.printf("f(x=%.2f) = %.16f\n", x1, linear[0] * x1 + linear[1]);
        System.out.printf("f(x=%.2f) = %.16f\n", x2, linear[0] * x2 + linear[1]);
        double[] yPredictedLinear = predictLinear(X, linear);
        System.out.printf("R^2: %.16f\n", calculateRSquared(Y, yPredictedLinear));

        System.out.println("--------------------------------");

        try {
            double[] exp = exponentialApproximation(X, Y);
            System.out.println("Показательная: y = " + exp[0] + " * " + exp[1] + "^x");
            System.out.printf("f(x=%.2f) = %.16f\n", x1, exp[0] * Math.pow(exp[1], x1));
            System.out.printf("f(x=%.2f) = %.16f\n", x2, exp[0] * Math.pow(exp[1], x2));
            double[] yPredictedExp = predictExponential(X, exp);
            System.out.printf("R^2: %.6f\n", calculateRSquared(Y, yPredictedExp));
        } catch (IllegalArgumentException e) {
            System.out.println("Показательная аппроксимация невозможна: " + e.getMessage());
        }

        System.out.println("--------------------------------");

        try {
            double[] expApprox = expApproximation(X, Y);
            System.out.println("Экспоненциальная: y = e^(" + expApprox[0] + "x + (" + expApprox[1] + ")");
            System.out.printf("f(x=%.2f) = %.16f\n", x1, Math.exp(expApprox[0] * x1 + expApprox[1]));
            System.out.printf("f(x=%.2f) = %.16f\n", x2, Math.exp(expApprox[0] * x2 + expApprox[1]));
            double[] yPredictedExpApprox = predictExpApprox(X, expApprox);
            System.out.printf("R^2: %.6f\n", calculateRSquared(Y, yPredictedExpApprox));
        } catch (IllegalArgumentException e) {
            System.out.println("Экспоненциальная аппроксимация невозможна: " + e.getMessage());
        }

        System.out.println("--------------------------------\n");

        // Регрессии
        System.out.println("--------------------------------");
        System.out.println("Регрессионный анализ: ");
        System.out.println("--------------------------------");

        double[] regYX = linearRegressionYX(X, Y);
        System.out.println("Линейная регрессия Y(X): y = " + regYX[0] + "x + (" + regYX[1] + ")");
        System.out.printf("f(x=%.2f) = %.16f\n", x1, regYX[0] * x1 + regYX[1]);
        System.out.printf("f(x=%.2f) = %.16f\n", x2, regYX[0] * x2 + regYX[1]);

        System.out.println("--------------------------------");

        double[] regXY = linearRegressionXY(X, Y);
        System.out.println("Линейная регрессия X(Y): x = " + regXY[0] + "y + (" + regXY[1] + ")");
        System.out.printf("f(y=%.6f) = %.16f\n", Y[0], regXY[0] * Y[0] + regXY[1]);
        System.out.printf("f(y=%.6f) = %.16f\n", Y[Y.length - 1], regXY[0] * Y[Y.length - 1] + regXY[1]);

        System.out.println("--------------------------------\n");

        // Статистика
        System.out.println("--------------------------------");
        System.out.println("Статистические параметры: ");
        System.out.println("--------------------------------");

        System.out.printf("Среднее X = %.16f%n", average(X));
        System.out.printf("Среднее Y = %.16f%n", average(Y));
        System.out.printf("Дисперсия X = %.16f%n", variance(X));
        System.out.printf("Дисперсия Y = %.16f%n", variance(Y));
        System.out.printf("Ковариация = %.16f%n", covariance(X, Y));
        System.out.printf("Корреляция = %.16f%n", correlation(X, Y));
        System.out.printf("Зависит ли X от Y? %b%n", isCorrelationMajor(correlation(X, Y), X.length));

        System.out.println("--------------------------------");
    }
}