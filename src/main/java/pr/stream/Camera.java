package pr.stream;

import pr.stream.RenderObjects.RenderObject;
import pr.stream.utils.RawIntersection;
import pr.stream.utils.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pr.stream.Main.*;
import static pr.stream.utils.QuickMath.*;

public class Camera {
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public final Vec3d pos, rot, light;
    private final double[][] R;
    private final int width, height;
    private final double tanFovX, tanFovY;

    public Camera(double[] pos, double[] rot, double[] light, int fov, int width, int height) {
        this.pos = new Vec3d(pos);
        this.rot = new Vec3d(rot);
        this.light = new Vec3d(light);
        this.width = width;
        this.height = height;

        double aspectRatio = (double) width / (double) height;
        tanFovY = Math.tan(Math.toRadians((double) fov / 2));
        tanFovX = aspectRatio * tanFovY;

        R = buildRotMatrix();
    }
    public double toNDCx(int x) {
        return ((double) (x * 2) / width) - 1;
    }
    public double toNDCy(int y) {
        return 1 - ((double) (y * 2) / height);
    }

    public Vec3d directionVector(double x, double y) {
        var forward = getForward();
        var right = getRight();
        var up = getUp();

        // forward + NDC.X * tanFovX * right + NDC.Y * tanFovY * up
        return forward.add(right.mul(x * tanFovX)).add(up.mul(y * tanFovY)).normalize();
    }

    private double[][] buildRotMatrix() {
        double yaw = Math.toRadians(rot.x);
        double pitch = Math.toRadians(rot.y);
        double roll = Math.toRadians(rot.z);

        double[][] Rx = rotateX(pitch);
        double[][] Ry = rotateY(yaw);
        double[][] Rz = rotateZ(roll);

        return multiply(multiply(Ry, Rz), Rx);
    }

    double[][] rotateX(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new double[][]{
                {1, 0, 0},
                {0, c, -s},
                {0, s, c}
        };
    }

    double[][] rotateY(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new double[][]{
                {c, 0, s},
                {0, 1, 0},
                {-s, 0, c}
        };
    }

    double[][] rotateZ(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new double[][]{
                {c, -s, 0},
                {s, c, 0},
                {0, 0, 1}
        };
    }

    Vec3d getForward() {
        double[] forward = new double[3];
        for (int i = 0; i < 3; i++) {
            forward[i] = R[i][0];
        }
        return new Vec3d(forward);
    }

    Vec3d getRight() {
        double[] right = new double[3];
        for (int i = 0; i < 3; i++) {
            right[i] = R[i][1];
        }

        return new Vec3d(right);
    }

    Vec3d getUp() {
        double[] up = new double[3];
        for (int i = 0; i < 3; i++) {
            up[i] = R[i][2];
        }
        return new Vec3d(up);
    }

    public RawIntersection getClosestIntersection(RenderObject[] objs, Vec3d d, Vec3d origin) {
        RawIntersection is = null;
        double t = Integer.MAX_VALUE;
        for (RenderObject obj : objs) {
            var i = obj.checkInterception(d, origin, light);
            if(i == null) continue;
            if(i.t < t) {
                is = i;
                t = i.t;
            }
        }

        return is;
    }

    public void renderScene(RenderObject[] objs) {
        Vec3d[] screen = new Vec3d[(HEIGHT + 1) * (WIDTH + 1)];
        List<Callable<Void>> callList = new ArrayList<>();
        for(int x = 0; x <= WIDTH; x++) {
            double NDCx = toNDCx(x);
            int finalX = x;
            callList.add(() -> {
                try {
                    for (int y = 0; y <= HEIGHT; y++) {
                        var is = getClosestIntersection(objs, directionVector(NDCx, toNDCy(y)), pos);
                        if (is != null) {
                            var c = directIllumination(is.build(light), 0);
                            screen[y * WIDTH + finalX] = c;
                        }
                    }
                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            });
        }
        try {
            executor.invokeAll(callList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onFrameEnd(screen);
    }

    public Vec3d getLight() {
        return light;
    }
}
