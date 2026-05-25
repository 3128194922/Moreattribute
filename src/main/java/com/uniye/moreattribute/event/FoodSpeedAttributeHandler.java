package com.uniye.moreattribute.event;

import com.uniye.moreattribute.MoreattributeMod;
import com.uniye.moreattribute.attribute.ModAttributes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.eventbus.api.EventPriority;
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemStartUse(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = event.getItem();
        double timeScale = getUseTimeScale(player, stack);
        if (timeScale <= 0D) {
            return;
        }

        int originalDuration = event.getDuration();
        int adjustedDuration = Math.max(1, (int) Math.round(originalDuration * timeScale));
        if (adjustedDuration != originalDuration) {
            event.setDuration(adjustedDuration);
        }
    }

    private static boolean canAlwaysEat(Player player) {
        return player.getAttributeValue(ModAttributes.CAN_ALWAYS_EAT.get()) > 0D;
    }

    private static double getUseTimeScale(Player player, ItemStack stack) {
        if (isDrinkItem(stack)) {
            return player.getAttributeValue(ModAttributes.DRINK_SPEED.get());
        }

        UseAnim useAnim = stack.getUseAnimation();
        if (useAnim == UseAnim.EAT) {
            return player.getAttributeValue(ModAttributes.EAT_SPEED.get());
        }

        return 0D;
    }

    private static boolean isDrinkItem(ItemStack stack) {
        if (stack.getUseAnimation() == UseAnim.DRINK) {
            return true;
        }

        return stack.getItem() instanceof PotionItem
                || stack.getItem() instanceof MilkBucketItem
                || stack.getItem() instanceof HoneyBottleItem;
    }
}
