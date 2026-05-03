package com.uniye.moreattribute.event;

import com.uniye.moreattribute.MoreattributeMod;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreattributeMod.MODID)
public class FoodSpeedAttributeHandler {
    private FoodSpeedAttributeHandler() {
    }

    @SubscribeEvent
    public static void onFoodRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!stack.isEdible()) {
            return;
        }

        if (!player.canEat(false) && canAlwaysEat(player)) {
            player.startUsingItem(event.getHand());
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemStartUse(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UseAnim useAnim = event.getItem().getUseAnimation();
        if (useAnim != UseAnim.EAT && useAnim != UseAnim.DRINK) {
            return;
        }

        double speedLevel = useAnim == UseAnim.DRINK
                ? player.getAttributeValue(ModAttributes.DRINK_SPEED.get())
                : player.getAttributeValue(ModAttributes.EAT_SPEED.get());

        int level = (int) Math.floor(speedLevel);
        if (level <= 0) {
            return;
        }

        int originalDuration = event.getDuration();
        int adjustedDuration = Math.max(1, originalDuration >> level);
        if (adjustedDuration != originalDuration) {
            event.setDuration(adjustedDuration);
        }
    }

    private static boolean canAlwaysEat(Player player) {
        return player.getAttributeValue(ModAttributes.CAN_ALWAYS_EAT.get()) > 0D;
    }
}
