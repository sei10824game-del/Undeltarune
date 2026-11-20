package com.undeltarune.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.undeltarune.Undeltarune;
import com.undeltarune.attack.Attack;
import com.undeltarune.attack.Pose;
import com.undeltarune.def.CharacterDef;
import com.undeltarune.def.Part;
import com.undeltarune.entity.UtMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.util.*;

/** Renders a CharacterDef's box model as a part tree with idle/walk/head-look + per-attack Pose. */
public class BlockyRenderer extends EntityRenderer<UtMob> {
    public static final ResourceLocation WHITE = new ResourceLocation(Undeltarune.ID, "textures/entity/white.png");
    private final float fit, bottom; private int ov;
    private final CharacterDef def; private final Map<String, List<Part>> kids = new HashMap<>();

    public BlockyRenderer(EntityRendererProvider.Context c, CharacterDef d) {
        super(c); def = d; shadowRadius = d.width() * .5f * d.scale();
        for (Part p : d.model().parts) kids.computeIfAbsent(p.parent() == null ? "" : p.parent(), k -> new ArrayList<>()).add(p);
        // rest-pose extent (ignores rotation): sit the model on the ground and fit it to the hitbox height
        float lo = 1e9f, hi = -1e9f; Map<String, Float> abs = new HashMap<>();
        for (Part p : d.model().parts) {
            float y = (p.parent() == null ? 0 : abs.getOrDefault(p.parent(), 0f)) + p.py(); abs.put(p.name(), y);
            lo = Math.min(lo, p.hang() ? y : y - p.h() / 2); hi = Math.max(hi, p.hang() ? y + p.h() : y + p.h() / 2);
        }
        bottom = hi; fit = hi > lo ? d.height() * 16 / (hi - lo) : 1;
    }
    @Override public ResourceLocation getTextureLocation(UtMob e) { return WHITE; }

    @Override public void render(UtMob e, float yaw, float pt, PoseStack ps, MultiBufferSource buf, int light) {
        ps.pushPose();
        ps.mulPose(Axis.YP.rotationDegrees(180 - Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot)));
        if (e.deathTime > 0) ps.mulPose(Axis.ZP.rotationDegrees(Math.min(1f, (e.deathTime + pt) / 20f) * 90f));
        ps.scale(def.scale() * fit, def.scale() * fit, def.scale() * fit); ps.scale(-1, -1, 1); ps.translate(0, -bottom / 16, 0);
        Pose pose = new Pose();
        float walk = e.walkAnimation.position(pt), amp = e.walkAnimation.speed(pt), age = e.tickCount + pt;
        pose.r("armL", Mth.cos(walk * .66f) * amp, 0, 0).r("armR", -Mth.cos(walk * .66f) * amp, 0, 0)
            .r("legL", -Mth.cos(walk * .66f) * amp * 1.2f, 0, 0).r("legR", Mth.cos(walk * .66f) * amp * 1.2f, 0, 0)
            .r("armL", 0, 0, Mth.sin(age * .08f) * .04f).r("armR", 0, 0, -Mth.sin(age * .08f) * .04f)
            .o("body", 0, Mth.sin(age * .1f) * .25f, 0);
        pose.r("head", (float) Math.toRadians(Mth.lerp(pt, e.xRotO, e.getXRot())),
            (float) Math.toRadians(Mth.wrapDegrees(Mth.rotLerp(pt, e.yHeadRotO, e.yHeadRot) - Mth.rotLerp(pt, e.yBodyRotO, e.yBodyRot))), 0);
        Attack a = e.current();
        if (a != null) a.pose(pose, e.atkTick() + pt, e);
        var solid = buf.getBuffer(RenderType.entityTranslucent(WHITE));
        ov = e.hurtTime > 0 || e.deathTime > 0 ? OverlayTexture.pack(OverlayTexture.u(1f), OverlayTexture.v(true)) : OverlayTexture.NO_OVERLAY;
        walk(null, ps, pose, solid, buf, light);
        ps.popPose();
        super.render(e, yaw, pt, ps, buf, light);
    }
    private void walk(String parent, PoseStack ps, Pose pose, com.mojang.blaze3d.vertex.VertexConsumer vc, MultiBufferSource buf, int light) {
        for (Part p : kids.getOrDefault(parent == null ? "" : parent, List.of())) {
            ps.pushPose();
            float[] o = pose.off.get(p.name()), r = pose.rot.get(p.name());
            // model space: y down after the flip, so negate y
            ps.translate((p.px() + (o == null ? 0 : o[0])) / 16, -(p.py() + (o == null ? 0 : o[1])) / 16, (p.pz() + (o == null ? 0 : o[2])) / 16);
            if (r != null) { ps.mulPose(Axis.ZP.rotation(r[2])); ps.mulPose(Axis.YP.rotation(r[1])); ps.mulPose(Axis.XP.rotation(r[0])); }
            float hw = p.w() / 32, hd = p.d() / 32, h = p.h() / 16;
            // y axis is flipped (+y = down on screen) so a hanging box spans y in [0,h]; a centered one [-h/2,h/2]
            float y0 = p.hang() ? 0 : -h / 2, y1 = p.hang() ? h : h / 2;
            var v = p.glow() ? buf.getBuffer(RenderType.entityTranslucent(WHITE)) : vc;
            Cube.draw(v, ps, -hw, y0, -hd, hw, y1, hd, p.rgb(), p.alpha(), p.glow() ? 15728880 : light, true, p.glow() ? OverlayTexture.NO_OVERLAY : ov, !p.glow());
            walk(p.name(), ps, pose, vc, buf, light);
            ps.popPose();
        }
    }
}
