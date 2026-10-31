package net.kenji.ef_uchigatana_adjustments;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompatMixinPlugin implements IMixinConfigPlugin {



    @Override
    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();

        return mixins;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(!EpicFightUchigatanaAdjustments.EFX_LOADED){
            if(mixinClassName.endsWith("MixinBattojutsuPassiveX"))
                return false;
        }
        return true;
    }

    // unused methods:
    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> a, Set<String> b) {}
    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}