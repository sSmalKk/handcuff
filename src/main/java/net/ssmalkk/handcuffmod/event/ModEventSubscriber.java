package net.ssmalkk.handcuffmod.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import net.ssmalkk.handcuffmod.util.NBTUtil;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventSubscriber {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;

        // Verifica se o jogador está usando algemas
        if (player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == ItemRegistry.HANDCUFFS.get()) {
            ItemStack handcuffs = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

            String lockerUUID = NBTUtil.getLocker(handcuffs);
            String lockedEntityUUID = NBTUtil.getLockedEntity(handcuffs);

            // Encontra os jogadores
            PlayerEntity locker = player.world.getPlayerByUuid(java.util.UUID.fromString(lockerUUID));
            PlayerEntity lockedEntity = player.world.getPlayerByUuid(java.util.UUID.fromString(lockedEntityUUID));

            if (locker != null && lockedEntity != null) {
                double distance = locker.getDistance(lockedEntity);

                // Se a distância for maior que 5 blocos, teleporta o jogador trancado
                if (distance > 5) {
                    Vector3d teleportPos = locker.getPositionVec().subtract(0, 1, 0); // Teleporta para debaixo do jogador principal
                    lockedEntity.setPositionAndUpdate(teleportPos.x, teleportPos.y, teleportPos.z);
                }
            }
        }
    }
}
