/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.dries007.tfc.common.capabilities.food.IFood;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * This is a thin supertype of {@link IFood} with a single purpose: provide access to the underlying {@link IFood} from a network thread, without triggering any state change or initialization.
 * This is a necessary distinction for capabilities that have initialization which must be performed on-thread, as we read this data from network off-thread, which may lead to odd behavior.
 */
public interface INetworkForging extends INBTSerializable<CompoundTag> {}
