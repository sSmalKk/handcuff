package net.ssmalkk.handcuffmod.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import net.ssmalkk.handcuffmod.util.NBTUtil;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID)
public class HandcuffEventHandler {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        // Verifica se o item no slot do peito é uma instância de HandcuffsItem (no plural)
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Bloqueia todas as interações de bloco, entidade e item no mundo
            if (event instanceof PlayerInteractEvent.RightClickBlock ||
                    event instanceof PlayerInteractEvent.EntityInteract ||
                    event instanceof PlayerInteractEvent.RightClickItem ||
                    event instanceof PlayerInteractEvent.RightClickEmpty) {

                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }

            // Bloqueia a interação com itens na mão se estiver algemado por trás
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
