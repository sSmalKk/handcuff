package net.ssmalkk.handcuffmod.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class NBTUtil {
    private static final String LOCKER_KEY = "Locker";
    private static final String LOCKED_ENTITY_KEY = "LockedEntity";
    private static final String LOCK_KEY = "LockKey";

    public static void setLocker(ItemStack stack, String locker) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(LOCKER_KEY, locker);
    }

    public static String getLocker(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getString(LOCKER_KEY) : "";
    }

    public static void setLockedEntity(ItemStack stack, String lockedEntity) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(LOCKED_ENTITY_KEY, lockedEntity);
    }

    public static String getLockedEntity(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getString(LOCKED_ENTITY_KEY) : "";
    }

    public static void setLockKey(ItemStack stack, String lockKey) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putString(LOCK_KEY, lockKey);
    }

    public static String getLockKey(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getString(LOCK_KEY) : "";
    }

    public static void setMain(ItemStack stack, boolean isMain) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putBoolean("IsMain", isMain);
    }
}
