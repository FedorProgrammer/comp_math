public class Function {
    /*
     * FIRST_DERIVATIVE_MAX = -8.734497384116917
     * FIRST_DERIVATIVE_MIN = -12.029062237402464
     * --------------------
     * SECOND_DERIVATIVE_MAX = -18.595369901547173
     * SECOND_DERIVATIVE_MIN = -91.94096038535571
     */

    public static double LEFT_BOUND = 0.7;
    public static double RIGHT_BOUND = 0.75;

    public static double FIRST_DERIVATIVE_MAX = firstDerivative(LEFT_BOUND);
    public static double FIRST_DERIVATIVE_MIN = firstDerivative(RIGHT_BOUND);

    public static double SECOND_DERIVATIVE_MAX = secondDerivative(RIGHT_BOUND);
    public static double SECOND_DERIVATIVE_MIN = secondDerivative(LEFT_BOUND);

    public static double f(double x) {
        double sin2x = Math.sin(2 * x);
        double cos8x5 = Math.cos(8 * Math.pow(x, 5));

        double term1 = Math.pow(sin2x, 3);
        double term2 = cos8x5;

        return term1 * term2;
    }

    public static double firstDerivative(double x) {
        double sin2x = Math.sin(2 * x);
        double cos2x = Math.cos(2 * x);
        double sin8x5 = Math.sin(8 * Math.pow(x, 5));
        double cos8x5 = Math.cos(8 * Math.pow(x, 5));

        double term1 = 6 * Math.pow(sin2x, 2) * cos2x * cos8x5;
        double term2 = -40 * Math.pow(x, 4) * Math.pow(sin2x, 3) * sin8x5;

        return term1 + term2;
    }

    public static double secondDerivative(double x) {
        double sin2x = Math.sin(2 * x);
        double cos2x = Math.cos(2 * x);
        double sin8x5 = Math.sin(8 * Math.pow(x, 5));
        double cos8x5 = Math.cos(8 * Math.pow(x, 5));

        double term1 = 24 * sin2x * Math.pow(cos2x, 2) * cos8x5;
        double term2 = -12 * Math.pow(sin2x, 3) * cos8x5;
        double term3 = -160 * Math.pow(x, 3) * Math.pow(sin2x, 3) * sin8x5;
        double term4 = -480 * Math.pow(x, 4) * Math.pow(sin2x, 2) * cos2x * sin8x5;
        double term5 = -1600 * Math.pow(x, 8) * Math.pow(sin2x, 3) * cos8x5;

        return term1 + term2 + term3 + term4 + term5;
    }
}
