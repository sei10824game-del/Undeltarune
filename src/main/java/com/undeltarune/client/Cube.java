package com.undeltarune.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Box drawing for the two vertex formats we use. */
final class Cube {
    private static final float[][] N = {{0,1,0},{0,-1,0},{0,0,-1},{0,0,1},{-1,0,0},{1,0,0}};
    // each face: 4 corner index triples (x?,y?,z?) as bit patterns into min/max
    private static final int[][][] F = {
        {{0,1,0},{0,1,1},{1,1,1},{1,1,0}}, {{0,0,1},{0,0,0},{1,0,0},{1,0,1}},
        {{1,0,0},{0,0,0},{0,1,0},{1,1,0}}, {{0,0,1},{1,0,1},{1,1,1},{0,1,1}},
        {{0,0,0},{0,0,1},{0,1,1},{0,1,0}}, {{1,0,1},{1,0,0},{1,1,0},{1,1,1}}};
    static void draw(VertexConsumer vc, PoseStack ps, float x0, float y0, float z0, float x1, float y1, float z1,
                     int rgb, float a, int light, boolean entityFmt) { draw(vc, ps, x0, y0, z0, x1, y1, z1, rgb, a, light, entityFmt, OverlayTexture.NO_OVERLAY, false); }
    static void draw(VertexConsumer vc, PoseStack ps, float x0, float y0, float z0, float x1, float y1, float z1,
                     int rgb, float a, int light, boolean entityFmt, int overlay, boolean shade) {
        var p = ps.last();
        int r = rgb >> 16 & 255, g = rgb >> 8 & 255, b = rgb & 255, al = (int) (a * 255);
        final float[] sh = {.6f, 1f, .8f, .8f, .9f, .9f}; // y is flipped in model space: face 0 (+y) is the underside
        float[] lo = {x0, y0, z0}, hi = {x1, y1, z1}; float[][] uv = {{0,0},{0,1},{1,1},{1,0}};
        for (int f = 0; f < 6; f++) for (int v = 0; v < 4; v++) {
            int[] c = F[f][v];
            float k = shade ? sh[f] : 1f;
            var q = vc.vertex(p.pose(), c[0] == 0 ? lo[0] : hi[0], c[1] == 0 ? lo[1] : hi[1], c[2] == 0 ? lo[2] : hi[2]).color((int) (r * k), (int) (g * k), (int) (b * k), al);
            if (entityFmt) q.uv(uv[v][0], uv[v][1]).overlayCoords(overlay).uv2(light).normal(p.normal(), N[f][0], N[f][1], N[f][2]);
            q.endVertex();
        }
    }
}
