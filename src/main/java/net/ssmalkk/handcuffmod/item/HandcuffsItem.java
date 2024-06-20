package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.ssmalkk.handcuffmod.HandcuffMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.UUID;

public class HandcuffsItem extends Item implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffsItem(Properties properties) {
        super(properties.maxStackSize(1)); // Limitar a 1 por pilha
    }

    @Override
    public void registerControllers(final AnimationData data) {
        // No need for any animation controllers in this simplified version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        CompoundNBT nbt = itemStack.getOrCreateTag();

        if (player.isSneaking()) {
            // Shift-click no ar para se libertar
            HandcuffMod.LOGGER.debug("Player {} shift-clicked in the air", player.getName().getString());
            handleShiftClickInAir(player, nbt);
        } else {
            // Clicar no ar para algemar
            HandcuffMod.LOGGER.debug("Player {} clicked in the air", player.getName().getString());
            handleRegularClickInAir(player, nbt);
        }

        return ActionResult.resultSuccess(itemStack);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity targetPlayer = (PlayerEntity) event.getTarget();
        PlayerEntity interactingPlayer = event.getPlayer();
        ItemStack itemStack = interactingPlayer.getHeldItem(event.getHand());

        if (!(itemStack.getItem() instanceof HandcuffsItem)) {
            return;
        }

        CompoundNBT nbt = itemStack.getOrCreateTag();

        if (interactingPlayer.isSneaking()) {
            // Shift-click em outro jogador para se trancar junto
            HandcuffMod.LOGGER.debug("Player {} shift-clicked on player {}", interactingPlayer.getName().getString(), targetPlayer.getName().getString());
            handleShiftClickOnPlayer(interactingPlayer, targetPlayer, nbt);
        } else {
            // Clicar em outro jogador para algemar
            HandcuffMod.LOGGER.debug("Player {} clicked on player {}", interactingPlayer.getName().getString(), targetPlayer.getName().getString());
            handleRegularClickOnPlayer(interactingPlayer, targetPlayer, nbt);
        }
    }

    private void handleRegularClickInAir(PlayerEntity player, CompoundNBT nbt) {
        HandcuffMod.LOGGER.debug("Handling regular click in the air for player {}", player.getName().getString());

        // Initialize handcuff state if not already handcuffed
        if (!nbt.contains("RightHandCuffed") && !nbt.contains("LeftHandCuffed")) {
            nbt.putBoolean("RightHandCuffed", true);
            nbt.putBoolean("LeftHandCuffed", true);
            nbt.putString("RightHandPlayer", player.getName().getString());
            nbt.putString("LeftHandPlayer", player.getName().getString());
            nbt.putString("Key", UUID.randomUUID().toString());

            // Apply curse of binding
            ItemStack offhandItem = new ItemStack(this);
            offhandItem.setTag(nbt);
            offhandItem.getOrCreateTag().putBoolean("curse_of_binding", true);
            player.setHeldItem(Hand.OFF_HAND, offhandItem);
        }
    }

    private void handleShiftClickInAir(PlayerEntity player, CompoundNBT nbt) {
        HandcuffMod.LOGGER.debug("Handling shift-click in the air for player {}", player.getName().getString());

        // Check if both hands are cuffed
        if (nbt.getBoolean("RightHandCuffed") && nbt.getBoolean("LeftHandCuffed")) {
            // Check for key in offhand
            ItemStack offhandItem = player.getHeldItemOffhand();
            if (!offhandItem.isEmpty() && offhandItem.getItem() instanceof HandcuffsItem) {
                CompoundNBT keyNbt = offhandItem.getOrCreateTag();

                // Check if key matches
                if (keyNbt.contains("Key") && keyNbt.getString("Key").equals(nbt.getString("Key"))) {
                    player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                    player.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
                    HandcuffMod.LOGGER.debug("Player {} released from handcuffs", player.getName().getString());
                }
            }
        }
    }

    private void handleRegularClickOnPlayer(PlayerEntity interactingPlayer, PlayerEntity targetPlayer, CompoundNBT nbt) {
        HandcuffMod.LOGGER.debug("Handling regular click on player {} by player {}", targetPlayer.getName().getString(), interactingPlayer.getName().getString());

        // Initialize handcuff state if not already handcuffed
        if (!nbt.contains("RightHandCuffed") && !nbt.contains("LeftHandCuffed")) {
            nbt.putBoolean("RightHandCuffed", true);
            nbt.putBoolean("LeftHandCuffed", true);
            nbt.putString("RightHandPlayer", interactingPlayer.getName().getString());
            nbt.putString("LeftHandPlayer", targetPlayer.getName().getString());
            nbt.putString("Key", UUID.randomUUID().toString());

            // Apply curse of binding to target player
            ItemStack handcuffStack = new ItemStack(this);
            handcuffStack.getOrCreateTag().putBoolean("curse_of_binding", true);
            targetPlayer.setHeldItem(Hand.OFF_HAND, handcuffStack);
        }
    }

    private void handleShiftClickOnPlayer(PlayerEntity interactingPlayer, PlayerEntity targetPlayer, CompoundNBT nbt) {
        HandcuffMod.LOGGER.debug("Handling shift-click on player {} by player {}", targetPlayer.getName().getString(), interactingPlayer.getName().getString());

        // Initialize handcuff state if not already handcuffed
        if (!nbt.contains("RightHandCuffed") && !nbt.contains("LeftHandCuffed")) {
            nbt.putBoolean("RightHandCuffed", true);
            nbt.putBoolean("LeftHandCuffed", true);
            nbt.putString("RightHandPlayer", interactingPlayer.getName().getString());
            nbt.putString("LeftHandPlayer", targetPlayer.getName().getString());
            nbt.putString("Key", UUID.randomUUID().toString());

            // Apply curse of binding to both players
            ItemStack interactingHandcuffStack = new ItemStack(this);
            interactingHandcuffStack.getOrCreateTag().putString("fakeitem", "true");
            interactingPlayer.setHeldItem(Hand.MAIN_HAND, interactingHandcuffStack);

            ItemStack targetHandcuffStack = new ItemStack(this);
            targetHandcuffStack.getOrCreateTag().putBoolean("curse_of_binding", true);
            targetPlayer.setHeldItem(Hand.OFF_HAND, targetHandcuffStack);
        }
    }
}
