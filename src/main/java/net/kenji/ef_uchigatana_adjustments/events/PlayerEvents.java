package net.kenji.ef_uchigatana_adjustments.events;

import com.hm.efn.client.renderer.geoEntity.HfBladeChargingRenderer;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.gameasset.EFNWeaponCapabilityPresets;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.gameasset.combos.Murasama;
import net.kenji.ef_uchigatana_adjustments.EpicFightUchigatanaAdjustments;
import net.kenji.ef_uchigatana_adjustments.UchigatanaSheathManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataKeys;

@Mod.EventBusSubscriber(modid = EpicFightUchigatanaAdjustments.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        UchigatanaSheathManager.onPlayerTick(event, EpicFightSkills.BATTOJUTSU_PASSIVE, SkillDataKeys.SHEATH, "uchi", "batto", null);
        if(EpicFightUchigatanaAdjustments.EFN_LOADED)
            UchigatanaSheathManager.onPlayerTick(event, HfBlade.HfBlade_Passive, EFNSKillDataKeys.MURASAMA_SHEATH, "hf_blade", "hf_blade_skill", "murasama_sheath");



    }


}
