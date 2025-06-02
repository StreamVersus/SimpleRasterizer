package pr.stream;

import pr.stream.RenderObjects.Plane;
import pr.stream.RenderObjects.RenderObject;
import pr.stream.RenderObjects.Sphere;
import pr.stream.utils.*;

import javax.swing.*;

import java.awt.image.DataBufferInt;
import java.util.stream.IntStream;

import static pr.stream.utils.QuickMath.*;

public class Main {
    public static long start;
    public static Window panel;
    public static RenderObject[] renderObjects;
    public static Camera d;
    public static final int WIDTH = 1600, HEIGHT = 1000;
    public static final double SCALE = 2;
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pixel Window");
        panel = new Window(WIDTH, HEIGHT, SCALE);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        d = new Camera(new double[] {0, 0, 3},
                new double[] {35, 0, 0},
                new double[] {3, 0, 4},
                90, WIDTH, HEIGHT);

        renderObjects = getRenderObjects();

        start = System.nanoTime();
        d.renderScene(renderObjects);
    }

    private static RenderObject[] getRenderObjects() {
        var red = new Vec3d(255, 0, 0);
        var sphere = new Sphere(new Material(red, 1, true, new Vec3d(0, 1.4, 1.5), 1.8), 2.4, 0, 1, 1);
        var green = new Vec3d(32, 255, 16);
        var white = new Vec3d(255, 255, 255);
        var plane = new Plane(new Vec3d(1, 0, 0),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, -1, 0),
                new Material(green, 0, false, null, 0));

        var wall = new Plane(new Vec3d(4, 0, 1),
                new Vec3d(4, 1, 1),
                new Vec3d(4, 0, -1),
                new Material(white, 0, false, null, 0));

        return new RenderObject[] {sphere, plane, wall};
    }

    static Vec3d directIllumination(Intersection is, int depth) {
        if(depth > 16) return new Vec3d(0, 0, 0);
        var mat = is.getObj().getMaterial();
        var costheta = Math.max(0, is.getToLight().dot(is.getNormal()));
        var c = mat.color();

        if(mat.transparent()) {
            double iorInside = mat.ior();
            double ior;

            Vec3d normal;
            boolean isInside = is.getObj().isInside(is.getOrigin());
            if(isInside) {
                normal = is.getNormal().neg();
                ior = iorInside;
            }
            else {
                normal = is.getNormal();
                ior = 1 / iorInside;
            }

            Vec3d refractedD = refract(is.getD(), normal, ior);
            Vec3d offsetedPos = is.getPos().add(refractedD.mul(0.001));
            RawIntersection refractIntersect = d.getClosestIntersection(renderObjects, refractedD, offsetedPos);

            if(refractIntersect != null) {
                var refractedColor = directIllumination(refractIntersect.build(d.getLight()), depth + 1);

                Vec3d reflectedColor;
                double F = schlick(refractIntersect.d.neg().dot(normal), 0.04);

                Vec3d reflectedD = reflect(is.getD(), normal);
                offsetedPos = is.getPos().add(reflectedD.mul(0.001));
                var ref = d.getClosestIntersection(renderObjects, reflectedD, offsetedPos);

                if (ref == null) reflectedColor = new Vec3d(0, 0, 0);
                else reflectedColor = directIllumination(ref.build(d.light), depth + 1);

                if (isInside) {
                    var t = is.getDist();
                    Vec3d transmittance = mat.sigma_a().mul(-t).forEach(Math::exp);
                    refractedColor = refractedColor.mul(transmittance);
                }
                return reflectedColor.mul(F).add(refractedColor.mul(1.0 - F));
            }
        }
        if(mat.shininess() > 0) {
            Vec3d h = is.getToLight().add(is.getToCamera()).normalize();
            c = c.mul(Math.pow(Math.clamp(h.dot(is.getNormal()), 0, 1), mat.shininess() * 4));
        }

        c = c.mul(costheta);
        return c;
    }

    static void onFrameEnd(Vec3d[] screen) {
        System.out.println("Frame: " + ((System.nanoTime() - start) / 1e+6) + " ms");

        int[] pixels = ((DataBufferInt) panel.image.getRaster().getDataBuffer()).getData();
        IntStream.range(0, pixels.length).parallel().forEach(i -> {
            Vec3d c1 = screen[i];
            Vec3d c2 = screen[i + 1];
            Vec3d c3 = screen[i + WIDTH];
            Vec3d c4 = screen[i + WIDTH + 1];

            Vec3d c = c1.add(c2).add(c3).add(c4).div(4);
            int r = (int) c.x;
            int g = (int) c.y;
            int b = (int) c.z;
            pixels[i] = 255 << 24 | (r << 16) | (g << 8) | b;
        });

        panel.repaint();
    }
}
