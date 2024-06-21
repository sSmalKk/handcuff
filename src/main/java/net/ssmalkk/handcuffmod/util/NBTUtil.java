package net.ssmalkk.handcuffmod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class NBTUtil {

    private static final String LOCKER_TAG = "Locker";
    private static final String LOCKED_ENTITY_TAG = "LockedEntity";
    private static final String LOCK_KEY_TAG = "LockKey";
    private static final String LOCKED_FROM_BEHIND_TAG = "LockedFromBehind";
    private static final String MAIN_TAG = "Main";

    public static void setLocker(ItemStack stack, String uuid) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString(LOCKER_TAG, uuid);
    }

    public static String getLocker(ItemStack stack) {
        return stack.getOrCreateTag().getString(LOCKER_TAG);
    }

    public static void setLockedEntity(ItemStack stack, String uuid) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString(LOCKED_ENTITY_TAG, uuid);
    }

    public static String getLockedEntity(ItemStack stack) {
        return stack.getOrCreateTag().getString(LOCKED_ENTITY_TAG);
    }

    public static void setLockKey(ItemStack stack, String key) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString(LOCK_KEY_TAG, key);
    }

    public static String getLockKey(ItemStack stack) {
        return stack.getOrCreateTag().getString(LOCK_KEY_TAG);
    }

    public static void setLockedFromBehind(ItemStack stack, boolean lockedFromBehind) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putBoolean(LOCKED_FROM_BEHIND_TAG, lockedFromBehind);
    }

    public static boolean isLockedFromBehind(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(LOCKED_FROM_BEHIND_TAG);
    }

    public static void setMain(ItemStack stack, boolean isMain) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putBoolean(MAIN_TAG, isMain);
    }

    public static boolean isMain(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(MAIN_TAG);
    }
}
