package net.kenji.ef_uchigatana_adjustments;

import net.minecraftforge.common.ForgeConfigSpec;

// Client-side config, registered in the main class as ModConfig.Type.CLIENT
public class ConfigCommon {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<Boolean> GUARD_KEEP_SHEATHED;
    public static final ForgeConfigSpec.ConfigValue<String> SHEATHED_GUARD_ANIMATION;
    public static final ForgeConfigSpec.ConfigValue<Boolean> EFN_SHEATHED_HF_BLADE_COMPAT;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DISBALE_HF_BLADE_COUNTER_SOUND;
    public static final ForgeConfigSpec.ConfigValue<Boolean> CHANGE_HF_BLADE_DOUBLE_JUMP_ANIM;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SWAP_HF_BLADE_LIVING_ANIMATIONS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> UNSHEATH_ON_INTERRUPTED_SHEATH;

    public static final ForgeConfigSpec SPEC;



    static {
        GUARD_KEEP_SHEATHED = BUILDER.comment("Whether To Keep The Uchigatana/Battojutsu Weapon Sheathed When Blocking").define(
                "Keep Sheathed During Guard",
                true);
        SHEATHED_GUARD_ANIMATION = BUILDER.comment("The animation which should be used when guarding while sheathed (Only applicable if the Config Option 'Keep Sheathed During Guard' is set to 'true')").define(
                "Sheathed Guard Animation",
                "biped/skill/guard_dualsword");
        BUILDER.push("Compat");
            BUILDER.push("Epic Fight - Nightfall");
            EFN_SHEATHED_HF_BLADE_COMPAT = BUILDER.comment("Whether to keep the add compat for Epic Fight - Nightfall. If 'true' the HF-Blade & HF-Murasama weapons from the mod will remain sheathed on weapon switch").define(
                    "Epic Fight - Nightfall Sheathed Hf-Blade Compat",
                    true);
            DISBALE_HF_BLADE_COUNTER_SOUND = BUILDER.comment("Whether to disable the sheathed counter 'ding' sound from Epic Fight - Nightfall's 'HF-Blade'").define(
                    "Disable EFN Hf Blade Ding Sound",
                    true);
            CHANGE_HF_BLADE_DOUBLE_JUMP_ANIM = BUILDER.comment("Whether or not the double jump anim for the HF-Blade & HF-Murasama will be replaced with Epic Fight's 'Flip Animation' (From the Phantom Ascension Skill)").define(
                    "Change HF-Blade Double Jump Animation",
                    true);
            SWAP_HF_BLADE_LIVING_ANIMATIONS = BUILDER.comment("Whether or not some living animations on the HF-Blade should use Epic Fight - Extra's living animations instead(Epic Fight - Extra Must Be Present To Work)").define(
                    "Swap HF-Blade Living Animations",
                    true);
            UNSHEATH_ON_INTERRUPTED_SHEATH = BUILDER.comment("If a sheathing animation is interrupted before the weapon actually reaches the sheath (dodge, double jump, etc.), set the weapon to unsheathed instead of forcing the sheathed render. If 'false', the weapon is rendered as sheathed.").define(
                    "Unsheath When Sheathing Is Interrupted",
                    true);
            BUILDER.pop();
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
