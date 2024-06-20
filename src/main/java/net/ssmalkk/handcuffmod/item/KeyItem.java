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
            ItemStack offhandItem = player.getHeldItem(Hand.OFF_HAND);
            if (isHandcuff(offhandItem)) {
                // Remove handcuffs and clear NBT
                clearHandcuffs(player, offhandItem);
                transformKeyToHandcuffsOpen(keyStack);
            } else {
                LivingEntity target = findTargetEntity(player);
                if (target != null && isHandcuff(target.getHeldItem(Hand.OFF_HAND))) {
                    // Remove handcuffs and clear NBT
                    clearHandcuffs(target, target.getHeldItem(Hand.OFF_HAND));
                    transformKeyToHandcuffsOpen(keyStack);
                }
            }
        }

        return ActionResult.resultPass(keyStack);
    }

    private boolean isHandcuff(ItemStack stack) {
        return stack.getItem() == ItemRegistry.HANDCUFF.get();
    }

    private void clearHandcuffs(LivingEntity entity, ItemStack handcuffStack) {
        NBTUtil.setLocker(handcuffStack, "");
        NBTUtil.setLockedEntity(handcuffStack, "");
        NBTUtil.setLockKey(handcuffStack, "");
        entity.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
    }

    private void transformKeyToHandcuffsOpen(ItemStack keyStack) {
        if (NBTUtil.getLocker(keyStack).isEmpty() && NBTUtil.getLockedEntity(keyStack).isEmpty()) {
            ItemStack handcuffsOpen = new ItemStack(ItemRegistry.HANDCUFFSOPEN.get());
            handcuffsOpen.setTag(keyStack.getTag());
            keyStack.shrink(1);
        }
    }

    private LivingEntity findTargetEntity(PlayerEntity player) {
        World world = player.world;
        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - 5, player.getPosY() - 5, player.getPosZ() - 5, player.getPosX() + 5, player.getPosY() + 5, player.getPosZ() + 5);
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, aabb);

        for (LivingEntity entity : entities) {
            if (entity instanceof PlayerEntity && entity != player) {
                return entity;
            }
        }
        return null;
    }
}
