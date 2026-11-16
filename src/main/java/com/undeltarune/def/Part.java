package com.undeltarune.def;

/** One colored box of a model. pivot is relative to parent pivot (pixels, y up, feet at 0, -z = front).
 *  hang: box extends downward from the pivot (arms/legs); else centered on the pivot. glow = fullbright. */
public record Part(String name, String parent, float px, float py, float pz,
                   float w, float h, float d, int rgb, boolean hang, boolean glow, float alpha) {}
