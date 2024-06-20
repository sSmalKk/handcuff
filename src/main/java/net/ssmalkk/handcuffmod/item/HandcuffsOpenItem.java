package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HandcuffsOpenItem extends Item implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffsOpenItem(Properties properties) {
        super(properties.maxStackSize(1)); // Limitar a 1 por pilha
    }

    @Override
    public void registerControllers(final AnimationData data) {
        // No need for any animation controllers in this simplified version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    // Função para usar o item no ar
    public void useItemInAir(PlayerEntity player, ItemStack stack) {
        lockSelf(player, stack);
    }

    // Função para trancar o próprio jogador
    private void lockSelf(PlayerEntity player, ItemStack stack) {
        ItemStack handcuffs = new ItemStack(ItemRegistry.HANDCUFFS.get());

        // Atualiza os NBTs com informações da chave e quem trancou quem
        NBTUtil.setLocker(handcuffs, player.getDisplayName().getString());
        NBTUtil.setLockedEntity(handcuffs, player.getDisplayName().getString());
        NBTUtil.setLockKey(handcuffs, generateLockKey());

        // Coloca as algemas na mão principal do jogador
        player.setHeldItem(Hand.MAIN_HAND, handcuffs);

        // Cria uma chave com os mesmos NBTs das algemas
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, NBTUtil.getLockKey(handcuffs));

        // Tenta adicionar a chave no inventário do jogador
        if (!player.inventory.addItemStackToInventory(key)) {
            // Se o inventário estiver cheio, joga a chave no chão
            player.dropItem(key, false);
        }

        // Remove as algemas abertas
        stack.shrink(1);
    }

    // Função para gerar uma chave única
    private String generateLockKey() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            // Verifica se o jogador está mirando em uma entidade
            LivingEntity target = findTargetEntity(player);
            if (target != null) {
                if (player.isSneaking()) {
                    lockEntityWithMain(player, target, stack);
                } else {
                    lockEntityAlone(player, target, stack);
                }
            } else {
                // Se não estiver mirando em uma entidade, tranca o próprio jogador
                useItemInAir(player, stack);
            }
        }

        return ActionResult.resultPass(stack);
    }

    // Função fictícia para encontrar uma entidade alvo (necessário implementar a lógica real)
    private LivingEntity findTargetEntity(PlayerEntity player) {
        // Implementar lógica de encontrar a entidade alvo
        return null;
    }

    // Função para usar o item em outro player
    private void lockEntityWithMain(PlayerEntity player, LivingEntity target, ItemStack stack) {
        ItemStack handcuff = new ItemStack(ItemRegistry.HANDCUFFS.get());
        NBTUtil.setLocker(handcuff, player.getDisplayName().getString());
        NBTUtil.setLockedEntity(handcuff, target.getDisplayName().getString());
        NBTUtil.setLockKey(handcuff, generateLockKey());
        NBTUtil.setMain(handcuff, true);

        // Entrega as algemas ao alvo
        target.entityDropItem(handcuff);

        // Entrega a chave ao jogador
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, NBTUtil.getLockKey(handcuff));
        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        // Remove as algemas abertas
        stack.shrink(1);
    }

    // Função para trancar uma entidade sozinha
    public void lockEntityAlone(PlayerEntity player, LivingEntity target, ItemStack stack) {
        ItemStack handcuffs = new ItemStack(ItemRegistry.HANDCUFFS.get());

        // Atualiza os NBTs com informações da chave e quem trancou quem
        NBTUtil.setLocker(handcuffs, player.getDisplayName().getString());
        NBTUtil.setLockedEntity(handcuffs, target.getDisplayName().getString());
        NBTUtil.setLockKey(handcuffs, generateLockKey());

        // Entrega as algemas ao alvo
        target.entityDropItem(handcuffs);

        // Entrega a chave ao jogador
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, NBTUtil.getLockKey(handcuffs));
        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        // Remove as algemas abertas
        stack.shrink(1);
    }
}
