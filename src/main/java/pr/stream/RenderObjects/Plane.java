package pr.stream.RenderObjects;

import pr.stream.utils.Material;
import pr.stream.utils.RawIntersection;
import pr.stream.utils.Vec3d;

public class Plane implements RenderObject{
    private final Vec3d p0;
    private final Vec3d normal;
    private final Material material;

    public Plane(Vec3d p0, Vec3d p1, Vec3d p2, Material material) {
        this.p0 = p0;
        this.material = material;
        var v1 = p1.sub(p0);
        var v2 = p2.sub(p0);
        this.normal = v1.cross(v2).normalize();
    }

    @Override
    public RawIntersection checkInterception(Vec3d d, Vec3d origin, Vec3d light) {
        var dn = d.dot(normal);
        if (Math.abs(dn) < 0.0001) return null;

        var l = p0.sub(origin);
        var ln = l.dot(normal);
        var t = ln / dn;
        if(t <= 0.0001) return null;

        return new RawIntersection(this, d, origin, t);
    }

    @Override
    public Vec3d getNormal(Vec3d p) {
        return normal;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean isInside(Vec3d pos) {
        return false;
    }
}
