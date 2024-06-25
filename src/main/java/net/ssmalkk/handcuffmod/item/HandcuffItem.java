package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.config.ConfigHandler;
import net.ssmalkk.handcuffmod.util.NBTUtil;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HandcuffItem extends GeoArmorItem implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder.group(HandcuffMod.handcuffModItemGroup));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void registerControllers(AnimationData data) {
        // No animation controllers needed for this version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (player.world.isRemote) return; // Ignore client-side ticks

        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (chestItem.getItem() instanceof HandcuffItem && NBTUtil.isMain(chestItem)) {
            updateLockNBT(chestItem, player);

            if (player.isSneaking()) {
                saveLockedPosition(chestItem, player);
            }
        }
    }

    private static void updateLockNBT(ItemStack stack, PlayerEntity player) {
        UUID lockerUUID = NBTUtil.getLocker(stack);
        UUID lockedUUID = NBTUtil.getLockedEntity(stack);

        if (lockerUUID != null && lockedUUID != null) {
            NBTUtil.setLockerPos(stack, player.getPositionVec());
            NBTUtil.setLockerDim(stack, player.world.getDimensionKey().getLocation().toString());

            LivingEntity lockedEntity = findEntityByUUID(player, lockedUUID);

            if (lockedEntity != null) {
                NBTUtil.setLockedPos(stack, lockedEntity.getPositionVec());
                NBTUtil.setLockedDim(stack, lockedEntity.world.getDimensionKey().getLocation().toString());

                // Teleportation logic
                if (ConfigHandler.COMMON.teleportOnBreak.get() && isBeyondTeleportDistance(player.getPositionVec(), lockedEntity.getPositionVec(), ConfigHandler.COMMON.teleportDistance.get())) {
                    teleportLockedEntity(player, lockedEntity, ConfigHandler.COMMON.teleportDistance.get());
                }
            }
        }
    }

    private static LivingEntity findEntityByUUID(PlayerEntity player, UUID uuid) {
        for (LivingEntity entity : player.world.getEntitiesWithinAABB(LivingEntity.class, player.getBoundingBox().grow(32.0))) {
            if (entity.getUniqueID().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    private static void saveLockedPosition(ItemStack stack, PlayerEntity player) {
        UUID lockedUUID = NBTUtil.getLockedEntity(stack);

        if (lockedUUID != null) {
            LivingEntity lockedEntity = findEntityByUUID(player, lockedUUID);

            if (lockedEntity != null) {
                NBTUtil.setLockedPos(stack, lockedEntity.getPositionVec());
                NBTUtil.setLockedDim(stack, lockedEntity.world.getDimensionKey().getLocation().toString());
            }
        }
    }

    private static boolean isBeyondTeleportDistance(Vector3d lockerPos, Vector3d lockedPos, int maxDistance) {
        return lockerPos.distanceTo(lockedPos) > maxDistance;
    }

    private static void teleportLockedEntity(PlayerEntity player, LivingEntity lockedEntity, int maxDistance) {
        Vector3d lockerPos = player.getPositionVec();
        Vector3d lockedPos = lockedEntity.getPositionVec();

        double x = clampToDistance(lockerPos.x, lockedPos.x, maxDistance);
        double y = clampToDistance(lockerPos.y, lockedPos.y, maxDistance);
        double z = clampToDistance(lockerPos.z, lockedPos.z, maxDistance);

        lockedEntity.setPositionAndUpdate(x, y, z);
    }

    private static double clampToDistance(double lockerCoord, double lockedCoord, int maxDistance) {
        if (lockedCoord > lockerCoord + maxDistance) {
            return lockerCoord + maxDistance;
        } else if (lockedCoord < lockerCoord - maxDistance) {
            return lockerCoord - maxDistance;
        } else {
            return lockedCoord;
        }
    }
}
