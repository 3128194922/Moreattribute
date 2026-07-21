package com.uniye.moreattribute.mixin;

import com.uniye.moreattribute.accessor.ItemCooldownsAccessor;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldowns.class)
public abstract class ItemCooldownsMixin implements ItemCooldownsAccessor {

    @Unique
    private Player moreattribute$owner;

    @Unique
    private boolean moreattribute$applying = false;

    @Override
    public void moreattribute$setOwner(Player player) {
        this.moreattribute$owner = player;
    }

    @Inject(method = "addCooldown", at = @At("HEAD"), cancellable = true)
    private void moreattribute$onAddCooldown(Item item, int duration, CallbackInfo ci) {
        if (this.moreattribute$applying) return;
        if (this.moreattribute$owner == null || duration <= 0) return;

        double reduction = this.moreattribute$owner.getAttributeValue(ModAttributes.COOLDOWN_REDUCTION.get());
        if (reduction <= 0.0D) return;

        int newDuration = (int) Math.round(duration * (1.0 - reduction));
        if (newDuration <= 0) {
            ci.cancel();
            return;
        }
        if (newDuration != duration) {
            ci.cancel();
            this.moreattribute$applying = true;
            ((ItemCooldowns) (Object) this).addCooldown(item, newDuration);
            this.moreattribute$applying = false;
        }
    }
}
