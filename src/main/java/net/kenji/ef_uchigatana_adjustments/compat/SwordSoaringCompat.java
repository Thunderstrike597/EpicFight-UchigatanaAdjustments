package net.kenji.ef_uchigatana_adjustments.compat;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.p1nero.ss.gameassets.SwordSoaringDatakeys;
import net.p1nero.ss.gameassets.SwordSoaringSkillSlots;
import net.p1nero.ss.skill.sword_soaring.SwordSoaringSkillElytra;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public final class SwordSoaringCompat {
    public static boolean isFlying(PlayerPatch<?> patch) {
        SkillContainer c = patch.getSkill(SwordSoaringSkillSlots.SWORD_SOARING);
        if (c == null || !(c.getSkill() instanceof SwordSoaringSkillElytra)) return false;
        SkillDataKey<Boolean> key = SwordSoaringDatakeys.FLYING.get();
        return c.getDataManager().hasData(key) && Boolean.TRUE.equals(c.getDataManager().getDataValue(key));
    }

}