package net.kenji.ef_uchigatana_adjustments;

import com.asanginxst.epicfightx.client.renderer.RenderUchigatanaX;
import com.asanginxst.epicfightx.gameassets.EpicFightSkillsX;
import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import net.kenji.ef_uchigatana_adjustments.compat.EFNCompat;
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
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.kenji.ef_uchigatana_adjustments.EpicFightUchigatanaAdjustments.EFX_LOADED;

public class UchigatanaSheathManager {

    private static final class Track { boolean wasOn; Object sheathAnim; }
    private static final Map<String, Track> TRACK = new HashMap<>();

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

        if (!isBatto && wasBatto && data.getBoolean(TAG_SHEATHED)) {
            data.putBoolean("ef_uchi_restore_pending", true);   // swapped away while sheathed
        }

        if (isBatto) {
            boolean justSwappedToSheathed = !wasBatto && data.getBoolean(TAG_SHEATHED);
            if (justSwappedToSheathed) {
                restoreSheath(container, dataKey);
                data.putLong("ef_uchi_silent_sheath", player.level().getGameTime()); // stamp here only
            }
            data.remove("ef_uchi_restore_pending");

            boolean sheathed = isSheathed(container, dataKey);
            data.putBoolean(TAG_SHEATHED, sheathed);
            final String TAG_RENDERED = "ef_" + tagItemName + "_tag_rendered";

            if (optionalItemTag != null) {
                final String trackKey = player.getUUID() + tagItemName;
                if (sheathed) {
                    Track t = TRACK.computeIfAbsent(trackKey, k -> new Track());
                    Object cur = currentAnim(patch);
                    if (!t.wasOn) { t.wasOn = true; t.sheathAnim = cur; }   // the animation that started the sheathing

                    CompoundTag itemTag = player.getMainHandItem().getOrCreateTag();
                    boolean tagOn = itemTag.getInt(optionalItemTag) == 1;

                    if (justSwappedToSheathed) {
                        if (!tagOn) itemTag.putInt(optionalItemTag, 1);
                        data.putBoolean(TAG_RENDERED, true);
                    } else if (tagOn) {
                        data.putBoolean(TAG_RENDERED, true);                 // EFN's timed event fired
                    } else if (data.getBoolean(TAG_RENDERED)) {
                        if (!patch.getEntityState().inaction()) itemTag.putInt(optionalItemTag, 1);   // cleared while sheathed: restore
                    } else if (ConfigCommon.UNSHEATH_ON_INTERRUPTED_SHEATH.get() && !Objects.equals(cur, t.sheathAnim)) {
                        unsheath(container, dataKey);                        // the sheathing never reached the timed event
                        TRACK.remove(trackKey);
                    }
                } else {
                    data.putBoolean(TAG_RENDERED, false);
                    TRACK.remove(trackKey);
                }
            }
        }

        if (isBatto != wasBatto) {
            data.putBoolean(TAG_WAS_BATTO, isBatto);
        }
    }

    public static void clearTracking(String id){
        TRACK.keySet().removeIf(k -> k.startsWith(id));
    }
    private static Object currentAnim(PlayerPatch<?> patch) {
        AnimationPlayer p = patch.getAnimator().getPlayerFor(null);
        return p == null ? null : p.getAnimation().get().getRealAnimation();
    }

    private static void unsheath(SkillContainer container, RegistryObject<SkillDataKey<Boolean>> dataKey) {
        if (EpicFightUchigatanaAdjustments.EFN_LOADED && EFNCompat.tryUnsheath(container)) return;
        container.getDataManager().setDataSync(dataKey.get(), false);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);
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