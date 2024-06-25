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
}
