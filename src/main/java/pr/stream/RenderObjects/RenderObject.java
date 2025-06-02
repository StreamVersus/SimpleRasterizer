package pr.stream.RenderObjects;

import pr.stream.utils.Material;
import pr.stream.utils.RawIntersection;
import pr.stream.utils.Vec3d;

public interface RenderObject {
    RawIntersection checkInterception(Vec3d d, Vec3d campos, Vec3d light);
    Vec3d getNormal(Vec3d p);
    Material getMaterial();
    boolean isInside(Vec3d pos);
}
