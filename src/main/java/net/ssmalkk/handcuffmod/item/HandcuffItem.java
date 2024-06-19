package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.UUID;

import static sun.audio.AudioPlayer.player;

public class HandcuffItem extends Item implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffItem(Properties properties) {
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
            // Clique no ar com shift
            HandcuffMod.LOGGER.debug("Player {} shift-clicked in the air", player.getName().getString());
            handleShiftClickInAir(player, nbt);
        } else {
            // Clique no ar sem shift
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

        if (!(itemStack.getItem() instanceof HandcuffItem)) {
            return;
        }

        CompoundNBT nbt = itemStack.getOrCreateTag();

        if (interactingPlayer.isSneaking()) {
            // Clique em outro jogador com shift
            HandcuffMod.LOGGER.debug("Player {} shift-clicked on player {}", interactingPlayer.getName().getString(), targetPlayer.getName().getString());
            handleShiftClickOnPlayer(interactingPlayer, targetPlayer, nbt);
        } else {
            // Clique em outro jogador sem shift
            HandcuffMod.LOGGER.debug("Player {} clicked on player {}", interactingPlayer.getName().getString(), targetPlayer.getName().getString());
            handleRegularClickOnPlayer(interactingPlayer, targetPlayer, nbt);
        }
    }

    private void handleRegularClickInAir(PlayerEntity player, CompoundNBT nbt) {
        // Regular click logic
        HandcuffMod.LOGGER.debug("Handling regular click in the air for player {}", player.getName().getString());

        if (!nbt.contains("HandcuffState")) {
            // If NBT does not exist, initialize it
            nbt.putBoolean("RightHandCuffed", true);
            nbt.putBoolean("LeftHandCuffed", true);
            nbt.putString("RightHandPlayer", player.getName().getString());
            nbt.putString("LeftHandPlayer", player.getName().getString());
            nbt.putString("Key", UUID.randomUUID().toString());
            player.sendMessage(new TranslationTextComponent("handcuffmod.handcuff_initialized"), player.getUniqueID());
            updateOffhandKey(player, nbt);
        }
    }

    private void handleShiftClickInAir(PlayerEntity player, CompoundNBT nbt) {
        HandcuffMod.LOGGER.debug("Handling shift-click in the air for player {}", player.getName().getString());

        // Check if there is a handcuff state saved
        if (nbt.contains("HandcuffState")) {
            boolean rightHandCuffed = nbt.getBoolean("RightHandCuffed");
            boolean leftHandCuffed = nbt.getBoolean("LeftHandCuffed");

            // Check if both hands are cuffed
            if (rightHandCuffed && leftHandCuffed) {
                // Check if there is a key in the offhand
                ItemStack offhandItem = player.getHeldItemOffhand();
                if (!offhandItem.isEmpty() && offhandItem.getItem() instanceof HandcuffItem) {
                    CompoundNBT keyNbt = offhandItem.getOrCreateTag();

                    // Check if key matches the handcuff data
                    if (keyNbt.contains("Key") && keyNbt.getString("Key").equals(nbt.getString("Key"))) {
                        // Unlock both hands
                        nbt.putBoolean("RightHandCuffed", false);
                        nbt.putBoolean("LeftHandCuffed", false);
                        player.sendMessage(new TranslationTextComponent("handcuffmod.hands_unlocked"), player.getUniqueID());
                        player.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY); // Remove the key from offhand
                        return;
                    }
                }
            }
        }

        // If no matching key found or handcuffs not fully cuffed, handle shift-click as needed
        // Implement your specific logic for shift-click here if needed
    }


    private void handleRegularClickOnPlayer(PlayerEntity interactingPlayer, PlayerEntity targetPlayer, CompoundNBT nbt) {
        // Regular click on another player logic
        HandcuffMod.LOGGER.debug("Handling regular click on player {} by player {}", targetPlayer.getName().getString(), interactingPlayer.getName().getString());

        if (!nbt.contains("HandcuffState")) {
            // Initialize if NBT does not exist
            nbt.putBoolean("RightHandCuffed", true);
            nbt.putBoolean("LeftHandCuffed", true);
            nbt.putString("RightHandPlayer", targetPlayer.getName().getString());
            nbt.putString("LeftHandPlayer", targetPlayer.getName().getString());
            nbt.putString("Key", UUID.randomUUID().toString());
            updateOffhandKey(interactingPlayer, nbt);
        }

        // Apply curse of binding to target player
        ItemStack handcuffStack = new ItemStack(this);
        handcuffStack.getOrCreateTag().putBoolean("curse_of_binding", true);
        targetPlayer.inventory.setInventorySlotContents(targetPlayer.inventory.currentItem, handcuffStack);
    }

    private void handleShiftClickInAir(PlayerEntity interactingPlayer, PlayerEntity targetPlayer, CompoundNBT nbt) {
        // Regular click on another player logic
        HandcuffMod.LOGGER.debug("Handling regular click on player {} by player {}", targetPlayer.getName().getString(), interactingPlayer.getName().getString());

        if (!nbt.contains("HandcuffState")) {
            // Initialize if NBT does not exist
            nbt.putBoolean("RightHandCuffed", true);
            nbt.putBoolean("LeftHandCuffed", true);
            nbt.putString("RightHandPlayer", player.getName().getString());
            nbt.putString("LeftHandPlayer", targetPlayer.getName().getString());
            nbt.putString("Key", UUID.randomUUID().toString());
            updateOffhandKey(interactingPlayer, nbt);
        }

        // Apply curse of binding to target player
        ItemStack handcuffStack = new ItemStack(this);
        handcuffStack.getOrCreateTag().putBoolean("curse_of_binding", true);
        targetPlayer.inventory.setInventorySlotContents(targetPlayer.inventory.currentItem, handcuffStack);
    }    private void updateOffhandKey(PlayerEntity player, CompoundNBT handcuffNbt) {
        ItemStack offhandItem = player.getHeldItemOffhand();
        if (offhandItem.getItem() == ItemRegistry.KEY.get()) {
            offhandItem.setTag(handcuffNbt); // Set the same NBT data to the key in the offhand
        } else {
            player.sendMessage(new TranslationTextComponent("handcuffmod.no_key_in_offhand"), player.getUniqueID());
        }
    }
}
