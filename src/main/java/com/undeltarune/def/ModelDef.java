package com.undeltarune.def;

import java.util.ArrayList;
import java.util.List;

/** Fluent builder for a procedural box model. Conventional names get default animation:
 *  head (looks at target), armL/armR/legL/legR (walk swing). */
public class ModelDef {
    public final List<Part> parts = new ArrayList<>();
    public ModelDef box(String n, String parent, float px, float py, float pz, float w, float h, float d, int rgb) {
        parts.add(new Part(n, parent, px, py, pz, w, h, d, rgb, false, false, 1f)); return this;
    }
    public ModelDef limb(String n, String parent, float px, float py, float pz, float w, float h, float d, int rgb) {
        parts.add(new Part(n, parent, px, py, pz, w, h, d, rgb, true, false, 1f)); return this;
    }
    public ModelDef glow(String n, String parent, float px, float py, float pz, float w, float h, float d, int rgb) {
        parts.add(new Part(n, parent, px, py, pz, w, h, d, rgb, false, true, 1f)); return this;
    }
    public ModelDef ghost(String n, String parent, float px, float py, float pz, float w, float h, float d, int rgb, float a) {
        parts.add(new Part(n, parent, px, py, pz, w, h, d, rgb, false, true, a)); return this;
    }
}
