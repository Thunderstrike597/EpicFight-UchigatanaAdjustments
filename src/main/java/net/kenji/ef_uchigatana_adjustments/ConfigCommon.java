package net.kenji.ef_uchigatana_adjustments;

import net.minecraftforge.common.ForgeConfigSpec;

// Client-side config, registered in the main class as ModConfig.Type.CLIENT
public class ConfigCommon {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Boolean> GUARD_KEEP_SHEATHED;
    public static final ForgeConfigSpec.ConfigValue<String> SHEATHED_GUARD_ANIMATION;

    public static final ForgeConfigSpec SPEC;



    static {
        GUARD_KEEP_SHEATHED = BUILDER.comment("Whether To Keep The Uchigatana/Battojutsu Weapon Sheathed When Blocking").define(
                "Keep Sheathed During Guard",
                true);
        SHEATHED_GUARD_ANIMATION = BUILDER.comment("The animation which should be used when guarding while sheathed (Only applicable if the Config Option 'Keep Sheathed During Guard' is set to 'true')").define(
                "Sheathed Guard Animation",
                "biped/skill/guard_dualsword");

        SPEC = BUILDER.build();
    }
}
