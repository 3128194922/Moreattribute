package com.uniye.moreattribute.event;

import com.uniye.moreattribute.MoreattributeMod;
import com.uniye.moreattribute.attribute.FireImmunityMath;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreattributeMod.MODID)
public final class FireImmunityAttributeHandler {
    private FireImmunityAttributeHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!event.getSource().is(DamageTypeTags.IS_FIRE)
                || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }

        LivingEntity entity = event.getEntity();
        if (entity.fireImmune()) {
            return;
        }

        AttributeInstance attribute = entity.getAttribute(ModAttributes.FIRE_IMMUNITY.get());
        if (attribute == null) {
            return;
        }

        double immunity = FireImmunityMath.clampImmunity(attribute.getValue());
        if (immunity > 0.0D) {
            event.setAmount(FireImmunityMath.scaleFireDamage(event.getAmount(), immunity));
        }
    }
}
