package net.ssmalkk.handcuffmod.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import net.ssmalkk.handcuffmod.util.NBTUtil;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventSubscriber {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;

        // Check if the player has handcuffs equipped
        ItemStack chestSlotStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (chestSlotStack.getItem() instanceof HandcuffItem) {
            HandcuffItem handcuffItem = (HandcuffItem) chestSlotStack.getItem();

            // Get UUIDs from NBT
            String lockerUUID = NBTUtil.getLocker(chestSlotStack);
            String lockedEntityUUID = NBTUtil.getLockedEntity(chestSlotStack);

            // Find the entities by UUID
            PlayerEntity locker = player.world.getPlayerByUuid(UUID.fromString(lockerUUID));
            PlayerEntity lockedEntity = player.world.getPlayerByUuid(UUID.fromString(lockedEntityUUID));

            // Check if both entities are valid and different
            if (locker != null && lockedEntity != null && locker != lockedEntity) {
                // Calculate distance between locker and lockedEntity
                double distance = locker.getDistance(lockedEntity);

                // If the distance is greater than 5 blocks, teleport the locked entity
                if (distance > 5) {
                    // Teleport lockedEntity near the locker within the same dimension
                    Vector3d teleportPos = locker.getPositionVec().subtract(0, 1, 0);
                    lockedEntity.teleportKeepLoaded(teleportPos.x, teleportPos.y, teleportPos.z);

                    // You may also need to update the entity's rotation if necessary
                    lockedEntity.rotationYaw = locker.rotationYaw;
                    lockedEntity.rotationPitch = locker.rotationPitch;
                }
            }
        }
    }
}
