/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class ToolRackBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public ToolRackBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.TOOL_RACK.get(), pos, state, defaultInventory(4));
    }

    public ItemInteractionResult onRightClick(Player player, int slot)
    {
        assert level != null;
        final ItemStack heldItem = player.getMainHandItem();
        final boolean shouldExtract = !inventory.getStackInSlot(slot).isEmpty();
        final boolean shouldInsert = !heldItem.isEmpty() && isItemValid(slot, heldItem);
        if (shouldExtract)
        {
            // Swap items
            if (shouldInsert)
            {
                final ItemStack extracted = inventory.extractItem(slot, 1, false);
                if (!level.isClientSide)
                {
                    insertItem(slot, heldItem.split(1));
                    ItemHandlerHelper.giveItemToPlayer(player, extracted, player.getInventory().selected);
                }
                markForSync();
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            // Just extract
            if (!level.isClientSide)
            {
                ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(slot, 1, false), player.getInventory().selected);
            }
            markForSync();
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (shouldInsert)
        {
            if (!level.isClientSide)
            {
                insertItem(slot, heldItem.split(1));
            }
            markForSync();
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.TOOL_RACK_TOOLS);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    private void insertItem(int slot, ItemStack stack)
    {
        assert level != null;
        inventory.insertItem(slot, stack, false);
        level.playSound(null, worldPosition, TFCSounds.TOOL_RACK_PLACE.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
    }

}
