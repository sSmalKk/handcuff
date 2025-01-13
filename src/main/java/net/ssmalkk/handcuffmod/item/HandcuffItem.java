package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.config.ConfigHandler;
import net.ssmalkk.handcuffmod.util.NBTUtil;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HandcuffItem extends GeoArmorItem implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final List<ParticleLine> activeParticleLines = new ArrayList<>();

    public HandcuffItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder.group(HandcuffMod.handcuffModItemGroup));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void registerControllers(AnimationData data) {
        // No animation controllers needed for this version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (player.world.isRemote) return; // Ignore client-side ticks

        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (chestItem.getItem() instanceof HandcuffItem && NBTUtil.isMain(chestItem)) {
            updateLockNBT(chestItem, player);

            if (player.isSneaking()) {
                saveLockedPosition(chestItem, player);
            }
        }
    }

    private static void updateLockNBT(ItemStack stack, PlayerEntity player) {
        UUID lockerUUID = NBTUtil.getLocker(stack);
        UUID lockedUUID = NBTUtil.getLockedEntity(stack);

        if (lockerUUID != null && lockedUUID != null) {
            NBTUtil.setLockerPos(stack, player.getPositionVec());
            NBTUtil.setLockerDim(stack, player.world.getDimensionKey().getLocation().toString());

            LivingEntity lockedEntity = findEntityByUUID(player, lockedUUID);

            if (lockedEntity != null) {
                NBTUtil.setLockedPos(stack, lockedEntity.getPositionVec());
                NBTUtil.setLockedDim(stack, lockedEntity.world.getDimensionKey().getLocation().toString());
                ServerWorld world = (ServerWorld) player.world.getServer().getWorld(player.world.getDimensionKey());

                if (world != null) {
                    Vector3d lockerPos = NBTUtil.getLockerPos(stack);
                    Vector3d lockedPos = NBTUtil.getLockedPos(stack);

                    if (lockerPos != null && lockedPos != null) {
                        spawnParticleLine(world, lockerPos, lockedPos);
                    }

                    // Teleportation logic
                    if (ConfigHandler.COMMON.teleportOnBreak.get() && isBeyondTeleportDistance(player.getPositionVec(), lockedEntity.getPositionVec(), ConfigHandler.COMMON.teleportDistance.get())) {
                        teleportLockedEntity(player, lockedEntity, ConfigHandler.COMMON.teleportDistance.get());
                    }
                }
            }
        }
    }

    private static void spawnParticleLine(ServerWorld world, Vector3d start, Vector3d end) {
        double distance = start.distanceTo(end);
        Vector3d direction = end.subtract(start).normalize();

        // Ajuste o número de partículas de acordo com a distância
        int numberOfParticles = (int) (distance * 2); // Ajuste conforme necessário

        for (int i = 0; i < numberOfParticles; i++) {
            double ratio = (double) i / (double) numberOfParticles;
            Vector3d particlePos = start.add(direction.scale(ratio * distance));

            ParticleLine particleLine = new ParticleLine(world, particlePos, direction);
            activeParticleLines.add(particleLine);
        }
    }

    private static LivingEntity findEntityByUUID(PlayerEntity player, UUID uuid) {
        for (LivingEntity entity : player.world.getEntitiesWithinAABB(LivingEntity.class, player.getBoundingBox().grow(32.0))) {
            if (entity.getUniqueID().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    private static void saveLockedPosition(ItemStack stack, PlayerEntity player) {
        UUID lockedUUID = NBTUtil.getLockedEntity(stack);

        if (lockedUUID != null) {
            LivingEntity lockedEntity = findEntityByUUID(player, lockedUUID);

            if (lockedEntity != null) {
                NBTUtil.setLockedPos(stack, lockedEntity.getPositionVec());
                NBTUtil.setLockedDim(stack, lockedEntity.world.getDimensionKey().getLocation().toString());
            }
        }
    }

    private static boolean isBeyondTeleportDistance(Vector3d lockerPos, Vector3d lockedPos, int maxDistance) {
        return lockerPos.distanceTo(lockedPos) > maxDistance;
    }

    private static void teleportLockedEntity(PlayerEntity player, LivingEntity lockedEntity, int maxDistance) {
        Vector3d lockerPos = player.getPositionVec();
        Vector3d lockedPos = lockedEntity.getPositionVec();

        double x = clampToDistance(lockerPos.x, lockedPos.x, maxDistance);
        double y = clampToDistance(lockerPos.y, lockedPos.y, maxDistance);
        double z = clampToDistance(lockerPos.z, lockedPos.z, maxDistance);

        lockedEntity.setPositionAndUpdate(x, y, z);
    }

    private static double clampToDistance(double lockerCoord, double lockedCoord, int maxDistance) {
        if (lockedCoord > lockerCoord + maxDistance) {
            return lockerCoord + maxDistance;
        } else if (lockedCoord < lockerCoord - maxDistance) {
            return lockerCoord - maxDistance;
        } else {
            return lockedCoord;
        }
    }

    private static class ParticleLine {
        private final ServerWorld world;
        private final Vector3d position;
        private final Vector3d direction;
        private int ticksExisted;

        public ParticleLine(ServerWorld world, Vector3d position, Vector3d direction) {
            this.world = world;
            this.position = position;
            this.direction = direction;
            this.ticksExisted = 0;
            spawnParticle();
        }

        public void tick() {
            ticksExisted++;
            if (ticksExisted > 1) {
                despawnParticle();
            }
        }

        private void spawnParticle() {
            world.spawnParticle(ParticleTypes.CRIT, position.x, position.y + 1.5, position.z, 0, 0, 0, 0, 1.0);
        }

        private void despawnParticle() {
            // Para desaparecer imediatamente, pode não ser necessário implementar nada aqui, pois as partículas de Minecraft
            // já têm duração muito curta.
        }
    }
}
