package com.uniye.moreattribute.mixin.compat;

import com.uniye.moreattribute.attribute.FireImmunityMath;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

final class CompatFireLogic {
    private CompatFireLogic() {
    }

    /**
     * fire_immunity 已被降低到 1.0 以下时返回 true，
     * 表示应放行原本被 boss hurt() 硬编码拦截的火焰伤害。
     */
    static boolean shouldLetFireDamageThrough(Object entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        AttributeInstance attribute = living.getAttribute(ModAttributes.FIRE_IMMUNITY.get());
        return attribute != null && !FireImmunityMath.grantsFireImmunity(attribute.getValue());
    }
}
