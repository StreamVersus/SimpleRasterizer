package pr.stream.utils;

import pr.stream.RenderObjects.RenderObject;

public class RawIntersection {
    public Vec3d d, origin;
    public double t;
    public RenderObject obj;

    public RawIntersection(RenderObject obj, Vec3d d, Vec3d origin, double t) {
        this.obj = obj;
        this.d = d;
        this.origin = origin;
        this.t = t;
    }

    public Intersection build(Vec3d light) {
        Vec3d pos = d.mul(t).add(origin);
        Vec3d toCamera = d.neg();

        Vec3d toLight = light.sub(pos);
        return new Intersection(obj, pos, origin, toCamera, toLight.div(toLight.length()), d, t, toLight.length());
    }
}
