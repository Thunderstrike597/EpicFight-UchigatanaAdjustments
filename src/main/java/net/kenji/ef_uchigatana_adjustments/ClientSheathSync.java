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
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.*;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EpicFightUchigatanaAdjustments.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientSheathSync {
    private static final Set<Entry> ARMED = new HashSet<>();
    private static final class State { boolean wasOn; Object sheathAnim; boolean armed; }
    private static final Map<Entry, State> STATE = new HashMap<>();

    private static Object currentAnim(PlayerPatch<?> patch) {
        AnimationPlayer p = patch.getAnimator().getPlayerFor(null);
        return p == null ? null : p.getAnimation().get().getRealAnimation();
    }

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
            if (container.getSkill() != entry.skill().get()) continue;
            State s = STATE.computeIfAbsent(entry, e -> new State());

            boolean on = Boolean.TRUE.equals(container.getDataManager().getDataValue(entry.dataKey().get()));
            if (!on) { s.wasOn = false; s.armed = false; s.sheathAnim = null; return; }

            Object cur = currentAnim(patch);
            if (!s.wasOn) {                  // just became sheathed: remember the animation that did it
                s.wasOn = true;
                s.sheathAnim = cur;
            }

            CompoundTag tag = player.getMainHandItem().getOrCreateTag();
            boolean tagOn = tag.getInt(entry.itemTag()) == 1;

            if (tagOn) {
                s.armed = true;                                   // timed event fired (or restored)
            } else if (!s.armed && !Objects.equals(cur, s.sheathAnim)
                    && !ConfigCommon.UNSHEATH_ON_INTERRUPTED_SHEATH.get()) {
                s.armed = true;                                   // sheath animation ended or was interrupted before the event
                tag.putInt(entry.itemTag(), 1);
            } else if (s.armed && !patch.getEntityState().inaction()) {
                tag.putInt(entry.itemTag(), 1);                   // cleared locally while sheathed
            }
            return;
        }
    }

    private ClientSheathSync() {}
}