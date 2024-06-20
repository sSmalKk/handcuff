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
            // Find the target entity within range
            LivingEntity target = findTargetEntity(player);
            if (target != null) {
                // Try to unlock the target entity
                if (unlockEntity(player, target, keyStack)) {
                    return ActionResult.resultSuccess(keyStack);
                }
            } else {
                // Try to unlock the player themselves
                if (unlockEntity(player, player, keyStack)) {
                    return ActionResult.resultSuccess(keyStack);
                }
            }
        }

        return ActionResult.resultPass(keyStack);
    }

    // Function to find a target entity within range
    private LivingEntity findTargetEntity(PlayerEntity player) {
        World world = player.world;
        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - 5, player.getPosY() - 5, player.getPosZ() - 5, player.getPosX() + 5, player.getPosY() + 5, player.getPosZ() + 5);
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, aabb);

        for (LivingEntity entity : entities) {
            if ((entity instanceof PlayerEntity && entity != player) || entity instanceof VillagerEntity || entity instanceof ZombieEntity || entity instanceof SkeletonEntity) {
                return entity;
            }
        }
        return null;
    }

    // Function to unlock an entity
    private boolean unlockEntity(PlayerEntity player, LivingEntity target, ItemStack keyStack) {
        ItemStack offhandItem = target.getHeldItem(Hand.OFF_HAND);

        if (offhandItem.getItem() == ItemRegistry.HANDCUFF.get() || offhandItem.getItem() == ItemRegistry.HANDCUFFS.get()) {
            String lockKey = NBTUtil.getLockKey(offhandItem);
            String keyLockKey = NBTUtil.getLockKey(keyStack);

            if (lockKey.equals(keyLockKey)) {
                // Remove the handcuffs from the offhand slot
                target.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);

                // Optionally, destroy the key after use
                keyStack.shrink(1);

                return true;
            }
        }

        return false;
    }
}
