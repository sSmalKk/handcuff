package net.ssmalkk.handcuffmod.config;

import it.unimi.dsi.fastutil.BigList;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHandler {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Config COMMON;
    public static final int MAX_DISTANCE = 5;

    static {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Config {
        public final ForgeConfigSpec.DoubleValue MAX_DISTANCE;

        public Config(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            MAX_DISTANCE = builder
                    .comment("Maximum distance before the locked entity is teleported back to the player")
                    .defineInRange("maxDistance", 50.0, 1.0, 1000.0);
            builder.pop();
        }
    }
}
