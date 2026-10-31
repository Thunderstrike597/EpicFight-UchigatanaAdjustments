package net.kenji.ef_uchigatana_adjustments;

import com.asanginxst.epicfightx.gameassets.EpicFightSkillsX;
import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import net.kenji.ef_uchigatana_adjustments.compat.EFXCompat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jline.utils.Log;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import static net.kenji.ef_uchigatana_adjustments.EpicFightUchigatanaAdjustments.EFX_LOADED;

@Mod.EventBusSubscriber(modid = EpicFightUchigatanaAdjustments.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UchigatanaSheathManager {

    private static final String TAG_SHEATHED = "ef_uchi_last_sheathed"; // last sheath state while holding battojutsu
    private static final String TAG_WAS_BATTO = "ef_uchi_was_batto";    // was the passive battojutsu last tick?

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return; // server only

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        SkillContainer container = patch.getSkill(SkillSlots.WEAPON_PASSIVE);
        if (container == null) return; // note: no return on getSkill() == null

        CompoundTag data = player.getPersistentData();
        boolean isBatto = isBatto(container);
        boolean wasBatto = data.getBoolean(TAG_WAS_BATTO);

        if (isBatto) {
            // just swapped TO a battojutsu item, and it was sheathed when we left it
            if (!wasBatto && data.getBoolean(TAG_SHEATHED)) {
                restoreSheath(container);
            }
            // keep the memory in sync with the live state
            data.putBoolean(TAG_SHEATHED, isSheathed(container));
        }

        if (isBatto != wasBatto) {
            data.putBoolean(TAG_WAS_BATTO, isBatto);
        }


    }


    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> getUchiBlockAnim(){
        ResourceLocation animationLoc = ResourceLocation.tryParse(ConfigCommon.SHEATHED_GUARD_ANIMATION.get());
        if(animationLoc != null && AnimationManager.byKey(animationLoc) != null) {
            return AnimationManager.byKey(animationLoc);
        }
        if(EFX_LOADED){
            return AnimationsX.SWORD_DUAL_GUARD;
        }
        return Animations.SWORD_DUAL_GUARD;
    }


    public static boolean isSheathed(SkillContainer container) {
        if (EFX_LOADED && EFXCompat.isEFXSkill(container)) {
            return EFXCompat.isSheathed(container);
        }
        return Boolean.TRUE.equals(container.getDataManager().getDataValue(SkillDataKeys.SHEATH.get()));
    }

    private static void restoreSheath(SkillContainer container) {
        if (EFX_LOADED && EFXCompat.isEFXSkill(container)) {
            EFXCompat.restoreSheath(container);
            return;
        }
        Log.info("logging Sheath");
        container.getDataManager().setDataSync(SkillDataKeys.SHEATH.get(), true);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
        container.getSkill().setConsumptionSynchronize(container, container.getMaxResource());
    }

    private static boolean isBatto(SkillContainer container){
        boolean isRegularBatto = container.getSkill() == EpicFightSkills.BATTOJUTSU_PASSIVE;
        boolean epicFightXBatto = false;
        if(EFX_LOADED){
            epicFightXBatto = container.getSkill() == EpicFightSkillsX.BATTOJUTSU_PASSIVE;
        }
        return isRegularBatto || epicFightXBatto;
    }
}