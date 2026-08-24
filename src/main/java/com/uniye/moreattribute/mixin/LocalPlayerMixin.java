package com.uniye.moreattribute.mixin;

import com.mojang.logging.LogUtils;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 移植自 NoSlowDown（1.18.2），适配 1.20.1：
 * 属性数值语义为“移速倍率”（1.0 = 与普通移速一致，0.5 = 普通移速的一半，0.0 = 停下）：
 * - sneak_speed  : 用 @Redirect 拦截 Input.tick，把潜行/爬行系数直接替换为属性值；
 * - charge_speed : 用 @ModifyConstant 替换使用物品时的 0.2F 系数（弓/弩/三叉戟蓄力、吃、喝、格挡等）。
 */
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Redirect(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/Input;tick(ZF)V"))
    private void moreattribute$sneakTick(Input input, boolean isMovingSlowly, float sneakFactor) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        AttributeInstance inst = self.getAttribute(ModAttributes.SNEAK_SPEED.get());
        float factor = sneakFactor;
        if (inst != null) {
            factor = (float) Mth.clamp(inst.getValue(), 0.0D, 1.0D);
        }
        if (inst == null || Math.abs(factor - sneakFactor) > 1.0E-5F) {
            LOGGER.info("[Moreattribute] sneak tick: attr={}, factor={} (vanilla={})",
                    inst == null ? "MISSING" : inst.getValue(), factor, sneakFactor);
        }
        input.tick(isMovingSlowly, factor);
    }

    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.2F))
    private float moreattribute$chargeSpeed(float original) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        AttributeInstance inst = self.getAttribute(ModAttributes.CHARGE_SPEED.get());
        float factor = original;
        if (inst != null) {
            factor = (float) Mth.clamp(inst.getValue(), 0.0D, 1.0D);
        }
        if (inst == null || Math.abs(factor - original) > 1.0E-5F) {
            LOGGER.info("[Moreattribute] charge tick: attr={}, factor={} (vanilla={})",
                    inst == null ? "MISSING" : inst.getValue(), factor, original);
        }
        return factor;
    }
}