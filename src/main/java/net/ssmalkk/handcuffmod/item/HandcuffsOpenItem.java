package net.ssmalkk.handcuffmod.item;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import net.ssmalkk.handcuffmod.util.NBTUtil;

import java.util.List;
import java.util.UUID;

public class HandcuffsOpenItem extends Item implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffsOpenItem(Properties properties) {
        super(properties.maxStackSize(1)); // Limit stack size to 1
    }

    @Override
    public void registerControllers(final AnimationData data) {
        // No need for animation controllers in this version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public void useItemInAir(PlayerEntity player, ItemStack stack) {
        lockSelf(player, stack);
    }

    private void lockSelf(PlayerEntity player, ItemStack stack) {
        // Check if the player already has handcuffs in the offhand
        if (hasHandcuffsInOffhand(player)) {
            return; // Cancel operation if already holding handcuffs in offhand
        }

        ItemStack handcuffs = new ItemStack(ItemRegistry.HANDCUFFS.get());
        handcuffs.addEnchantment(Enchantments.BINDING_CURSE, 1); // Add Curse of Binding

        UUID playerUUID = player.getUniqueID();

        NBTUtil.setLocker(handcuffs, playerUUID);
        NBTUtil.setLockedEntity(handcuffs, playerUUID);
        NBTUtil.setLockKey(handcuffs, generateLockKey());

        ItemStack armorItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (!armorItem.isEmpty()) player.dropItem(armorItem, false);

        player.setItemStackToSlot(EquipmentSlotType.CHEST, handcuffs);

        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, NBTUtil.getLockKey(handcuffs));
        NBTUtil.setLocker(key, playerUUID);
        NBTUtil.setLockedEntity(key, playerUUID);

        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        stack.shrink(1);
    }

    private String generateLockKey() {
        return UUID.randomUUID().toString();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            LivingEntity target = findTargetEntity(player);
            if (target != null) {
                if (player.isSneaking()) {
                    lockEntityWithMain(player, target, stack);
                } else {
                    lockEntityTogether(player, target, stack);
                }
            } else {
                useItemInAir(player, stack);
            }
        }

        return ActionResult.resultPass(stack);
    }

    private LivingEntity findTargetEntity(PlayerEntity player) {
        World world = player.world;
        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - 5, player.getPosY() - 5, player.getPosZ() - 5, player.getPosX() + 5, player.getPosY() + 5, player.getPosZ() + 5);
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, aabb);

        for (LivingEntity entity : entities) {
            if (entity instanceof ZombieEntity || entity instanceof SkeletonEntity || entity instanceof VillagerEntity || (entity instanceof PlayerEntity && entity != player)) {
                return entity;
            }
        }
        return null;
    }

    private boolean isPlayerBehindEntity(PlayerEntity player, LivingEntity target) {
        return player.getLookVec().dotProduct(target.getPositionVec().subtract(player.getPositionVec())) < 0;
    }

    private void lockEntityWithMain(PlayerEntity player, LivingEntity target, ItemStack stack) {
        if (hasHandcuffsInOffhand(player)) return;

        ItemStack handcuff = new ItemStack(ItemRegistry.HANDCUFF.get());
        handcuff.addEnchantment(Enchantments.BINDING_CURSE, 1);
        String lockKey = generateLockKey();

        UUID playerUUID = player.getUniqueID();
        UUID targetUUID = target.getUniqueID();

        NBTUtil.setLocker(handcuff, playerUUID);
        NBTUtil.setLockedEntity(handcuff, targetUUID);
        NBTUtil.setLockKey(handcuff, lockKey);
        NBTUtil.setLockedFromBehind(handcuff, isPlayerBehindEntity(player, target));

        ItemStack armorItem = target.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (!armorItem.isEmpty()) target.entityDropItem(armorItem);

        target.setItemStackToSlot(EquipmentSlotType.CHEST, handcuff);

        ItemStack handcuffCopy = handcuff.copy();
        NBTUtil.setMain(handcuffCopy, true);

        ItemStack playerArmorItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (!playerArmorItem.isEmpty()) player.dropItem(playerArmorItem, false);

        player.setItemStackToSlot(EquipmentSlotType.CHEST, handcuffCopy);

        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, lockKey);
        NBTUtil.setLocker(key, playerUUID);
        NBTUtil.setLockedEntity(key, targetUUID);

        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        stack.shrink(1);
    }

    private void lockEntityTogether(PlayerEntity player, LivingEntity target, ItemStack stack) {
        if (hasHandcuffsInOffhand(player)) return;

        ItemStack handcuff = new ItemStack(ItemRegistry.HANDCUFF.get());
        handcuff.addEnchantment(Enchantments.BINDING_CURSE, 1);
        String lockKey = generateLockKey();

        UUID playerUUID = player.getUniqueID();
        UUID targetUUID = target.getUniqueID();

        NBTUtil.setLocker(handcuff, playerUUID);
        NBTUtil.setLockedEntity(handcuff, targetUUID);
        NBTUtil.setLockKey(handcuff, lockKey);
        NBTUtil.setLockedFromBehind(handcuff, isPlayerBehindEntity(player, target));

        ItemStack armorItem = target.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (!armorItem.isEmpty()) target.entityDropItem(armorItem);

        target.setItemStackToSlot(EquipmentSlotType.CHEST, handcuff);

        ItemStack handcuffCopy = handcuff.copy();
        NBTUtil.setMain(handcuffCopy, true);

        ItemStack playerArmorItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (!playerArmorItem.isEmpty()) player.dropItem(playerArmorItem, false);

        player.setItemStackToSlot(EquipmentSlotType.CHEST, handcuffCopy);

        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, lockKey);
        NBTUtil.setLocker(key, playerUUID);
        NBTUtil.setLockedEntity(key, targetUUID);

        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        stack.shrink(1);
    }

    // Function to check if the player already has handcuffs in the offhand
    private boolean hasHandcuffsInOffhand(PlayerEntity player) {
        ItemStack offhandItem = player.getHeldItemOffhand();
        return offhandItem.getItem() instanceof HandcuffItem;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            if (!player.world.isRemote) {
                lockEntityOnHit(player, target, stack);
            }
        }
        return super.hitEntity(stack, target, attacker);
    }

    private void lockEntityOnHit(PlayerEntity player, LivingEntity target, ItemStack stack) {
        if (hasHandcuffsInOffhand(player)) return;

        ItemStack handcuffs = new ItemStack(ItemRegistry.HANDCUFFS.get());
        handcuffs.addEnchantment(Enchantments.BINDING_CURSE, 1);
        String lockKey = generateLockKey();

        UUID playerUUID = player.getUniqueID();
        UUID targetUUID = target.getUniqueID();

        NBTUtil.setLocker(handcuffs, playerUUID);
        NBTUtil.setLockedEntity(handcuffs, targetUUID);
        NBTUtil.setLockKey(handcuffs, lockKey);
        NBTUtil.setLockedFromBehind(handcuffs, true);

        ItemStack armorItem = target.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (!armorItem.isEmpty()) target.entityDropItem(armorItem);

        target.setItemStackToSlot(EquipmentSlotType.CHEST, handcuffs);

        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, lockKey);
        NBTUtil.setLocker(key, playerUUID);
        NBTUtil.setLockedEntity(key, targetUUID);

        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        stack.shrink(1);
    }
}
