package net.ssmalkk.handcuffmod.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.item.ItemStack;

public class NBTUtil {
    public static void setLockData(ItemStack stack, String key, String value) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putString(key, value);
        stack.setTag(nbt);
    }

    public static String getLockData(ItemStack stack, String key) {
        CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.contains(key) ? nbt.getString(key) : "";
    }

    public static void setLockKey(ItemStack stack, String key) {
        setLockData(stack, "lockKey", key);
    }

    public static String getLockKey(ItemStack stack) {
        return getLockData(stack, "lockKey");
    }

    public static void setLockedEntity(ItemStack stack, String entity) {
        setLockData(stack, "lockedEntity", entity);
    }

    public static String getLockedEntity(ItemStack stack) {
        return getLockData(stack, "lockedEntity");
    }

    public static void setLocker(ItemStack stack, String entity) {
        setLockData(stack, "locker", entity);
    }

    public static String getLocker(ItemStack stack) {
        return getLockData(stack, "locker");
    }

    public static void setMain(ItemStack stack, boolean isMain) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putBoolean("main", isMain);
        stack.setTag(nbt);
    }

    public static boolean isMain(ItemStack stack) {
        CompoundNBT nbt = stack.getTag();
        return nbt != null && nbt.contains("main") && nbt.getBoolean("main");
    }
}
