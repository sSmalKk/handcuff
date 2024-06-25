package net.ssmalkk.handcuffmod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

import java.util.UUID;

public class NBTUtil {
    private static final String LOCKER = "Locker";
    private static final String LOCKED_ENTITY = "LockedEntity";
    private static final String LOCK_KEY = "LockKey";
    private static final String LOCKER_POS = "LockerPos";
    private static final String LOCKER_DIM = "LockerDim";
    private static final String LOCKED_POS = "LockedPos";
    private static final String LOCKED_DIM = "LockedDim";
    private static final String LOCKED_FROM_BEHIND = "LockedFromBehind";
    private static final String IS_MAIN = "IsMain";

    public static void setLocker(ItemStack stack, UUID uuid) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putUniqueId(LOCKER, uuid);
    }

    public static UUID getLocker(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getUniqueId(LOCKER) : null;
    }

    public static void setLockKey(ItemStack stack, String key) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(LOCK_KEY, key);
    }

    public static String getLockKey(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getString(LOCK_KEY) : "";
    }

    public static void setLockerPos(ItemStack stack, Vector3d pos) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putDouble(LOCKER_POS + "X", pos.x);
        tag.putDouble(LOCKER_POS + "Y", pos.y);
        tag.putDouble(LOCKER_POS + "Z", pos.z);
    }

    public static Vector3d getLockerPos(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains(LOCKER_POS + "X") && tag.contains(LOCKER_POS + "Y") && tag.contains(LOCKER_POS + "Z")) {
            double x = tag.getDouble(LOCKER_POS + "X");
            double y = tag.getDouble(LOCKER_POS + "Y");
            double z = tag.getDouble(LOCKER_POS + "Z");
            return new Vector3d(x, y, z);
        }
        return Vector3d.ZERO;
    }

    public static void setLockerDim(ItemStack stack, String dim) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(LOCKER_DIM, dim);
    }

    public static String getLockerDim(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getString(LOCKER_DIM) : "";
    }

    public static void setLockedPos(ItemStack stack, Vector3d pos) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putDouble(LOCKED_POS + "X", pos.x);
        tag.putDouble(LOCKED_POS + "Y", pos.y);
        tag.putDouble(LOCKED_POS + "Z", pos.z);
    }

    public static Vector3d getLockedPos(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains(LOCKED_POS + "X") && tag.contains(LOCKED_POS + "Y") && tag.contains(LOCKED_POS + "Z")) {
            double x = tag.getDouble(LOCKED_POS + "X");
            double y = tag.getDouble(LOCKED_POS + "Y");
            double z = tag.getDouble(LOCKED_POS + "Z");
            return new Vector3d(x, y, z);
        }
        return Vector3d.ZERO;
    }

    public static void setLockedDim(ItemStack stack, String dim) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(LOCKED_DIM, dim);
    }

    public static String getLockedDim(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getString(LOCKED_DIM) : "";
    }

    public static void setLockedFromBehind(ItemStack stack, boolean locked) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean(LOCKED_FROM_BEHIND, locked);
    }

    public static boolean isLockedFromBehind(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains(LOCKED_FROM_BEHIND) && tag.getBoolean(LOCKED_FROM_BEHIND);
    }

    public static void setLockedEntity(ItemStack stack, UUID uuid) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putUniqueId(LOCKED_ENTITY, uuid);
    }

    public static UUID getLockedEntity(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getUniqueId(LOCKED_ENTITY) : null;
    }

    public static void setMain(ItemStack stack, boolean isMain) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean(IS_MAIN, isMain);
    }

    public static boolean isMain(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains(IS_MAIN) && tag.getBoolean(IS_MAIN);
    }
}
