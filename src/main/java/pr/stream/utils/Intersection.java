package pr.stream.utils;

import pr.stream.RenderObjects.RenderObject;

public class Intersection {
    private final Vec3d normal, toCamera, toLight, pos, d, origin;
    private final double dist, distToLight;
    private final RenderObject obj;

    public Intersection(RenderObject obj, Vec3d pos, Vec3d origin, Vec3d dinv, Vec3d toLight, Vec3d d, double dist, double distToLight) {
        this.obj = obj;
        this.pos = pos;
        this.origin = origin;
        this.normal = obj.getNormal(pos);
        this.toCamera = dinv;
        this.toLight = toLight;
        this.d = d;
        this.dist = dist;
        this.distToLight = distToLight;
    }

    public Vec3d getNormal() {
        return normal;
    }

    public Vec3d getToCamera() {
        return toCamera;
    }

    public Vec3d getToLight() {
        return toLight;
    }

    public Vec3d getPos() {
        return pos;
    }

    public RenderObject getObj() {
        return obj;
    }

    public double getDist() {
        return dist;
    }

    public Vec3d getD() {
        return d;
    }

    public double getDistToLight() {
        return distToLight;
    }

    public Vec3d getOrigin() {
        return origin;
    }
}
