package net.kenji.ef_uchigatana_adjustments.compat;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.hm.efn.skill.weapon_passive.HfBladePassive;
import net.kenji.ef_uchigatana_adjustments.ConfigCommon;
import net.kenji.ef_uchigatana_adjustments.EpicFightUchigatanaAdjustments;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;

public class EFNCompat {

    public static AnimationManager.AnimationAccessor<? extends StaticAnimation> getHFBladeSwapAnimation(LivingMotion livingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation> original){
        if(EpicFightUchigatanaAdjustments.EFX_LOADED){
            if(ConfigCommon.SWAP_HF_BLADE_LIVING_ANIMATIONS.get()){
                if (livingMotion.equals(LivingMotions.RUN)) {
                    return AnimationsX.BIPED_RUN_UCHIGATANA_SHEATHING;
                } else if (livingMotion.equals(LivingMotions.JUMP)) {
                    return AnimationsX.BIPED_JUMP;
                }
            }
        }
        return original;
    }
    public static boolean tryUnsheath(SkillContainer container) {
        if (container.getSkill() instanceof HfBladePassive passive) {
            passive.murasamaSwordOut(container);
            return true;
        }
        return false;
    }
}
