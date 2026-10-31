package net.kenji.ef_uchigatana_adjustments.mixins;


import com.asanginxst.epicfightx.skills.weapon_passive.BattojutsuPassiveX;
import net.kenji.ef_uchigatana_adjustments.ConfigCommon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.BattojutsuPassive;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;

@Mixin(value = BattojutsuPassive.class, remap = false)
public abstract class MixinBattojutsuPassive {


    @Shadow
    public abstract void onReset(SkillContainer container);

    @Redirect(
            method = "lambda$onInitiate$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/skill/BattojutsuPassive;onReset(Lyesman/epicfight/skill/SkillContainer;)V"
            )
    )
    private void skipReset$1(BattojutsuPassive instance, SkillContainer container){
        if(!ConfigCommon.GUARD_KEEP_SHEATHED.get())
            this.onReset(container);
    }
}
