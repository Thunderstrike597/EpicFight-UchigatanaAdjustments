package net.kenji.ef_uchigatana_adjustments;


import net.kenji.ef_uchigatana_adjustments.compat.EFXCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mod.EventBusSubscriber(modid = EpicFightUchigatanaAdjustments.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientSheathSync {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!ModList.get().isLoaded("epicfightx")) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        SkillContainer container = patch.getSkill(SkillSlots.WEAPON_PASSIVE);
        if (container == null) return;

        EFXCompat.syncClientTag(patch, container);
    }
}