/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.model;

import java.util.Map;
import java.util.function.Function;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.util.Helpers;


public record TrimmedItemModel(@Nullable ArmorTrim trim) implements IUnbakedGeometry<TrimmedItemModel>
{

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        final TextureAtlasSprite baseSprite = spriteGetter.apply(context.getMaterial("armor"));
        final TextureAtlasSprite overlaySprite = context.hasMaterial("overlay") ? spriteGetter.apply(context.getMaterial("overlay")) : null;
        final ResourceLocation trimLocation = context.getMaterial("trim").texture();
        final String color = trim != null ? trim.material().value().assetName() : null;
        final TextureAtlasSprite trimSprite = trim != null ? spriteGetter.apply(new Material(RenderHelpers.BLOCKS_ATLAS, trimLocation.withSuffix("_" + color))) : null;

        final var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(Helpers.resourceLocation("trim_override"));
        final var builder = CompositeModel.Baked.builder(itemContext, baseSprite, new TrimOverrideHandler(overrides, baker, itemContext), context.getTransforms());
        final var normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());

        addQuads(modelState, baseSprite, builder, normalRenderTypes, ContainedFluidModel.FLUID_TRANSFORM);

        if (overlaySprite != null)
        {
            addQuads(modelState, overlaySprite, builder, normalRenderTypes, ContainedFluidModel.FLUID_TRANSFORM);
        }

        if (trimSprite != null)
        {
            addQuads(modelState, trimSprite, builder, normalRenderTypes, ContainedFluidModel.COVER_TRANSFORM);
        }

        builder.setParticle(baseSprite);
        return builder.build();
    }

    private static void addQuads(ModelState modelState, TextureAtlasSprite trimSprite, CompositeModel.Baked.Builder builder, RenderTypeGroup normalRenderTypes, @Nullable Transformation transformation)
    {
        var transformedState = transformation == null ? modelState : new SimpleModelState(modelState.getRotation().compose(transformation), modelState.isUvLocked());
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, trimSprite);
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, material -> trimSprite, transformedState);
        builder.addQuads(normalRenderTypes, quads);
    }

    public static class Loader implements IGeometryLoader<TrimmedItemModel>
    {
        @Override
        public TrimmedItemModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            return new TrimmedItemModel(null);
        }
    }

    private static final class TrimOverrideHandler extends ItemOverrides
    {
        private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
        private final ItemOverrides nested;
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;

        private TrimOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner)
        {
            this.nested = nested;
            this.baker = baker;
            this.owner = owner;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed)
        {
            BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
            if (overridden != originalModel || level == null) return overridden;
            final ArmorTrim trim = stack.get(DataComponents.TRIM);
            if (trim == null)
                return originalModel;
            final String name = trim.material().value().assetName();
            if (!cache.containsKey(name))
            {
                TrimmedItemModel unbaked = new TrimmedItemModel(trim);
                BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this);
                cache.put(name, bakedModel);
                return bakedModel;
            }
            return cache.get(name);
        }
    }

}
