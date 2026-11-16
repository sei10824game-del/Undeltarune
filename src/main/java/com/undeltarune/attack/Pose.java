package com.undeltarune.attack;

import java.util.HashMap;
import java.util.Map;

/** Per-frame client pose overrides, by part name. r = rotation (radians, added), o = offset (pixels, added). */
public class Pose {
    public final Map<String, float[]> rot = new HashMap<>(), off = new HashMap<>();
    public Pose r(String n, float x, float y, float z) { add(rot, n, x, y, z); return this; }
    public Pose o(String n, float x, float y, float z) { add(off, n, x, y, z); return this; }
    private static void add(Map<String, float[]> m, String n, float x, float y, float z) {
        float[] v = m.computeIfAbsent(n, k -> new float[3]); v[0] += x; v[1] += y; v[2] += z;
    }
}
