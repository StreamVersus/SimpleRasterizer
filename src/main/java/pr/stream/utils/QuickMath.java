package pr.stream.utils;

public class QuickMath {
    public static Vec3d refract(Vec3d i, Vec3d n, double ior) {
        double c1 = -n.dot(i);
        double c2sqr = 1 - ior * ior * (1 - c1*c1);

        if(c2sqr < 0) return reflect(i, n);
        return i.mul(ior).add(n.mul(ior * c1 - Math.sqrt(c2sqr))).normalize();
    }

    public static Vec3d reflect(Vec3d i, Vec3d n) {
        return i.sub(n.mul(2 * n.dot(i))).normalize();
    }

    public static double[][] multiply(double[][] firstMatrix, double[][] secondMatrix) {
        double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }

        return result;
    }

    private static double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    public static double schlick(double cosTheta, double r0) {
        return r0 + (1 - r0) * Math.pow(1 - cosTheta, 5);
    }
}
