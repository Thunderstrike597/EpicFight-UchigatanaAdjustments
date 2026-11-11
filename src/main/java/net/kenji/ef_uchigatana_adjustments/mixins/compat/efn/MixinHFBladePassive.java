package net.kenji.ef_uchigatana_adjustments.mixins.compat.efn;


import com.hm.efn.animations.types.murasama.MurasamaLivingAnimation;
import com.hm.efn.animations.types.murasama.MurasamaMovementAnimation;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.skill.dodge.MurasamaDodge;
import com.hm.efn.skill.weapon_passive.HfBladePassive;
import net.kenji.ef_uchigatana_adjustments.ConfigCommon;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.input.MovementDirection;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.eventlistener.MovementInputEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(value = HfBladePassive.class, remap = false)
public abstract class MixinHFBladePassive {
    @Unique
    private static final UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac120002");

    @Unique
    private static List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> epicfight_uchigatana_adjustments$valiSheathAnimations = new ArrayList<>();


    static {
        epicfight_uchigatana_adjustments$valiSheathAnimations.add(EFNHfBladeAnimations.HF_BLADE_SHEATH_IN);
        epicfight_uchigatana_adjustments$valiSheathAnimations.add(EFNHfBladeAnimations.HF_BLADE_SHEATH_IN_RUN);
        epicfight_uchigatana_adjustments$valiSheathAnimations.add(EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH);
        epicfight_uchigatana_adjustments$valiSheathAnimations.add(EFNHfBladeAnimations.HF_BLADE_WALK_SHEATH);
        epicfight_uchigatana_adjustments$valiSheathAnimations.add(EFNHfBladeAnimations.HF_BLADE_RUN_SHEATH);
    }

    @Shadow
    public abstract void murasamaSwordOut(SkillContainer container);

    @Shadow
    @Final
    private static UUID SWORDOUT_EVENT_DODGE_UUID;


    @Shadow
    @Final
    private static UUID SHEATH_RESETEVENT_UUID;

    @Shadow
    public abstract void setMurasamaSheathMesh(SkillContainer container, boolean isSheath);

    @Unique
    private MovementInputEvent efUchi$currentInput;

    @Inject(method = "executeDoubleJump", at = @At("HEAD"))
    private void efUchi$captureInput(SkillContainer container, MovementInputEvent event, CallbackInfo ci) {
        this.efUchi$currentInput = event;
    }
    @Redirect(method = "executeDoubleJump",
            at = @At(value = "INVOKE",
                    target = "Lyesman/epicfight/client/world/capabilites/entitypatch/player/LocalPlayerPatch;playAnimationSynchronized(Lyesman/epicfight/api/asset/AssetAccessor;F)V"))
    private void maybeChangeDoubleJumpAnim(LocalPlayerPatch instance, AssetAccessor<? extends StaticAnimation> animation, float transition) {
        if (ConfigCommon.CHANGE_HF_BLADE_DOUBLE_JUMP_ANIM.get()) {
            boolean backward = this.efUchi$currentInput != null
                    && MovementDirection.fromInputState(this.efUchi$currentInput.getInputState()).vertical() < 0;

            instance.playAnimationSynchronized(
                    backward ? Animations.BIPED_PHANTOM_ASCENT_BACKWARD : Animations.BIPED_PHANTOM_ASCENT_FORWARD, 0.0F);
        } else {
            instance.playAnimationSynchronized(animation, transition);
        }
        this.efUchi$currentInput = null;
    }

    @Redirect(
            method = "lambda$setupEventListeners$8",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hm/efn/skill/weapon_passive/HfBladePassive;murasamaSwordOut(Lyesman/epicfight/skill/SkillContainer;)V"
            )
    )
    public void maybeStopSwordOut(HfBladePassive instance, SkillContainer container) {
        PlayerEventListener listener = container.getExecutor().getEventListener();

        listener.addEventListener(PlayerEventListener.EventType.ANIMATION_BEGIN_EVENT, SHEATH_RESETEVENT_UUID, (event) -> {

            if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_KICK_Y)) {
                container.getDataManager().setDataSync((SkillDataKey) EFNSKillDataKeys.BACKWARD_FORWARD_WINDOW_ACTIVE.get(), false);
                container.getDataManager().setData((SkillDataKey) EFNSKillDataKeys.BACKWARD_FORWARD_REMAINING_TICKS.get(), 0);
            }

            if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_SWORD_OUT)) {
                container.getDataManager().setDataSync((SkillDataKey) EFNSKillDataKeys.MANUAL_UNSHEATH.get(), true);
            }

            if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_GUARD_DASH)) {
                container.getDataManager().setDataSync((SkillDataKey) EFNSKillDataKeys.MANUAL_UNSHEATH.get(), true);
                this.murasamaSwordOut(container);
            }

            if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_TAUNT)) {
                container.getDataManager().setDataSync((SkillDataKey) EFNSKillDataKeys.MANUAL_UNSHEATH.get(), true);
                this.murasamaSwordOut(container);
            }

            if (!ConfigCommon.EFN_SHEATHED_HF_BLADE_COMPAT.get()) {
                if (event.getAnimation().getRealAnimation().equals(EFNHfBladeAnimations.HF_BLADE_JUMP_SECOND)) {
                    container.getDataManager().setDataSync((SkillDataKey) EFNSKillDataKeys.MANUAL_UNSHEATH.get(), true);
                    this.murasamaSwordOut(container);
                }


                if (event.getAnimation().getRealAnimation().equals(EFNDodgeAnimations.MURASAMA_ROLL_F_AIR)) {
                    container.getDataManager().setDataSync((SkillDataKey) EFNSKillDataKeys.MANUAL_UNSHEATH.get(), true);
                    this.murasamaSwordOut(container);
                }


            }
            if (event.getAnimation() instanceof MurasamaLivingAnimation) {
                this.setMurasamaSheathMesh(container, false);
            }

            if (event.getAnimation() instanceof MurasamaMovementAnimation) {
                this.setMurasamaSheathMesh(container, false);
            }
        });
    }
    @Redirect(method = "updateSheathBuff",
            at = @At(value = "INVOKE",
                    target = "Lyesman/epicfight/world/capabilities/entitypatch/player/PlayerPatch;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void efUchi$maybePlayDeplete(PlayerPatch<?> instance, SoundEvent sound, float vol, float pitch) {
        if (ConfigCommon.EFN_SHEATHED_HF_BLADE_COMPAT.get()
                && efUchi$wasRestoredRecently((Player) instance.getOriginal())) {
            return;
        }
        instance.playSound(sound, vol, pitch);
    }

    @Unique
    private static boolean efUchi$wasRestoredRecently(Player player) {
        long stamp = player.getPersistentData().getLong("ef_uchi_silent_sheath");
        return stamp != 0L && player.level().getGameTime() - stamp <= 3;
    }

    @Redirect(
            method = "lambda$setupEventListeners$4",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hm/efn/skill/weapon_passive/HfBladePassive;murasamaSwordOut(Lyesman/epicfight/skill/SkillContainer;)V"
            )
    )
    private void skipDodgeSwordOut$1(HfBladePassive instance, SkillContainer container){
        if(!ConfigCommon.EFN_SHEATHED_HF_BLADE_COMPAT.get()){
            this.murasamaSwordOut(container);
        }
    }
    @Redirect(
            method = "lambda$setupEventListeners$6",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hm/efn/skill/weapon_passive/HfBladePassive;murasamaSwordOut(Lyesman/epicfight/skill/SkillContainer;)V"
            )
    )
    private void skipDodgeSwordOut$2(HfBladePassive instance, SkillContainer container) {
        if (!ConfigCommon.EFN_SHEATHED_HF_BLADE_COMPAT.get()) {
            PlayerEventListener listener = container.getExecutor().getEventListener();
            listener.addEventListener(PlayerEventListener.EventType.SKILL_CAST_EVENT, SWORDOUT_EVENT_DODGE_UUID, (event) -> {
                if (event.getSkillContainer().getSkill() instanceof MurasamaDodge) {
                    this.murasamaSwordOut(container);
                }
            });
        }
    }
}
