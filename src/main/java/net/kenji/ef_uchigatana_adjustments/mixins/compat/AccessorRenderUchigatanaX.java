package net.kenji.ef_uchigatana_adjustments.mixins.compat;

import com.asanginxst.epicfightx.client.renderer.RenderUchigatanaX;
import net.kenji.ef_uchigatana_adjustments.UchigatanaSheathManager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderUchigatanaX.class)
public interface AccessorRenderUchigatanaX {

    @Accessor("alterSheathStack")
    ItemStack getAlterSheathStack();
}
