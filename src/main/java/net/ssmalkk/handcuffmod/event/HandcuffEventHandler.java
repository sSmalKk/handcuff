package net.ssmalkk.handcuffmod.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import net.ssmalkk.handcuffmod.util.NBTUtil;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HandcuffEventHandler {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        // Verifica se o item no slot do peito é uma instância de HandcuffsItem
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Aplica efeitos ao jogador se estiver algemado por trás
            if (isBehind) {
                applyHandcuffedEffects(player);

                // Bloqueia todas as interações se o jogador estiver algemado
                if (event instanceof PlayerInteractEvent.RightClickBlock ||
                        event instanceof PlayerInteractEvent.EntityInteract ||
                        event instanceof PlayerInteractEvent.RightClickItem ||
                        event instanceof PlayerInteractEvent.RightClickEmpty ||
                        event instanceof PlayerInteractEvent.LeftClickBlock ||
                        event instanceof PlayerInteractEvent.EntityInteractSpecific) {

                    // Verifica se o evento é cancelável antes de tentar cancelá-lo
                    if (event.isCancelable()) {
                        event.setCancellationResult(ActionResultType.FAIL);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGuiScreenOpen(GuiScreenEvent.InitGuiEvent.Post event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

            // Verifica se o item no slot do peito é uma instância de HandcuffsItem
            if (chestItem.getItem() instanceof HandcuffsItem) {
                boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

                // Fecha todas as HUDs se o jogador estiver algemado por trás
                if (isBehind) {
                    Screen gui = event.getGui();
                    Minecraft.getInstance().displayGuiScreen(null);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        // Verifica se o item no slot do peito é uma instância de HandcuffsItem
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Bloqueia a interação com entidades se o jogador estiver algemado por trás
            if (isBehind) {
                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        // Verifica se o item no slot do peito é uma instância de HandcuffsItem
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Bloqueia a quebra de blocos se o jogador estiver algemado por trás
            if (isBehind) {
                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        PlayerEntity player = event.getPlayer();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        // Verifica se o item no slot do peito é uma instância de HandcuffsItem
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isBehind = NBTUtil.isLockedFromBehind(chestItem);

            // Bloqueia a interação específica com entidades se o jogador estiver algemado por trás
            if (isBehind) {
                event.setCancellationResult(ActionResultType.FAIL);
                event.setCanceled(true);
            }
        }
    }

    private static void applyHandcuffedEffects(PlayerEntity player) {
        // Verifica se o jogador está no modo criativo ou tem uma chave no inventário
        if (player.isCreative() || checkForKeyInInventory(player)) {
            // Desbloqueia o jogador automaticamente
            player.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStack.EMPTY);
        } else {
            // Aplica Fadiga de Mineração II por 10 segundos
            EffectInstance fatigueEffect = new EffectInstance(Effects.MINING_FATIGUE, 200, 1);
            // Aplica Fraqueza II por 10 segundos
            EffectInstance weaknessEffect = new EffectInstance(Effects.WEAKNESS, 200, 1);

            // Aplica os efeitos ao jogador
            player.addPotionEffect(fatigueEffect);
            player.addPotionEffect(weaknessEffect);
        }
    }

    private static boolean checkForKeyInInventory(PlayerEntity player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() == ItemRegistry.KEY.get()) {
                return true;
            }
        }
        return false;
    }
}
