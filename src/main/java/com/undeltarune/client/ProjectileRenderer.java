package com.undeltarune.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ProjectileRenderer extends EntityRenderer<UtProjectile> {
    public ProjectileRenderer(EntityRendererProvider.Context c) { super(c); }
    @Override public ResourceLocation getTextureLocation(UtProjectile e) { return BlockyRenderer.WHITE; }
    @Override public boolean shouldRender(UtProjectile e, net.minecraft.client.renderer.culling.Frustum f, double x, double y, double z) { return true; }
    @Override public void render(UtProjectile e, float yaw, float pt, PoseStack ps, MultiBufferSource buf, int light) {
        ps.pushPose();
        ps.mulPose(Axis.YP.rotationDegrees(180 - e.getYRot()));
        ps.mulPose(Axis.XP.rotationDegrees(e.getXRot()));
        float w = e.w(), h = e.h(), l = e.l(), k = e.shrink ? 1 - e.lifeFrac(pt) : 1;
        boolean warning = e.age() <= e.warn();
        float a = warning ? .25f + .15f * (float) Math.sin((e.age() + pt) * 1.2f) : 1f;
        if (warning) { w *= .25f; h *= .25f; }
        w *= k; h *= k;
        int c = e.color();
        var vc = buf.getBuffer(RenderType.lightning());
        float f = warning ? 1f : 1f + .06f * (float) Math.sin((e.age() + pt) * 2.3f) + .04f * (e.getId() % 3);   // flicker
        float ow = w * 1.5f * f, oh = h * 1.5f * f, ol = l + (l > 1 ? 0 : (h + w) * .25f);
        Cube.draw(vc, ps, -ow / 2, -oh / 2, -ol / 2, ow / 2, oh / 2, ol / 2, c, a * .18f, 0, false);      // halo
        Cube.draw(vc, ps, -w / 2 * f, -h / 2 * f, -l / 2, w / 2 * f, h / 2 * f, l / 2, c, a * .55f, 0, false); // body
        float iw = w * .5f, ih = h * .5f;
        Cube.draw(vc, ps, -iw / 2, -ih / 2, -l / 2 * .98f, iw / 2, ih / 2, l / 2 * .98f, 0xffffff, a, 0, false); // hot core
        // bone look: white thin elongated box gets knobbed ends (two small cubes per end)
        boolean white = (c >> 16 & 255) > 220 && (c >> 8 & 255) > 220 && (c & 255) > 220;
        float th = Math.min(e.w(), Math.min(e.h(), e.l())), lg = Math.max(e.w(), Math.max(e.h(), e.l()));
        if (white && !warning && th < .4f && lg > th * 3f) {
            int ax = e.h() == lg ? 1 : e.l() == lg ? 2 : 0; float kn = th * 1.7f * k, half = lg * k / 2;
            for (int sgn = -1; sgn <= 1; sgn += 2) for (int side = -1; side <= 1; side += 2) {
                float o = kn * .32f * side, p = sgn * (half - kn * .4f);
                float cx = ax == 0 ? p : o, cy = ax == 1 ? p : o, cz = ax == 2 ? p : 0;
                float hk = kn / 2 * .8f;
                Cube.draw(vc, ps, cx - hk, cy - hk, cz - hk, cx + hk, cy + hk, cz + hk, 0xffffff, a, 0, false);
            }
        }
        ps.popPose();
    }
}
