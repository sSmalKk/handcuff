package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;

public class KeyItem extends Item {
    public KeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack keyStack = player.getHeldItem(hand);

        if (!world.isRemote) {
            // Verificar a entidade que o jogador está clicando
            RayTraceResult rayTraceResult = player.pick(5.0D, 0.0F, false);
            if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if (entityRayTraceResult.getEntity() instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entityRayTraceResult.getEntity();
                    return tryUnlock(player, target, keyStack);
                }
            } else if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
                // Implementar lógica de desbloqueio com blocos, se necessário
            }

            // Verificar apenas a offhand do jogador
            return tryUnlock(player, null, keyStack);
        }

        return ActionResult.resultPass(keyStack);
    }

    private ActionResult<ItemStack> tryUnlock(PlayerEntity player, LivingEntity target, ItemStack keyStack) {
        ItemStack playerOffhand = player.getHeldItem(Hand.OFF_HAND);
        ItemStack targetOffhand = target != null ? target.getHeldItem(Hand.OFF_HAND) : ItemStack.EMPTY;

        // Verificar se a chave corresponde à algema
        if (playerOffhand.getItem() == ItemRegistry.HANDCUFFS.get() && NBTUtil.getLockKey(playerOffhand).equals(NBTUtil.getLockKey(keyStack))) {
            unlock(player, Hand.OFF_HAND, keyStack);
            return ActionResult.resultSuccess(keyStack);
        }

        if (!targetOffhand.isEmpty() && targetOffhand.getItem() == ItemRegistry.HANDCUFFS.get() && NBTUtil.getLockKey(targetOffhand).equals(NBTUtil.getLockKey(keyStack))) {
            unlock(target, Hand.OFF_HAND, keyStack);
            return ActionResult.resultSuccess(keyStack);
        }

        return ActionResult.resultPass(keyStack);
    }

    private void unlock(LivingEntity entity, Hand hand, ItemStack keyStack) {
        ItemStack handcuffs = entity.getHeldItem(hand);
        ItemStack openHandcuffs = new ItemStack(ItemRegistry.HANDCUFFSOPEN.get());

        // Copiar os NBTs necessários para as algemas abertas
        NBTUtil.setLockKey(openHandcuffs, NBTUtil.getLockKey(handcuffs));

        // Substituir as algemas pela versão aberta
        entity.setHeldItem(hand, openHandcuffs);

        // Remover a chave do inventário
        keyStack.shrink(1);
    }
}
