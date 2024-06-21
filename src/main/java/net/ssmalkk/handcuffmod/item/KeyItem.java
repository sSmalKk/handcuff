package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import net.ssmalkk.handcuffmod.util.NBTUtil;

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
            ItemStack playerChestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

            if (target != null) {
                ItemStack targetChestItem = target.getItemStackFromSlot(EquipmentSlotType.CHEST);

                if (playerChestItem.getItem() == ItemRegistry.HANDCUFF.get() &&
                        targetChestItem.getItem() == ItemRegistry.HANDCUFF.get()) {
                    // Caso 1: Dois players/entidades com handcuffs (singular)
                    if (areKeysMatching(keyStack, playerChestItem, targetChestItem)) {
                        unlockBothEntities(player, target, keyStack, playerChestItem, targetChestItem);
                    }
                } else if (targetChestItem.getItem() == ItemRegistry.HANDCUFFS.get() &&
                        areKeysMatching(keyStack, playerChestItem, targetChestItem)) {
                    // Caso 2: Entidade alvo tem handcuffs (plural)
                    unlockSingleEntity(player, target, keyStack, targetChestItem);
                }
            } else if (playerChestItem.getItem() == ItemRegistry.HANDCUFFS.get() &&
                    areKeysMatching(keyStack, playerChestItem, null)) {
                // Caso 2: Player tem handcuffs (plural) e está clicando no ar
                unlockSingleEntity(player, null, keyStack, playerChestItem);
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

    private boolean areKeysMatching(ItemStack keyStack, ItemStack playerChestItem, ItemStack targetChestItem) {
        String key = NBTUtil.getLockKey(keyStack);

        if (key.isEmpty()) {
            return false;
        }

        boolean playerMatches = playerChestItem != null && NBTUtil.getLockKey(playerChestItem).equals(key);
        boolean targetMatches = targetChestItem != null && NBTUtil.getLockKey(targetChestItem).equals(key);

        return playerMatches || targetMatches;
    }

    private void unlockBothEntities(PlayerEntity player, LivingEntity target, ItemStack keyStack, ItemStack playerChestItem, ItemStack targetChestItem) {
        // Remove handcuffs from player and target
        player.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStack.EMPTY);
        target.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStack.EMPTY);

        // Convert key to handcuffsopen
        keyStack.shrink(1);
        player.addItemStackToInventory(new ItemStack(ItemRegistry.HANDCUFFSOPEN.get()));
    }

    private void unlockSingleEntity(PlayerEntity player, LivingEntity target, ItemStack keyStack, ItemStack targetChestItem) {
        // Remove handcuffs from the target or player
        if (target != null) {
            target.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStack.EMPTY);
        } else {
            player.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStack.EMPTY);
        }

        // Convert key to handcuffsopen
        keyStack.shrink(1);
        player.addItemStackToInventory(new ItemStack(ItemRegistry.HANDCUFFSOPEN.get()));
    }
}
