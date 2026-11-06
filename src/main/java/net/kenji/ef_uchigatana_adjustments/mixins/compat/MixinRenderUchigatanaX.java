package net.kenji.ef_uchigatana_adjustments.mixins.compat;

import com.asanginxst.epicfightx.client.renderer.RenderUchigatanaX;
import com.mojang.blaze3d.vertex.PoseStack;
import net.kenji.ef_uchigatana_adjustments.compat.SwordSoaringCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = RenderUchigatanaX.class, remap = false)
public abstract class MixinRenderUchigatanaX {

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void efUchi$soaringSheath(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand,
                                      OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack,
                                      int packedLight, float partialTicks, CallbackInfo ci) {
        if (!(entitypatch instanceof PlayerPatch<?> playerpatch)) return;
        if (stack.getTag() == null || stack.getTag().getInt("sheath") != 1) return;
        if (!SwordSoaringCompat.isFlying(playerpatch)) return;

        RenderItemBase self = (RenderItemBase) (Object) this;
        ItemStack sheath = ((AccessorRenderUchigatanaX) this).getAlterSheathStack();

        OpenMatrix4f matrix = self.getCorrectionMatrix(entitypatch, InteractionHand.MAIN_HAND, poses);
        poseStack.pushPose();
        MathUtils.mulStack(poseStack, matrix);

        poseStack.translate(0.0F, 1F, 0); // shift along the item's own axis

        Minecraft.getInstance().getItemRenderer().renderStatic(sheath, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, null, 0);
        poseStack.popPose();

        ci.cancel(); // skips the off-hand draw, so nothing orbits the player
    }
}