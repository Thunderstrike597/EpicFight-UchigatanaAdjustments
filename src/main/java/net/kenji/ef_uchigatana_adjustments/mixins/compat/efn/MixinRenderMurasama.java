package net.kenji.ef_uchigatana_adjustments.mixins.compat.efn;

import com.hm.efn.client.renderer.RenderMurasama;
import com.mojang.blaze3d.vertex.PoseStack;
import net.kenji.ef_uchigatana_adjustments.compat.SwordSoaringCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = RenderMurasama.class, remap = false)
public abstract class MixinRenderMurasama {

    @Unique
    private boolean efUchi$soaring;

    @Inject(method = "renderItemInHand", at = @At("HEAD"))
    private void efUchi$captureSoaring(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand,
                                       OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack,
                                       int packedLight, float partialTicks, CallbackInfo ci) {
        this.efUchi$soaring = entitypatch instanceof PlayerPatch<?> p && SwordSoaringCompat.isFlying(p);
    }

    @Redirect(method = "renderItemInHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getInt(Ljava/lang/String;)I", remap = true))
    private int efUchi$overrideSheathTag(CompoundTag tag, String key) {
        return this.efUchi$soaring ? 0 : tag.getInt(key);
    }
}