package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

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
        NBTUtil.setLocker(handcuffs, player.getUniqueID().toString());
        NBTUtil.setLockedEntity(handcuffs, player.getUniqueID().toString());
        NBTUtil.setLockKey(handcuffs, generateLockKey());

        // Remove qualquer item atualmente na offhand e dropa no chão
        ItemStack offhandItem = player.getHeldItem(Hand.OFF_HAND);
        if (!offhandItem.isEmpty()) {
            player.dropItem(offhandItem, false);
        }

        // Coloca as algemas na offhand do jogador
        player.setHeldItem(Hand.OFF_HAND, handcuffs);

        // Cria uma chave com os mesmos NBTs das algemas
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, NBTUtil.getLockKey(handcuffs));
        NBTUtil.setLocker(key, player.getUniqueID().toString()); // Define o jogador como o trancador
        NBTUtil.setLockedEntity(key, player.getUniqueID().toString()); // Define o próprio jogador como a entidade trancada

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
                    // Se estiver mirando e pressionando shift, tranca o jogador com a entidade
                    lockEntityWithMain(player, target, stack);
                } else {
                    // Se estiver mirando mas não pressionando shift, tranca o jogador e a entidade juntos
                    lockEntityTogether(player, target, stack);
                }
            } else {
                // Se não estiver mirando em uma entidade, trata como um clique no ar normal
                useItemInAir(player, stack);
            }
        }

        return ActionResult.resultPass(stack);
    }


    // Função para encontrar uma entidade alvo
    private LivingEntity findTargetEntity(PlayerEntity player) {
        World world = player.world;
        AxisAlignedBB aabb = new AxisAlignedBB(player.getPosX() - 5, player.getPosY() - 5, player.getPosZ() - 5, player.getPosX() + 5, player.getPosY() + 5, player.getPosZ() + 5);
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, aabb);

        for (LivingEntity entity : entities) {
            if (entity instanceof ZombieEntity || entity instanceof SkeletonEntity || entity instanceof VillagerEntity || (entity instanceof PlayerEntity && entity != player)) {
                return entity;
            }
        }
        return null;
    }

    // Função para trancar uma entidade e manter uma cópia com o jogador (main true)
    private void lockEntityWithMain(PlayerEntity player, LivingEntity target, ItemStack stack) {
        ItemStack handcuff = new ItemStack(ItemRegistry.HANDCUFF.get());
        String lockKey = generateLockKey();

        // Atualiza os NBTs com informações da chave e quem trancou quem
        NBTUtil.setLocker(handcuff, player.getUniqueID().toString());
        NBTUtil.setLockedEntity(handcuff, target.getUniqueID().toString());
        NBTUtil.setLockKey(handcuff, lockKey);

        // Remove qualquer item atualmente na offhand do alvo e dropa no chão
        ItemStack offhandItem = target.getHeldItem(Hand.OFF_HAND);
        if (!offhandItem.isEmpty()) {
            target.entityDropItem(offhandItem);
        }

        // Coloca as algemas na offhand do alvo
        target.setHeldItem(Hand.OFF_HAND, handcuff);

        // Cria uma cópia das algemas para o jogador
        ItemStack handcuffCopy = handcuff.copy();
        NBTUtil.setMain(handcuffCopy, true);

        // Remove qualquer item atualmente na offhand do jogador e dropa no chão
        ItemStack playerOffhandItem = player.getHeldItem(Hand.OFF_HAND);
        if (!playerOffhandItem.isEmpty()) {
            player.dropItem(playerOffhandItem, false);
        }

        // Coloca a cópia das algemas na offhand do jogador
        player.setHeldItem(Hand.OFF_HAND, handcuffCopy);

        // Entrega a chave ao jogador
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, lockKey);
        NBTUtil.setLocker(key, player.getUniqueID().toString()); // Define o jogador como o trancador
        NBTUtil.setLockedEntity(key, target.getUniqueID().toString()); // Define o alvo como a entidade trancada

        // Adiciona a chave ao inventário do jogador ou dropa no chão se estiver cheio
        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        // Remove as algemas abertas
        stack.shrink(1);
    }


    // Função para trancar uma entidade sozinha
    // Função para trancar uma entidade sozinha
    public void lockEntityTogether(PlayerEntity player, LivingEntity target, ItemStack stack) {
        ItemStack handcuffs = new ItemStack(ItemRegistry.HANDCUFFS.get());
        String lockKey = generateLockKey();

        // Atualiza os NBTs com informações da chave e quem trancou quem
        NBTUtil.setLocker(handcuffs, player.getUniqueID().toString());
        NBTUtil.setLockedEntity(handcuffs, target.getUniqueID().toString());
        NBTUtil.setLockKey(handcuffs, lockKey);

        // Remove qualquer item atualmente na offhand do alvo e dropa no chão
        ItemStack offhandItem = target.getHeldItem(Hand.OFF_HAND);
        if (!offhandItem.isEmpty()) {
            target.entityDropItem(offhandItem);
        }

        // Coloca as algemas na offhand do alvo
        target.setHeldItem(Hand.OFF_HAND, handcuffs);

        // Entrega a chave ao jogador
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, lockKey);
        NBTUtil.setLocker(key, player.getUniqueID().toString()); // Define o jogador como o trancador
        NBTUtil.setLockedEntity(key, target.getUniqueID().toString()); // Define o alvo como a entidade trancada

        // Adiciona a chave ao inventário do jogador ou dropa no chão se estiver cheio
        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        // Remove as algemas abertas
        stack.shrink(1);
    }

}
