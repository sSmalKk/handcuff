package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import net.ssmalkk.handcuffmod.util.NBTUtil;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class HandcuffItem extends GeoArmorItem implements HandcuffItemInterfce, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory((IAnimatable) this);

    public HandcuffItem(ArmorMaterial diamond, EquipmentSlotType chest, Properties builder) {
        super(HandcuffMod.handcuffMaterial, EquipmentSlotType.CHEST, builder.group(HandcuffMod.handcuffModItemGroup));
    }

    @Override
    public void onArmorTick(ItemStack stack, ServerWorld world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (!world.isRemote()) {
            BlockPos blockPos = player.getPosition();
            Vector3d lookVec = player.getLookVec();
            BlockPos knotPos = blockPos.offset(player.getHorizontalFacing());

            // Check if there is already a leash knot at the targeted position
            if (!world.isAirBlock(knotPos) || !world.isAirBlock(knotPos.up())) {
                return;
            }

            // Find the entity to be leashed (for example, a nearby animal)
            Entity leashedEntity = findEntityToLeash(world, blockPos, player);

            if (leashedEntity != null) {
                // Create a new leash knot entity
                LeashKnotEntity leashKnot = LeashKnotEntity.create(world, knotPos);

                // Leash the entity to the leash knot
                leashedEntity.startRiding(leashKnot, true);

                // Teleport the player 1.5 blocks ahead in the facing direction
                Vector3d newPos = player.getPositionVec().add(lookVec.scale(1.5));
                player.setPosition(newPos.x, newPos.y, newPos.z);

                // Spawn the leash knot entity in the world
                world.addEntity(leashKnot);
            }
        }
    }

    // Example method to find a nearby entity to leash
    private Entity findEntityToLeash(ServerWorld world, BlockPos blockPos, PlayerEntity player) {
        // Replace this with your own logic to find the appropriate entity
        // For example, find a nearby animal entity
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos).grow(10.0));
        for (Entity entity : entities) {
            if (entity instanceof AnimalEntity && entity.isAlive()) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            LivingEntity target = findTargetEntity(player);
            if (target instanceof PlayerEntity && target != player) {
                lockPlayersTogether(player, (PlayerEntity) target, stack);
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
            if (entity instanceof PlayerEntity && entity != player) {
                return entity;
            }
        }
        return null;
    }

    // Função para trancar dois jogadores juntos
    private void lockPlayersTogether(PlayerEntity player, PlayerEntity target, ItemStack stack) {
        ItemStack handcuffs = new ItemStack(ItemRegistry.HANDCUFFS.get());
        String lockKey = generateLockKey();

        // Define o NBT para o jogador principal e o alvo
        NBTUtil.setLocker(handcuffs, player.getUniqueID().toString());
        NBTUtil.setLockedEntity(handcuffs, target.getUniqueID().toString());
        NBTUtil.setLockKey(handcuffs, lockKey);

        // Define se está preso pela frente ou por trás
        boolean isBehind = isPlayerBehindEntity(player, target);
        NBTUtil.setLockedFromBehind(handcuffs, isBehind);

        // Coloca as algemas na armorslot do jogador principal
        player.setItemStackToSlot(EquipmentSlotType.CHEST, handcuffs);

        // Cria uma cópia das algemas para o jogador alvo
        ItemStack handcuffCopy = handcuffs.copy();
        NBTUtil.setMain(handcuffCopy, true);

        // Coloca a cópia das algemas na armorslot do jogador alvo
        target.setItemStackToSlot(EquipmentSlotType.CHEST, handcuffCopy);

        // Entrega a chave ao jogador principal
        ItemStack key = new ItemStack(ItemRegistry.KEY.get());
        NBTUtil.setLockKey(key, lockKey);
        NBTUtil.setLocker(key, player.getUniqueID().toString()); // Define o jogador como o trancador
        NBTUtil.setLockedEntity(key, target.getUniqueID().toString()); // Define o alvo como a entidade trancada

        // Adiciona a chave ao inventário do jogador principal ou dropa no chão se estiver cheio
        if (!player.inventory.addItemStackToInventory(key)) {
            player.dropItem(key, false);
        }

        // Remove as algemas abertas
        stack.shrink(1);
    }

    // Função para gerar uma chave única
    private String generateLockKey() {
        return java.util.UUID.randomUUID().toString();
    }

    // Função para verificar se o jogador está atrás do alvo
    private boolean isPlayerBehindEntity(PlayerEntity player, LivingEntity target) {
        Vector3d playerViewVec = player.getLookVec().normalize();
        Vector3d targetVec = target.getPositionVec().subtract(player.getPositionVec()).normalize();
        double dotProduct = playerViewVec.dotProduct(targetVec);
        return dotProduct < 0; // Se o produto escalar for negativo, o jogador está atrás da entidade
    }
}
