package com.uniye.moreattribute.attribute;

import com.uniye.moreattribute.MoreattributeMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = MoreattributeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributes {
    public static final double DEFAULT_USE_TIME_SCALE = 1.0D;
    public static final double MIN_USE_TIME_SCALE = 0.1D;
    public static final double MAX_USE_TIME_SCALE = 10.0D;

    /** 原版潜行速度倍率（1.0 = 不减速，0.3 = 原版潜行减速） */
    public static final double DEFAULT_SNEAK_SPEED = 0.3D;
    /** 原版使用物品（蓄力/吃/喝等）速度倍率（0.2 = 原版减速） */
    public static final double DEFAULT_CHARGE_SPEED = 0.2D;
    public static final double MIN_MOVE_SPEED = 0.0D;
    public static final double MAX_MOVE_SPEED = 1.0D;

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MoreattributeMod.MODID);

    public static final RegistryObject<Attribute> EAT_SPEED = ATTRIBUTES.register("eat_speed",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".eat_speed",
                    DEFAULT_USE_TIME_SCALE, MIN_USE_TIME_SCALE, MAX_USE_TIME_SCALE).setSyncable(true));

    public static final RegistryObject<Attribute> DRINK_SPEED = ATTRIBUTES.register("drink_speed",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".drink_speed",
                    DEFAULT_USE_TIME_SCALE, MIN_USE_TIME_SCALE, MAX_USE_TIME_SCALE).setSyncable(true));

    public static final RegistryObject<Attribute> CAN_ALWAYS_EAT = ATTRIBUTES.register("can_always_eat",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".can_always_eat", 0.0D, 0.0D, 1.0D).setSyncable(true));

    public static final RegistryObject<Attribute> SIZE_SCALE = ATTRIBUTES.register("size_scale",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".size_scale", 1.0D, 0.1D, 20.0D).setSyncable(true));

    public static final RegistryObject<Attribute> NO_COLLISION = ATTRIBUTES.register("no_collision",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".no_collision", 0.0D, 0.0D, 1.0D).setSyncable(true));

    public static final RegistryObject<Attribute> COOLDOWN_REDUCTION = ATTRIBUTES.register("cooldown_reduction",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".cooldown_reduction", 0.0D, 0.0D, 1.0D).setSyncable(true));

    public static final RegistryObject<Attribute> SNEAK_SPEED = ATTRIBUTES.register("sneak_speed",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".sneak_speed",
                    DEFAULT_SNEAK_SPEED, MIN_MOVE_SPEED, MAX_MOVE_SPEED).setSyncable(true));

    public static final RegistryObject<Attribute> CHARGE_SPEED = ATTRIBUTES.register("charge_speed",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".charge_speed",
                    DEFAULT_CHARGE_SPEED, MIN_MOVE_SPEED, MAX_MOVE_SPEED).setSyncable(true));

    public static final RegistryObject<Attribute> FIRE_IMMUNITY = ATTRIBUTES.register("fire_immunity",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".fire_immunity",
                    0.0D, 0.0D, 1.0D).setSyncable(true));

    public static final RegistryObject<Attribute> MAX_EXPERIENCE_LEVEL = ATTRIBUTES.register("max_experience_level",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".max_experience_level",
                    0.0D, 0.0D, Integer.MAX_VALUE).setSyncable(true));

    private ModAttributes() {
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, EAT_SPEED.get());
        event.add(EntityType.PLAYER, DRINK_SPEED.get());
        event.add(EntityType.PLAYER, CAN_ALWAYS_EAT.get());
        event.add(EntityType.PLAYER, COOLDOWN_REDUCTION.get());
        event.add(EntityType.PLAYER, SNEAK_SPEED.get());
        event.add(EntityType.PLAYER, CHARGE_SPEED.get());
        event.add(EntityType.PLAYER, MAX_EXPERIENCE_LEVEL.get());

        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, SIZE_SCALE.get());
            event.add(type, NO_COLLISION.get());
            event.add(type, FIRE_IMMUNITY.get(), type.fireImmune() ? 1.0D : 0.0D);
        }
    }
}
