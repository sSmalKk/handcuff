package net.ssmalkk.handcuffmod.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import net.ssmalkk.handcuffmod.util.NBTUtil;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HandcuffEventHandler {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        // Verifica se o item no slot do peito é uma instância de HandcuffsItem (no plural)
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Aplica Fatigue II e Weakness II se estiver algemado por trás
            if (isBehind) {
                // Efeito de Fatigue II (Fadiga II) por 10 segundos
                EffectInstance fatigueEffect = new EffectInstance(Effects.MINING_FATIGUE, 200, 1);
                // Efeito de Weakness II (Fraqueza II) por 10 segundos
                EffectInstance weaknessEffect = new EffectInstance(Effects.WEAKNESS, 200, 1);

                // Aplica os efeitos ao jogador
                player.addPotionEffect(fatigueEffect);
                player.addPotionEffect(weaknessEffect);
            }

            // Bloqueia todas as interações de bloco, entidade e item no mundo
            if (event instanceof PlayerInteractEvent.RightClickBlock ||
                    event instanceof PlayerInteractEvent.EntityInteract ||
                    event instanceof PlayerInteractEvent.RightClickItem ||
                    event instanceof PlayerInteractEvent.RightClickEmpty ||
                    event instanceof PlayerInteractEvent.LeftClickBlock ||
                    event instanceof PlayerInteractEvent.LeftClickEmpty ||
                    event instanceof PlayerInteractEvent.EntityInteractSpecific) {

                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }

            // Bloqueia a interação com itens na mão se estiver algemado por trás
            if (isBehind && event instanceof PlayerInteractEvent.RightClickItem) {
                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }
        }
    }
}
