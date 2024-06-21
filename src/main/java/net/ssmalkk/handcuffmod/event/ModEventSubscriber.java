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

        // Check if the player has handcuff equipped
        ItemStack chestSlotStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (chestSlotStack.getItem() instanceof HandcuffItem) {
        }
    }
}
