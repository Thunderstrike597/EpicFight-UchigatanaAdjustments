package net.kenji.ef_uchigatana_adjustments;

import com.asanginxst.epicfightx.gameassets.EFXSkillDataKeys;
import com.asanginxst.epicfightx.gameassets.EpicFightSkillsX;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.combos.HfBlade;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EpicFightUchigatanaAdjustments.MODID)
public class EpicFightUchigatanaAdjustments {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "ef_uchigatana_adjustments";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isLoaded(String modid) {
        return FMLLoader.getLoadingModList().getModFileById(modid) != null;
    }
    public static final boolean EFX_LOADED =
            isLoaded("epicfightx");
    public static final boolean SS_LOADED =
            isLoaded("sword_soaring");
    public static final boolean EFN_LOADED =
            isLoaded("efn");

    public EpicFightUchigatanaAdjustments() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigCommon.SPEC, "ef_uchigatana_adjustments-common.toml");
    }

    private void commonSetup(final FMLLoadCompleteEvent event) {
        if (EFX_LOADED) {
            ClientSheathSync.register(
                    () -> EpicFightSkillsX.BATTOJUTSU_PASSIVE,
                    EFXSkillDataKeys.SHEATH,
                    "sheath");
        }
        if (EFN_LOADED) {
            ClientSheathSync.register(
                    () -> HfBlade.HfBlade_Passive,
                    EFNSKillDataKeys.MURASAMA_SHEATH,
                    "murasama_sheath");
            // MurasamaPassive shares the same key and tag, so add a second entry with its own skill field
        }
    }

    // Add the example block item to the building blocks tab

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
