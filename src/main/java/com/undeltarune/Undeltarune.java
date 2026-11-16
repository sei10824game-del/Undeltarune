package com.undeltarune;

import com.undeltarune.def.CharacterDef;
import com.undeltarune.entity.UtMob;
import com.undeltarune.entity.UtProjectile;
import com.undeltarune.chars.Characters;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod(Undeltarune.ID)
public class Undeltarune {
    public static final String ID = "undeltarune";
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final Map<CharacterDef, RegistryObject<EntityType<UtMob>>> MOBS = new LinkedHashMap<>();
    public static final RegistryObject<EntityType<UtProjectile>> PROJECTILE = ENTITIES.register("projectile",
        () -> EntityType.Builder.<UtProjectile>of(UtProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f)
            .clientTrackingRange(16).updateInterval(2).noSummon().build("projectile"));

    public Undeltarune() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Characters.init();
        for (CharacterDef d : Characters.ALL) {
            RegistryObject<EntityType<UtMob>> t = ENTITIES.register(d.id(), () ->
                EntityType.Builder.<UtMob>of((ty, lv) -> new UtMob(ty, lv, d), MobCategory.MONSTER)
                    .sized(d.width() * d.scale(), d.height() * d.scale()).clientTrackingRange(10).build(d.id()));
            MOBS.put(d, t);
            ITEMS.register(d.id() + "_spawn_egg", () -> new ForgeSpawnEggItem(t, d.eggA(), d.eggB(), new Item.Properties()));
        }
        TABS.register("main", () -> CreativeModeTab.builder().title(Component.literal("Undeltarune"))
            .icon(() -> new ItemStack(ITEMS.getEntries().iterator().next().get()))
            .displayItems((p, o) -> ITEMS.getEntries().forEach(i -> o.accept(i.get()))).build());
        ENTITIES.register(bus); ITEMS.register(bus); TABS.register(bus);
        bus.addListener((EntityAttributeCreationEvent e) ->
            MOBS.forEach((d, t) -> e.put(t.get(), UtMob.attrs(d).build())));
    }
}
