package net.kenji.ef_uchigatana_adjustments.mixins.compat;


import com.asanginxst.epicfightx.skills.weapon_passive.BattojutsuPassiveX;
import net.kenji.ef_uchigatana_adjustments.ConfigCommon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;

@Mixin(value = BattojutsuPassiveX.class, remap = false)
public abstract class MixinBattojutsuPassiveX {


    @Shadow
    public abstract void onReset(SkillContainer container);

    @Inject(method = "isHoldingWeapon", at = @At("RETURN"), cancellable = true)
    private void fixIsHoldingWeapon(SkillContainer container, CallbackInfoReturnable<Boolean> cir){
        BattojutsuPassiveX self = (BattojutsuPassiveX)(Object)this;

        ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
        cir.setReturnValue(container.getExecutor().getSkill(SkillSlots.WEAPON_PASSIVE) != null && container.getExecutor().getSkill(SkillSlots.WEAPON_PASSIVE).getSkill() == self);
    }

    @Redirect(
            method = "lambda$onInitiate$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/asanginxst/epicfightx/skills/weapon_passive/BattojutsuPassiveX;onReset(Lyesman/epicfight/skill/SkillContainer;)V"
            )
    )
    private void skipReset$1(BattojutsuPassiveX instance, SkillContainer container){
        if(!ConfigCommon.GUARD_KEEP_SHEATHED.get())
            this.onReset(container);
    }
    @Redirect(
            method = "lambda$onInitiate$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/asanginxst/epicfightx/skills/weapon_passive/BattojutsuPassiveX;onReset(Lyesman/epicfight/skill/SkillContainer;)V"
            )
    )
    private void skipReset$2(BattojutsuPassiveX instance, SkillContainer container){
        if(!ConfigCommon.GUARD_KEEP_SHEATHED.get())
            this.onReset(container);
    }
    @Redirect(
            method = "lambda$onInitiate$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/asanginxst/epicfightx/skills/weapon_passive/BattojutsuPassiveX;setSheathRenderState(Lyesman/epicfight/skill/SkillContainer;Z)V"
            )
    )
    private void skipUnsheathRender$2(BattojutsuPassiveX instance, SkillContainer container, boolean isSheath){
        if(!ConfigCommon.GUARD_KEEP_SHEATHED.get())
            this.onReset(container);
    }
    @Redirect(
            method = "lambda$onInitiate$4",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/asanginxst/epicfightx/skills/weapon_passive/BattojutsuPassiveX;setSheathRenderState(Lyesman/epicfight/skill/SkillContainer;Z)V"
            )
    )
    private void skipUnsheathRender$5(BattojutsuPassiveX instance, SkillContainer container, boolean isSheath){
        if(!ConfigCommon.GUARD_KEEP_SHEATHED.get())
            this.onReset(container);
    }
}
