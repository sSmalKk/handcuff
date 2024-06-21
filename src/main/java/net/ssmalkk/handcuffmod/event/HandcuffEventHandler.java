package net.ssmalkk.handcuffmod.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import net.ssmalkk.handcuffmod.util.NBTUtil;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID)
public class HandcuffEventHandler {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        if (chestItem.getItem() instanceof HandcuffItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Bloquear interações com blocos, entidades e itens no mundo
            if (event instanceof PlayerInteractEvent.RightClickBlock ||
                    event instanceof PlayerInteractEvent.EntityInteract ||
                    event instanceof PlayerInteractEvent.RightClickItem ||
                    event instanceof PlayerInteractEvent.RightClickEmpty) {

                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }

            if (isBehind) {
                if (event instanceof PlayerInteractEvent.RightClickItem) {
                    ItemStack itemStack = event.getItemStack();
                    event.setCancellationResult(ActionResultType.FAIL);
                    event.setCanceled(true);
                }
            }
        }
    }
}
