package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.nbt.CompoundTag;

public interface DelegateForgingHandler extends IForging {
    IForging getForgingHandler();

    @Override
    default CompoundTag serializeNBT() {
        return getForgingHandler().serializeNBT();
    }

    @Override
    default void deserializeNBT(CompoundTag compoundTag) {
        getForgingHandler().deserializeNBT(compoundTag);
    }
}
