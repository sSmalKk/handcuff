package net.ssmalkk.handcuffmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Config COMMON;

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Config {
        public final ForgeConfigSpec.IntValue maxDistance;
        public final ForgeConfigSpec.BooleanValue teleportOnBreak;
        public final ForgeConfigSpec.IntValue teleportDistance;

        public Config(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            builder.comment("Handcuff settings").push("handcuff");
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
}
