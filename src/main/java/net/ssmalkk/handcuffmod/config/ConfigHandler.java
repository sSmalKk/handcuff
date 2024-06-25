package net.ssmalkk.handcuffmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

public class ConfigHandler {
    public static final String CATEGORY_GENERAL = "general";
    public static final String SUBCATEGORY_HANDCUFF = "handcuff";

    public static ForgeConfigSpec.IntValue maxDistance;
    public static ForgeConfigSpec.BooleanValue teleportOnBreak;
    public static ForgeConfigSpec.IntValue teleportDistance;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.comment("General settings").push(CATEGORY_GENERAL);

        builder.comment("Handcuff settings").push(SUBCATEGORY_HANDCUFF);
        maxDistance = builder
                .comment("Maximum distance for handcuffs to be effective (in blocks)")
                .defineInRange("maxDistance", 50, 1, 1000);
        teleportOnBreak = builder
                .comment("Whether to teleport the player when the handcuffs break")
                .define("teleportOnBreak", true);
        teleportDistance = builder
                .comment("Distance to teleport the player when the handcuffs break (in blocks)")
                .defineInRange("teleportDistance", 10, 1, 1000);
        builder.pop();

        builder.pop();
    }
}
