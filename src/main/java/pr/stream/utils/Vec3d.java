package pr.stream.utils;

import java.util.function.Function;

public final class Vec3d {
    public final double x, y, z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3d(double[] arr) {
        this(arr[0], arr[1], arr[2]);
    }

    public Vec3d add(Vec3d other) {
        if(other == null) return this;
        else return new Vec3d(x + other.x, y + other.y, z + other.z);
    }

    public Vec3d sub(Vec3d other) {
        return new Vec3d(x - other.x, y - other.y, z - other.z);
    }

    public Vec3d mul(double s) {
        return new Vec3d(x * s, y * s, z * s);
    }

    public Vec3d mul(Vec3d s) {
        return new Vec3d(x * s.x, y * s.y, z * s.z);
    }

    public Vec3d div(double s) {
        double invS = 1.0 / s;
        return new Vec3d(x * invS, y * invS, z * invS);
    }

    public double dot(Vec3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3d cross(Vec3d other) {
        return new Vec3d(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vec3d normalize() {
        double len = length();
        if (len == 0.0) return this;
        double invLen = 1.0 / len;
        return new Vec3d(x * invLen, y * invLen, z * invLen);
    }

    public Vec3d clamp(double min, double max)  {
        return new Vec3d(
                Math.clamp(x, min, max),
                Math.clamp(y, min, max),
                Math.clamp(z, min, max)
        );
    }

    public Vec3d forEach(Function<Double, Double> func) {
        return new Vec3d(
                func.apply(x),
                func.apply(y),
                func.apply(z)
        );
    }

    public Vec3d neg() {
        return new Vec3d(
                -x,
                -y,
                -z
        );
    }

    @Override
    public String toString() {
        return String.format("[%.4f, %.4f, %.4f]", x, y, z);
    }
}
