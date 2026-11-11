package net.kenji.ef_uchigatana_adjustments.mixins.compat.efn;

import com.hm.efn.mobeffects.MurasamaSheathEffect;
import net.kenji.ef_uchigatana_adjustments.ConfigCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MurasamaSheathEffect.class)
public abstract class MixinMurasamaSheathEffect {

    @Redirect(method = "applyEffectTick",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void efUchi$muteOrbPickup(Level instance, Player pPlayer, BlockPos pPos, SoundEvent pSound, SoundSource pCategory, float pVolume, float pPitch) {
        boolean orb = pSound == SoundEvents.EXPERIENCE_ORB_PICKUP;
        if (orb && ConfigCommon.DISBALE_HF_BLADE_COUNTER_SOUND.get()) {
            return;
        }
        instance.playSound(pPlayer, pPos, pSound, pCategory, pVolume, pPitch);
    }
}