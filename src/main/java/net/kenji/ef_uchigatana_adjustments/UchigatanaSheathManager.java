package net.kenji.ef_uchigatana_adjustments;

import com.asanginxst.epicfightx.client.renderer.RenderUchigatanaX;
import com.asanginxst.epicfightx.gameassets.EpicFightSkillsX;
import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import net.kenji.ef_uchigatana_adjustments.compat.EFXCompat;
import net.kenji.ef_uchigatana_adjustments.mixins.compat.AccessorRenderUchigatanaX;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.checkerframework.checker.units.qual.A;
import org.jline.utils.Log;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import static net.kenji.ef_uchigatana_adjustments.EpicFightUchigatanaAdjustments.EFX_LOADED;

public class UchigatanaSheathManager {




    public static void onPlayerTick(TickEvent.PlayerTickEvent event, Skill sheathSkill, RegistryObject<SkillDataKey<Boolean>> dataKey, String tagItemName, String tagSkillName, String optionalItemTag) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return; // server only

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        SkillContainer container = patch.getSkill(SkillSlots.WEAPON_PASSIVE);
        if (container == null) return; // note: no return on getSkill() == null
        final String TAG_SHEATHED = "ef_" + tagItemName +"_last_sheathed"; // last sheath state while holding battojutsu
        final String TAG_WAS_BATTO = "ef_" + tagItemName + "_was_" + tagSkillName;    // was the passive battojutsu last tick?

        CompoundTag data = player.getPersistentData();
        boolean isBatto = isBatto(container, sheathSkill);
        boolean wasBatto = data.getBoolean(TAG_WAS_BATTO);

        if (isBatto) {
            boolean justSwappedToSheathed = !wasBatto && data.getBoolean(TAG_SHEATHED);
            if (justSwappedToSheathed) {
                restoreSheath(container, dataKey);
            }
            boolean sheathed = isSheathed(container, dataKey);
            data.putBoolean(TAG_SHEATHED, sheathed);

            if (optionalItemTag != null && sheathed) {
                boolean idle = !patch.getEntityState().inaction();
                if (justSwappedToSheathed || idle) {
                    CompoundTag itemTag = player.getMainHandItem().getOrCreateTag();
                    if (itemTag.getInt(optionalItemTag) != 1) itemTag.putInt(optionalItemTag, 1);
                }
            }
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


    public static boolean isSheathed(SkillContainer container, RegistryObject<SkillDataKey<Boolean>> dataKey) {
        if (dataKey == SkillDataKeys.SHEATH && EFX_LOADED && EFXCompat.isEFXSkill(container)) {
            return EFXCompat.isSheathed(container);
        }
        return Boolean.TRUE.equals(container.getDataManager().getDataValue(dataKey.get()));
    }

    private static void restoreSheath(SkillContainer container, RegistryObject<SkillDataKey<Boolean>> dataKey) {
        if (dataKey == SkillDataKeys.SHEATH && EFX_LOADED && EFXCompat.isEFXSkill(container)) {
            EFXCompat.restoreSheath(container);
            return;
        }
        container.getDataManager().setDataSync(dataKey.get(), true);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
        container.getSkill().setConsumptionSynchronize(container, container.getMaxResource());
    }

    private static boolean isBatto(SkillContainer container, Skill sheathSkill){
        boolean isRegularBatto = container.getSkill() == sheathSkill;
        boolean epicFightXBatto = false;
        if(EFX_LOADED && sheathSkill == EpicFightSkills.BATTOJUTSU_PASSIVE){
            epicFightXBatto = container.getSkill() == EpicFightSkillsX.BATTOJUTSU_PASSIVE;
        }
        return isRegularBatto || epicFightXBatto;
    }
}