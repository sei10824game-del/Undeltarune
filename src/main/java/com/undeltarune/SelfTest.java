package com.undeltarune;

import com.mojang.brigadier.Command;
import com.undeltarune.attack.Attack;
import com.undeltarune.attack.AttackCtx;
import com.undeltarune.attack.Pose;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** /undeltarune_test [id]: spawns each character (or one), runs every attack tick + pose, reports exceptions. */
@Mod.EventBusSubscriber(modid = Undeltarune.ID)
public class SelfTest {
    static final org.slf4j.Logger LOG = com.mojang.logging.LogUtils.getLogger();
    @SubscribeEvent
    public static void cmds(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("undeltarune_test").requires(s -> s.hasPermission(2))
            .executes(c -> run(c.getSource(), null))
            .then(Commands.argument("id", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(c -> run(c.getSource(), com.mojang.brigadier.arguments.StringArgumentType.getString(c, "id")))));
    }

    static int run(net.minecraft.commands.CommandSourceStack src, String only) {
        var player = src.getEntity() instanceof net.minecraft.world.entity.LivingEntity l ? l : null;
        if (player == null) { src.sendFailure(Component.literal("run as a player")); return 0; }
        var level = src.getLevel(); List<String> bad = new ArrayList<>(); int n = 0, atks = 0;
        for (var en : Undeltarune.MOBS.entrySet()) {
            var d = en.getKey(); if (only != null && !d.id().equals(only)) continue;
            UtMob m = en.getValue().get().create(level); if (m == null) { bad.add(d.id() + ": create null"); continue; }
            m.moveTo(player.getX() + 6, player.getY(), player.getZ(), 90, 0); level.addFreshEntity(m); n++;
            for (Attack a : d.attacks()) {
                atks++;
                try {
                    AttackCtx c = new AttackCtx(m, player);
                    for (int t = 0; t < a.duration; t++) a.tick(c, t);
                    for (int t = 0; t < a.duration; t += 3) a.pose(new Pose(), t, m);
                } catch (Throwable x) { bad.add(d.id() + "/" + a.id + ": " + x); }
            }
            m.discard();
            level.getEntitiesOfClass(UtProjectile.class, new AABB(player.blockPosition()).inflate(120)).forEach(UtProjectile::discard);
        }
        bad.forEach(s -> LOG.error("SELFTEST {}", s));
        final int fn = n, fa = atks;
        src.sendSuccess(() -> Component.literal("chars=" + fn + " attacks=" + fa + " failures=" + bad.size() + (bad.isEmpty() ? "" : " (see log: SELFTEST)")), false);
        return Command.SINGLE_SUCCESS;
    }
}
