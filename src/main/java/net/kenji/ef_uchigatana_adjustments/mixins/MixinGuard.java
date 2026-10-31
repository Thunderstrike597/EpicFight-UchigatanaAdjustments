package net.kenji.ef_uchigatana_adjustments.mixins;

import net.kenji.ef_uchigatana_adjustments.ConfigCommon;
import net.kenji.ef_uchigatana_adjustments.UchigatanaSheathManager;
import net.minecraft.world.InteractionHand;
import org.jline.utils.Log;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = WeaponCapability.class, remap = false)
public abstract class MixinGuard {

    @Inject(method = "getLivingMotionModifier", at = @At("RETURN"), cancellable = true)
    private void efUchi$sheathedGuard(LivingEntityPatch<?> player, InteractionHand hand, CallbackInfoReturnable<Map<LivingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation>>> cir) {
        if (hand != InteractionHand.MAIN_HAND) return;
        if(!ConfigCommon.GUARD_KEEP_SHEATHED.get())
            return;
        WeaponCapability self = (WeaponCapability) (Object) this;
        if (self.getWeaponCategory() != CapabilityItem.WeaponCategories.UCHIGATANA) return;
        if (self.getStyle(player) != CapabilityItem.Styles.SHEATH) return;

        // copy first: the original returns the stored per-style map and mutates it
        Map<LivingMotion, AnimationManager.AnimationAccessor<? extends StaticAnimation>> map = new HashMap<>(cir.getReturnValue());
        map.put(LivingMotions.BLOCK, UchigatanaSheathManager.getUchiBlockAnim());

        cir.setReturnValue(map);
    }
}