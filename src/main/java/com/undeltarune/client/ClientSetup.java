package com.undeltarune.client;

import com.undeltarune.Undeltarune;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Undeltarune.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        Undeltarune.MOBS.forEach((d, t) -> e.registerEntityRenderer(t.get(), c -> new BlockyRenderer(c, d)));
        e.registerEntityRenderer(Undeltarune.PROJECTILE.get(), ProjectileRenderer::new);
    }
}
