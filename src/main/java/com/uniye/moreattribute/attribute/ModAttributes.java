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
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MoreattributeMod.MODID);

    public static final RegistryObject<Attribute> EAT_SPEED = ATTRIBUTES.register("eat_speed",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".eat_speed", 0.0D, 0.0D, 10.0D).setSyncable(true));

    public static final RegistryObject<Attribute> DRINK_SPEED = ATTRIBUTES.register("drink_speed",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".drink_speed", 0.0D, 0.0D, 10.0D).setSyncable(true));

    public static final RegistryObject<Attribute> CAN_ALWAYS_EAT = ATTRIBUTES.register("can_always_eat",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".can_always_eat", 0.0D, 0.0D, 1.0D).setSyncable(true));

    public static final RegistryObject<Attribute> SIZE_SCALE = ATTRIBUTES.register("size_scale",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".size_scale", 1.0D, 0.1D, 20.0D).setSyncable(true));

    public static final RegistryObject<Attribute> NO_COLLISION = ATTRIBUTES.register("no_collision",
            () -> new RangedAttribute("attribute." + MoreattributeMod.MODID + ".no_collision", 0.0D, 0.0D, 1.0D).setSyncable(true));

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

        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, SIZE_SCALE.get());
            event.add(type, NO_COLLISION.get());
        }
    }
}
