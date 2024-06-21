package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;

import java.util.List;

public class KeyItem extends Item {
    public KeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack keyStack = player.getHeldItem(hand);

        if (!world.isRemote) {
            LivingEntity target = findTargetEntity(player);
            ItemStack playerOffhandItem = player.getHeldItem(Hand.OFF_HAND);

            if (target != null) {
                ItemStack targetOffhandItem = target.getHeldItem(Hand.OFF_HAND);

                if (playerOffhandItem.getItem() == ItemRegistry.HANDCUFF.get() &&
                        targetOffhandItem.getItem() == ItemRegistry.HANDCUFF.get()) {
                    // Caso 1: Dois players/entidades com handcuffs (singular)
                    if (areKeysMatching(keyStack, playerOffhandItem, targetOffhandItem)) {
                        unlockBothEntities(player, target, keyStack, playerOffhandItem, targetOffhandItem);
                    }
                } else if (targetOffhandItem.getItem() == ItemRegistry.HANDCUFFS.get() &&
                        areKeysMatching(keyStack, playerOffhandItem, targetOffhandItem)) {
                    // Caso 2: Entidade alvo tem handcuffs (plural)
                    unlockSingleEntity(player, target, keyStack, targetOffhandItem);
                }
            } else if (playerOffhandItem.getItem() == ItemRegistry.HANDCUFFS.get() &&
                    areKeysMatching(keyStack, playerOffhandItem, null)) {
                // Caso 2: Player tem handcuffs (plural) e está clicando no ar
                unlockSingleEntity(player, null, keyStack, playerOffhandItem);
            }
        }

        return ActionResult.resultPass(keyStack);
    }

    private LivingEntity findTargetEntity(PlayerEntity player) {
        World world = player.world;
        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - 5, player.getPosY() - 5, player.getPosZ() - 5, player.getPosX() + 5, player.getPosY() + 5, player.getPosZ() + 5);
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, aabb);

        for (LivingEntity entity : entities) {
            if (entity != player) {
                return entity;
            }
        }
        return null;
    }

    private boolean areKeysMatching(ItemStack keyStack, ItemStack playerOffhandItem, ItemStack targetOffhandItem) {
        String key = NBTUtil.getLockKey(keyStack);

        if (key.isEmpty()) {
            return false;
        }

        boolean playerMatches = playerOffhandItem != null && NBTUtil.getLockKey(playerOffhandItem).equals(key);
        boolean targetMatches = targetOffhandItem != null && NBTUtil.getLockKey(targetOffhandItem).equals(key);

        return playerMatches || targetMatches;
    }

    private void unlockBothEntities(PlayerEntity player, LivingEntity target, ItemStack keyStack, ItemStack playerOffhandItem, ItemStack targetOffhandItem) {
        // Remove handcuffs from player and target
        player.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
        target.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);

        // Convert key to handcuffsopen
        keyStack.shrink(1);
        player.addItemStackToInventory(new ItemStack(ItemRegistry.HANDCUFFSOPEN.get()));
    }

    private void unlockSingleEntity(PlayerEntity player, LivingEntity target, ItemStack keyStack, ItemStack targetOffhandItem) {
        // Remove handcuffs from the target or player
        if (target != null) {
            target.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
        } else {
            player.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
        }

        // Convert key to handcuffsopen
        keyStack.shrink(1);
        player.addItemStackToInventory(new ItemStack(ItemRegistry.HANDCUFFSOPEN.get()));
    }
}
