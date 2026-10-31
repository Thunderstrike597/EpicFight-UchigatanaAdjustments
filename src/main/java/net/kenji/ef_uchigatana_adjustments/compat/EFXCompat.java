package net.kenji.ef_uchigatana_adjustments.compat;

import com.asanginxst.epicfightx.gameassets.EFXSkillDataKeys;
import com.asanginxst.epicfightx.skills.weapon_passive.BattojutsuPassiveX;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public final class EFXCompat {

    public static boolean isEFXSkill(SkillContainer container) {
        return container.getSkill() instanceof BattojutsuPassiveX;
    }

    public static boolean isSheathed(SkillContainer container) {
        return Boolean.TRUE.equals(container.getDataManager().getDataValue(EFXSkillDataKeys.SHEATH.get()));
    }

    public static void restoreSheath(SkillContainer container) {
        BattojutsuPassiveX skill = (BattojutsuPassiveX) container.getSkill();

        container.getDataManager().setDataSync(EFXSkillDataKeys.SHEATH.get(), true);
        container.getDataManager().setDataSync(EFXSkillDataKeys.SHEATH_KILLED.get(), false);
        container.getServerExecutor().modifyLivingMotionByCurrentItem(false);

        // what the scrap animation's END event would normally do
        skill.setSheathRenderState(container, true);

        container.getSkill().setConsumptionSynchronize(container, container.getMaxResource());
    }
    public static void syncClientTag(PlayerPatch<?> patch, SkillContainer container) {
        if (!isEFXSkill(container)) return;
        BattojutsuPassiveX skill = (BattojutsuPassiveX) container.getSkill();
        if (!skill.isHoldingWeapon(container)) return;
        if (!isSheathed(container)) return;
        if (patch.getEntityState().inaction()) return; // don't hide the blade mid-attack

        ItemStack stack = patch.getOriginal().getMainHandItem();
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt("sheath") != 1) tag.putInt("sheath", 1);
    }
}