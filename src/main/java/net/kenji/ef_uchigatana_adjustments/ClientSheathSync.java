package net.kenji.ef_uchigatana_adjustments;

import net.kenji.ef_uchigatana_adjustments.EpicFightUchigatanaAdjustments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EpicFightUchigatanaAdjustments.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientSheathSync {

    /** skill and dataKey are suppliers so they are only resolved after registries exist */
    public record Entry(Supplier<? extends Skill> skill,
                        Supplier<SkillDataKey<Boolean>> dataKey,
                        String itemTag) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();

    public static void register(Supplier<? extends Skill> skill,
                                Supplier<SkillDataKey<Boolean>> dataKey,
                                String itemTag) {
        ENTRIES.add(new Entry(skill, dataKey, itemTag));
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ENTRIES.isEmpty()) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;

        SkillContainer container = patch.getSkill(SkillSlots.WEAPON_PASSIVE);
        if (container == null || container.getSkill() == null) return;
        if (patch.getEntityState().inaction()) return; // don't hide the blade mid-attack

        for (Entry entry : ENTRIES) {
            // same "holding it" test the server manager uses: the passive slot has this weapon's skill
            if (container.getSkill() != entry.skill().get()) continue;

            if (!Boolean.TRUE.equals(container.getDataManager().getDataValue(entry.dataKey().get()))) return;

            ItemStack stack = player.getMainHandItem();
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.getInt(entry.itemTag()) != 1) tag.putInt(entry.itemTag(), 1);
            return;
        }
    }

    private ClientSheathSync() {}
}