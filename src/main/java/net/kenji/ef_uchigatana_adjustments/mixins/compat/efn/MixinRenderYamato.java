package net.kenji.ef_uchigatana_adjustments.mixins.compat.efn;

import com.hm.efn.client.renderer.RenderMurasama;
import com.hm.efn.client.renderer.RenderYamato_DMC;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.mojang.blaze3d.vertex.PoseStack;
import net.kenji.ef_uchigatana_adjustments.compat.SwordSoaringCompat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.model.armature.types.ToolHolderArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.Objects;

@Mixin(value = RenderYamato_DMC.class, remap = false)
public abstract class MixinRenderYamato {


    @Shadow
    private static boolean isUseMesh2(DynamicAnimation animation) {
        return animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_NORMAL_AUTO3) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO3) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO4) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_EXTEND_AUTO5) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO1) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO2) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO3) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL_ALL) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_VOLCANOL) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_CHARGE) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST_MOB) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_REPAIDSLASH) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_REPAIDSLASH_MOB) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT_REPAID) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_FLARECUT_RISING) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_UPPERSLASH) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_UPPERSLASH_HOLD) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DRIVE) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_DRIVE_MOB) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_STOMP) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_AIRFLUSH) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_HELMBREAKER) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_ORBIT_1) || animation.getRealAnimation().equals(EFNYamatoAnimations.YAMATO_ORBIT_2) || animation.getRealAnimation().equals(EFNAnimations.DMC5_V_JC);
    }

    @Shadow
    @Final
    private ResourceLocation mesh2Texture;

    @Shadow
    @Final
    private ResourceLocation mesh2TextureL;

    @Shadow
    @Final
    private ResourceLocation mesh1Texture;

    @Shadow
    @Final
    private ResourceLocation mesh1TextureL;

    @Shadow
    @Final
    private AssetAccessor<? extends SkinnedMesh> mesh2_main;

    @Shadow
    @Final
    private AssetAccessor<? extends SkinnedMesh> mesh1_main;

    @Shadow
    @Final
    private AssetAccessor<? extends SkinnedMesh> mesh2_off;

    @Shadow
    @Final
    private AssetAccessor<? extends SkinnedMesh> mesh1_off;

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void efUchi$yamatoSoaring(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand,
                                      OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack,
                                      int packedLight, float partialTicks, CallbackInfo ci) {
        if (entitypatch == null || hand != InteractionHand.MAIN_HAND) return;
        if (!SwordSoaringCompat.isFlying(entitypatch)) return;
        SkinnedMesh mesh = this.mesh1_main == null ? null : this.mesh1_main.get();   // the in-sheath model
        if (mesh == null) return;

        Armature armature = entitypatch.getArmature();
        poseStack.pushPose();
        armature.setPose(entitypatch.getAnimator().getPose(partialTicks));
        OpenMatrix4f[] mats = armature.getPoseMatrices();

        int leftId = -1;
        OpenMatrix4f originalLeft = null;
        if (armature instanceof ToolHolderArmature tools) {
            leftId = tools.leftToolJoint().getId();
            originalLeft = mats[leftId];
            mats[leftId] = mats[tools.rightToolJoint().getId()];   // Tool_L-bound vertices now follow Tool_R
        }
        try {
            if (this.mesh1Texture != null)
                mesh.draw(poseStack, buffer, RenderType.entityTranslucent(this.mesh1Texture), packedLight,
                        1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, armature, mats);
            if (this.mesh1TextureL != null)
                mesh.draw(poseStack, buffer, RenderType.entityTranslucentEmissive(this.mesh1TextureL), packedLight,
                        1.0F, 1.0F, 1.0F, 0.9F, OverlayTexture.NO_OVERLAY, armature, mats);
        } finally {
            if (originalLeft != null) mats[leftId] = originalLeft;   // don't leak the swap to other renderers
            poseStack.popPose();
        }
        ci.cancel();
    }
   
}