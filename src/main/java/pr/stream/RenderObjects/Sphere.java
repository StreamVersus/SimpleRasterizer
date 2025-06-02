package pr.stream.RenderObjects;

import pr.stream.utils.Material;
import pr.stream.utils.RawIntersection;
import pr.stream.utils.Vec3d;

public class Sphere implements RenderObject{
    private final Vec3d origin;
    private final Material material;
    private final double rsquare;

    public Material getMaterial() {
        return material;
    }

    public Sphere(Material m, double x, double y, double z, double l) {
        this.material = m;
        this.origin = new Vec3d(x, y, z);
        this.rsquare = l * l;
    }

    @Override
    public RawIntersection checkInterception(Vec3d d, Vec3d origin, Vec3d light) {
        Vec3d l = origin.sub(this.origin);
        double l2 = l.dot(l) - rsquare;
        double ld = l.dot(d);
        double b = ld * 2;

        double discriminant = b*b - 4 * l2;
        if(discriminant == 0) return new RawIntersection(this, d, origin, -ld);
        else if (discriminant < 0) return null; // no interception
        double dscrsqrt = Math.sqrt(discriminant);

        double t1 = (-b - dscrsqrt) / 2;
        double t2 = (-b + dscrsqrt) / 2;

        if (t2 < 0) return null;
        double t = t1 > 0 ? t1 : t2;
        return new RawIntersection(this, d, origin, t);
    }

    @Override
    public Vec3d getNormal(Vec3d p) {
        return p.sub(origin).normalize();
    }

    @Override
    public boolean isInside(Vec3d pos) {
        return pos.sub(origin).lengthSquared() - rsquare <= 0.0001;
    }

}
