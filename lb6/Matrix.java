public class Matrix {
    public int rows;
    public int columns;
    public double[][] coeff;
    public double[] vector;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.coeff = new double[columns][rows];
        this.vector = new double[rows];
    }

    public void fill() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                coeff[j][i] = Math.random() + (i * rows + j);
            }
        }
    }
}