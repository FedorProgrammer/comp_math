public class Solver {
    public static double determinant(Matrix matrix) {
        int rows = matrix.rows;
        int columns = matrix.columns;

        if (rows != columns) {
            throw new IllegalArgumentException("Матрица должна быть квадратной!");
        }

        double[][] temp = new double[columns][rows];
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                temp[j][i] = matrix.coeff[j][i];
            }
        }

        double det = 1.0;
        int swaps = 0;

        for (int i = 0; i < rows; i++) {
            int maximal = i;
            for (int k = i + 1; k < rows; k++) {
                if (Math.abs(temp[i][k]) > Math.abs(temp[i][maximal])) {
                    maximal = k;
                }
            }

            if (Math.abs(temp[i][maximal]) < 1e-12) {
                return 0.0;
            }

            if (i != maximal) {
                for (int j = 0; j < columns; j++) {
                    double t = temp[j][i];
                    temp[j][i] = temp[j][maximal];
                    temp[j][maximal] = t;
                }
                swaps++;
            }

            for (int k = i + 1; k < rows; k++) {
                double factor = temp[i][k] / temp[i][i];
                for (int j = i; j < columns; j++) {
                    temp[j][k] -= factor * temp[j][i];
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            det *= temp[i][i];
        }

        if (swaps % 2 != 0) {
            det *= -1;
        }

        return det;
    }

    public static double[] cramer(Matrix matrix) {
        double[] x = new double[matrix.rows];

        double det = determinant(matrix);

        for (int i = 0; i < matrix.rows; i++) {
            double[] temp = matrix.coeff[i];
            matrix.coeff[i] = matrix.vector;

            x[i] = determinant(matrix) / det;
            matrix.coeff[i] = temp;
        }

        return x;
    }

    public static boolean isDegeneracy(Matrix matrix) {
        return determinant(matrix) == 0.0;
    }

    public static double norm(double[] vecor) {
        double result = 0.0;
        for (int i = 0; i < vecor.length; i++) {
            result += Math.abs(vecor[i]);
        }

        return result;
    }

    public static double norm(Matrix matrix) {
        double result = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < matrix.columns; i++) {
            result = Math.max(result, norm(matrix.coeff[i]));
        }

        return result;
    }

    public static Matrix getInverse(Matrix matrix) {
        int n = matrix.rows;
        if (n != matrix.columns) {
            throw new IllegalArgumentException("Матрица должна быть квадратной!");
        }

        double[][] aug = new double[2 * n][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                aug[j][i] = matrix.coeff[j][i];
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

            if (Math.abs(aug[i][i]) < 1e-12) {
                throw new IllegalArgumentException("Матрица вырождена, обратной не существует!");
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

        Matrix inv = new Matrix(n, n);
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                inv.coeff[j][i] = aug[j + n][i];
            }
        }

        return inv;
    }

    public static double absoluteConditionNumber(Matrix matrix) {
        return norm(getInverse(matrix));
    }

    public static double relativeConditionNumber(Matrix matrix) {
        return norm(getInverse(matrix)) * (norm(matrix.vector) / norm(cramer(matrix)));
    }

    public static double standardConditionNumber(Matrix matrix) {
        return norm(getInverse(matrix)) * norm(matrix);
    }

    public static double evaluateStandardConditionNumber(Matrix matrix) {
        // TODO: make delta(X*) and delta(B*)
    }

    public static void main(String[] args) {

    }
}
