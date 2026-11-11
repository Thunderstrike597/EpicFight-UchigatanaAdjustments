package net.kenji.ef_uchigatana_adjustments.mixins.compat.efn;

import com.hm.efn.capability.AdvanceWeaponCapability;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNSkills;
import com.hm.efn.gameasset.EFNWeaponCapabilityPresets;
import com.hm.efn.gameasset.animations.EFNHfBladeAnimations;
import com.hm.efn.gameasset.combos.HfBlade;
import com.hm.efn.particle.EFNParticles;
import net.kenji.ef_uchigatana_adjustments.compat.EFNCompat;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.function.Function;

@Mixin(EFNWeaponCapabilityPresets.class)
public class MixinEFNWeaponCapabilityPresets {
   @Shadow
   public static final Function<Item, CapabilityItem.Builder> HF_BLADE = (item) -> AdvanceWeaponCapability.builder().category(CapabilityItem.WeaponCategories.TACHI).styleProvider((entityPatch) -> {
        if (entityPatch instanceof PlayerPatch<?> playerPatch) {
            SkillContainer container = playerPatch.getSkill(HfBlade.HfBlade_Passive);
            if (container != null && !container.isEmpty() && container.getDataManager().hasData((SkillDataKey) EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT.get()) && (Boolean)container.getDataManager().getDataValue((SkillDataKey)EFNSKillDataKeys.IS_DOUBLE_TAP_SPRINT.get())) {
                return CapabilityItem.Styles.COMMON;
            }

            if (container != null && !container.isEmpty() && container.getDataManager().hasData((SkillDataKey)EFNSKillDataKeys.MURASAMA_SHEATH.get()) && (Boolean)container.getDataManager().getDataValue((SkillDataKey)EFNSKillDataKeys.MURASAMA_SHEATH.get())) {
                return CapabilityItem.Styles.SHEATH;
            }
        }
        return CapabilityItem.Styles.TWO_HAND;
    }).collider(new MultiOBBCollider(3, 0.45, 0.45, (double)1.25F, (double)0.0F, (double)0.0F, -0.95))
           .swingSound((SoundEvent) EpicFightSounds.WHOOSH.get())
           .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
           .hitParticle((HitParticleType) EFNParticles.HF_BLADE_HIT.get())
           .canBePlacedOffhand(false)
           .newAdvanceStyleCombo(CapabilityItem.Styles.TWO_HAND, new AnimationManager.AnimationAccessor[]{
                           Animations.SWORD_AUTO1,
                   Animations.SWORD_AUTO2,
                   Animations.SWORD_AUTO3,
                   Animations.SWORD_DASH,
                   Animations.SWORD_AIR_SLASH})
           .newAdvanceStyleCombo(CapabilityItem.Styles.COMMON, new AnimationManager.AnimationAccessor[]{
                   Animations.SWORD_AUTO1,
                   Animations.SWORD_AUTO2,
                   Animations.SWORD_AUTO3,
                   Animations.SWORD_DASH,
                   Animations.SWORD_AIR_SLASH})
           .newAdvanceStyleCombo(CapabilityItem.Styles.SHEATH, new AnimationManager.AnimationAccessor[]{
                   Animations.SWORD_AUTO1,
                   Animations.SWORD_AUTO2,
                   Animations.SWORD_AUTO3,
                   Animations.SWORD_DASH,
                   Animations.SWORD_AIR_SLASH})
           .innateSkill(CapabilityItem.Styles.TWO_HAND, (itemstack) -> HfBlade.HfBlade)
           .innateSkill(CapabilityItem.Styles.COMMON, (itemstack) -> HfBlade.HfBlade)
           .innateSkill(CapabilityItem.Styles.SHEATH, (itemstack) -> HfBlade.HfBlade)
           .passiveSkill(HfBlade.HfBlade_Passive)
           .exclusiveDodge(EFNSkills.MURASAMA_DODGE)
           .exclusiveGuard(EFNSkills.MURASAMA_PARRY)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.BLOCK, EFNHfBladeAnimations.HF_BLADE_GUARD)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.CREATIVE_IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_AIR)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.JUMP, EFNCompat.getHFBladeSwapAnimation(LivingMotions.JUMP, EFNHfBladeAnimations.HF_BLADE_JUMP_FIRST))
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.FALL, EFNHfBladeAnimations.HF_BLADE_FALL_FIRST)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.LANDING_RECOVERY, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.WALK, EFNHfBladeAnimations.HF_BLADE_WALK_COMBAT)
           .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.RUN, EFNHfBladeAnimations.HF_BLADE_RUN_COMBAT_1)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.BLOCK, EFNHfBladeAnimations.HF_BLADE_GUARD)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.CREATIVE_IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_AIR)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.JUMP, EFNCompat.getHFBladeSwapAnimation(LivingMotions.JUMP, EFNHfBladeAnimations.HF_BLADE_JUMP_FIRST))
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.FALL, EFNHfBladeAnimations.HF_BLADE_FALL_FIRST)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.LANDING_RECOVERY, EFNHfBladeAnimations.HF_BLADE_IDLE_COMBAT)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.WALK, EFNHfBladeAnimations.HF_BLADE_WALK_COMBAT)
           .livingMotionModifier(CapabilityItem.Styles.COMMON, LivingMotions.RUN, EFNHfBladeAnimations.HF_BLADE_RUN_COMBAT_2)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.BLOCK, EFNHfBladeAnimations.HF_BLADE_GUARD)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.CREATIVE_IDLE, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.KNEEL, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.SNEAK, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.LANDING_RECOVERY, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.SWIM, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.FLOAT, EFNHfBladeAnimations.HF_BLADE_IDLE_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.JUMP, EFNCompat.getHFBladeSwapAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP))
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.FALL, EFNHfBladeAnimations.HF_BLADE_FALL_FIRST)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.WALK, EFNHfBladeAnimations.HF_BLADE_WALK_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.CHASE, EFNHfBladeAnimations.HF_BLADE_RUN_SHEATH)
           .livingMotionModifier(CapabilityItem.Styles.SHEATH, LivingMotions.RUN, EFNCompat.getHFBladeSwapAnimation(LivingMotions.RUN, EFNHfBladeAnimations.HF_BLADE_RUN_SHEATH))
           .reach(0.35F).comboCancel((style) -> false);


}
