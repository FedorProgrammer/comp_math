public class Solver {
    private static final int ROWS = 4;
    private static final int COLUMNS = 4;
    private static final int VARIANT = 2;

    private static double[][] originalA;
    private static double[] originalB;

    private static double[][] deepCopy(double[][] src) {
        int cols = src.length, rows = src[0].length;
        double[][] dst = new double[cols][rows];
        for (int j = 0; j < cols; j++) {
            System.arraycopy(src[j], 0, dst[j], 0, rows);
        }
        return dst;
    }

    private static double[] deepCopy(double[] src) {
        return src.clone();
    }

    private static double[][] addNoise(double[][] matrix, double order) {
        double[][] matrixErr = deepCopy(matrix);
        for (int j = 0; j < matrixErr.length; j++) {
            for (int i = 0; i < matrixErr[0].length; i++) {
                matrixErr[j][i] += (Math.random() - 0.5) * order;
            }
        }
        return matrixErr;
    }

    private static double[] addNoise(double[] vector, double order) {
        double[] vectorErr = vector.clone();
        for (int i = 0; i < vectorErr.length; i++) {
            vectorErr[i] += (Math.random() - 0.5) * order;
        }
        return vectorErr;
    }

    private static void fill(double[][] matrix, double[] vectorB, int var) {
        for (int i = 0; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[j][i] = Math.random() + (Math.random() * 10) + var;
            }
            vectorB[i] = Math.random() + (Math.random() * 10) + var;
        }
    }

    private static double norm(double[] vecor) {
        double result = 0.0;
        for (int i = 0; i < vecor.length; i++) {
            result += Math.abs(vecor[i]);
        }

        return result;
    }

    private static double norm(double[][] matrix) {
        double result = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < matrix.length; i++) {
            result = Math.max(result, norm(matrix[i]));
        }

        return result;
    }

    private static boolean check(double[][] matrix) {
        return determinant(matrix) == 0.0;
    }

    public static double[] mulMatrixVector(double[][] matrix, double[] vector) {
        if (matrix.length != vector.length) {
            throw new IllegalArgumentException("Невозможно умножить матрицу на вектор!");
        }

        int rows = matrix[0].length;
        int cols = matrix.length;

        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                double v = (j < vector.length) ? vector[j] : 0.0;
                sum += matrix[j][i] * v;
            }
            result[i] = sum;
        }
        return result;
    }

    public static double[][] mulMatrices(double[][] matrixA, double[][] matrixB) {
        int columnsA = matrixA.length;
        int rowsA = matrixA[0].length;
        int columnsB = matrixB.length;
        int rowsB = matrixB[0].length;

        if (columnsA != rowsB) {
            throw new IllegalArgumentException("Невозможно перемножить матрицы!");
        }

        double[][] result = new double[columnsB][rowsA];

        for (int j = 0; j < columnsB; j++) {
            for (int i = 0; i < rowsA; i++) {
                double sum = 0.0;
                for (int k = 0; k < columnsA; k++) {
                    sum += matrixA[k][i] * matrixB[j][k];
                }
                result[j][i] = sum;
            }
        }
        return result;
    }

    public static double[] subVectors(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Невозможно вычесть векторы!");
        }

        int n = vectorA.length;
        double[] res = new double[n];

        for (int i = 0; i < n; i++) {
            res[i] = vectorA[i] - vectorB[i];
        }

        return res;
    }

    public static double[][] subMatrices(double[][] matrixA, double[][] matrixB) {
        int cols = Math.max(matrixA.length, matrixB.length);
        int rows = Math.max(matrixA[0].length, matrixB[0].length);
        double[][] res = new double[cols][rows];

        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                double a = (j < matrixA.length && i < matrixA[0].length) ? matrixA[j][i] : 0.0;
                double b = (j < matrixB.length && i < matrixB[0].length) ? matrixB[j][i] : 0.0;
                res[j][i] = a - b;
            }
        }
        return res;
    }

    public static void show(double[][] matrix, double[] vectorB) {
        int n = matrix[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < matrix.length; j++) {
                double val = matrix[j][i];
                System.out.printf("%8.4f", val);
            }

            if (vectorB != null) {
                double v = vectorB[i];
                if (Math.abs(v) < 1e-8)
                    v = 0.0;
                System.out.printf(" | %8.4f", v);
            }
            System.out.println();
        }
    }

    public static void show(double[] vector) {
        int n = vector.length;
        System.out.printf("(");
        for (int i = 0; i < n; i++) {
            System.out.printf(" %.8f%s ", vector[i], (i == n - 1 ? "" : ","));
        }
        System.out.printf(")^T%n");
    }

    public static double determinant(double[][] matrix) {
        int n = matrix.length;
        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("Матрица должна быть квадратной!");
        }

        if (n == 1)
            return matrix[0][0];

        double det = 0.0;
        for (int c = 0; c < n; c++) {
            double[][] minor = new double[n - 1][n - 1];
            for (int i = 1; i < n; i++) {
                int colIndex = 0;
                for (int j = 0; j < n; j++) {
                    if (j == c)
                        continue;
                    minor[colIndex++][i - 1] = matrix[j][i];
                }
            }
            det += Math.pow(-1, c) * matrix[c][0] * determinant(minor);
        }

        return det;
    }

    public static double[] cramer(double[][] matrix, double[] vectorB) {
        if (check(matrix)) {
            throw new IllegalArgumentException("Вырожденная матрица!");
        }

        double det = determinant(matrix);
        double[] x = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            double[] temp = matrix[i];
            matrix[i] = vectorB;

            x[i] = determinant(matrix) / det;
            matrix[i] = temp;
        }

        return x;
    }

    public static double[][] getInverse(double[][] matrix) {
        if (matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("Матрица должна быть квадратной!");
        }

        if (check(matrix)) {
            throw new IllegalArgumentException("Вырожденная матрица!");
        }

        int n = matrix.length;

        double[][] aug = new double[2 * n][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                aug[j][i] = matrix[j][i];
            }
        }

        for (int j = n; j < 2 * n; j++) {
            for (int i = 0; i < n; i++) {
                aug[j][i] = (j - n == i) ? 1.0 : 0.0;
            }
        }

        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(aug[i][k]) > Math.abs(aug[i][maxRow])) {
                    maxRow = k;
                }
            }

            if (maxRow != i) {
                for (int j = 0; j < 2 * n; j++) {
                    double tmp = aug[j][i];
                    aug[j][i] = aug[j][maxRow];
                    aug[j][maxRow] = tmp;
                }
            }

            double div = aug[i][i];
            for (int j = 0; j < 2 * n; j++) {
                aug[j][i] /= div;
            }

            for (int k = 0; k < n; k++) {
                if (k == i)
                    continue;
                double factor = aug[i][k];
                for (int j = 0; j < 2 * n; j++) {
                    aug[j][k] -= factor * aug[j][i];
                }
            }
        }

        double[][] inversed = new double[n][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                inversed[j][i] = aug[j + n][i];
            }
        }

        return inversed;
    }

    public static double absoluteConditionNumber(double[][] matrix) {
        return norm(getInverse(matrix));
    }

    public static double relativeConditionNumber(double[][] matrix, double[] vectorB) {
        return norm(getInverse(matrix)) * (norm(vectorB) / norm(cramer(matrix, vectorB)));
    }

    public static double standardConditionNumber(double[][] matrix) {
        return norm(getInverse(matrix)) * norm(matrix);
    }

    public static void solve(double[][] matrixA, double[] vectorB, boolean checkError) {
        System.out.println("Расширенная матрица A: ");
        show(matrixA, vectorB);

        System.out.printf("Определитель матрицы det(A): %.4f%n", determinant(matrixA));

        double[] vectorX = cramer(matrixA, vectorB);

        System.out.println("Решение системы Ax = b: ");
        show(vectorX);

        System.out.println("\n--------------------------------\n");

        double[] vectorAX = mulMatrixVector(matrixA, vectorX);
        double[] residual = subVectors(vectorB, vectorAX);

        System.out.println("Проверка решения: ");

        System.out.printf("Вектор Ax: ");
        show(vectorAX);

        System.out.printf("Вектор b: ");
        show(vectorB);

        System.out.printf("Невязка r = b - Ax: ");
        show(residual);

        System.out.println("\n--------------------------------\n");

        double absoluteCond = absoluteConditionNumber(matrixA);
        double relativeCond = relativeConditionNumber(matrixA, vectorB);
        double standardCond = standardConditionNumber(matrixA);

        System.out.printf("Абсолютное число обусловленности: %.8f%n", absoluteCond);
        System.out.printf("Естественное число обусловленности: %.8f%n", relativeCond);
        System.out.printf("Стандартное число обусловленности: %.8f%n", standardCond);

        if (checkError) {
            System.out.println("\n--------------------------------\n");

            double[] originalX = cramer(originalA, originalB);

            double deltaX = norm(subVectors(originalX, vectorX)) / norm(originalX);
            double deltaA = norm(subMatrices(originalA, matrixA)) / norm(originalA);
            double deltaB = norm(subVectors(originalB, vectorB)) / norm(originalB);

            double[][] inversedA = getInverse(matrixA);
            double[][] matrixIdentity = mulMatrices(inversedA, matrixA);

            double condA = norm(inversedA) * norm(matrixA);

            System.out.println("Оценка естественного числа обусловленности:");

            System.out.println();

            System.out.println("Обратная к A матрица:");

            show(inversedA, null);

            System.out.println("Проверка обратимости:");

            System.out.println("A * (A)^(-1): ");
            show(matrixIdentity, null);

            System.out.println();

            System.out.printf("cond(A) = %f%n", condA);
            System.out.printf("δ(X*) = ||X - X*|| / ||X|| = %.8f%n", deltaX);
            System.out.printf("δ(A*) = ||A - A*|| / ||A|| = %.8f%n", deltaA);
            System.out.printf("δ(B*) = ||B - B*|| / ||B|| = %.8f%n", deltaB);

            System.out.println();

            System.out.println("Проверяем условие: cond(A) >= δ(X) / (δ(A*) + δ(B*))");
            System.out.printf("%.8f V %.8f%n", condA, deltaX / (deltaA + deltaB));
            System.out.printf("%s%n",
                    condA >= deltaX / (deltaA + deltaB) ? "Условие выполняется!" : "Условие НЕ выполняется!");
        }
    }

    public static void main(String[] args) {
        double[][] matrixA = new double[COLUMNS][ROWS];
        double[] vectorB = new double[ROWS];
        double order = 1e-1;

        fill(matrixA, vectorB, VARIANT);

        originalA = deepCopy(matrixA);
        originalB = deepCopy(vectorB);

        try {
            System.out.println("======= РЕШЕНИЕ С ОРИГИНАЛЬНЫМИ МАТРИЦЕЙ A И ВЕКТОРОМ B =======");

            solve(matrixA, vectorB, false);

            System.out.println("\n================================\n");

            System.out.println("======= РЕШЕНИЕ С ОШИБКАМИ В ВЕКТОРЕ B =======");

            double[] vectorBErr = addNoise(vectorB, order);
            solve(matrixA, vectorBErr, true);

            System.out.println("\n================================\n");

            System.out.println("======= РЕШЕНИЕ С ОШИБКАМИ В МАТРИЦЕ A =======");

            double[][] matrixAErr = addNoise(matrixA, order);
            solve(matrixAErr, vectorB, true);

            System.out.println("\n================================\n");

            System.out.println("======= РЕШЕНИЕ С ОШИБКАМИ В МАТРИЦЕ A И В ВЕКТОРЕ B =======");

            solve(matrixAErr, vectorBErr, true);

            System.out.println("\n================================\n");

            double[][] hilbertMatrixA = originalA;
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (i == j) {
                        hilbertMatrixA[j][i] = 1.0 / ((i + 1) + (j + 1) - 1);
                    }
                }
            }

            originalA = deepCopy(hilbertMatrixA);

            System.out.println("======= РЕШЕНИЕ С ОРИГИНАЛЬНЫМИ МАТРИЦЕЙ ГИЛЬБЕРТА A И ВЕКТОРОМ B =======");

            solve(hilbertMatrixA, vectorB, false);

            System.out.println("\n================================\n");

            System.out.println("======= РЕШЕНИЕ С ОШИБКАМИ В ВЕКТОРЕ B =======");

            solve(hilbertMatrixA, vectorBErr, true);

            System.out.println("\n================================\n");

            System.out.println("======= РЕШЕНИЕ С ОШИБКАМИ В МАТРИЦЕ ГИЛЬБЕРТА A =======");

            double[][] hilbertMatrixAErr = addNoise(hilbertMatrixA, order);
            solve(hilbertMatrixAErr, vectorB, true);

            System.out.println("\n================================\n");

            System.out.println("======= РЕШЕНИЕ С ОШИБКАМИ В МАТРИЦЕ A И В ВЕКТОРЕ B =======");

            solve(hilbertMatrixAErr, vectorBErr, true);

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }
}
