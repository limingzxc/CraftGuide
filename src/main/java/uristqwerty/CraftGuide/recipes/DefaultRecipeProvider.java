package uristqwerty.CraftGuide.recipes;

import net.minecraft.*;
import uristqwerty.CraftGuide.*;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.*;
import uristqwerty.gui_craftguide.texture.DynamicTexture;
import uristqwerty.gui_craftguide.texture.TextureClip;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRecipeProvider extends CraftGuideAPIObject implements RecipeProvider, StackInfoSource
{
	public DefaultRecipeProvider()
	{
		StackInfo.addSource(this);
	}

	private final Slot[] shapelessCraftingSlots = new ItemSlot[]{
		new ItemSlot( 3,  3, 16, 16),
		new ItemSlot(21,  3, 16, 16),
		new ItemSlot(39,  3, 16, 16),
		new ItemSlot( 3, 21, 16, 16),
		new ItemSlot(21, 21, 16, 16),
		new ItemSlot(39, 21, 16, 16),
		new ItemSlot( 3, 39, 16, 16),
		new ItemSlot(21, 39, 16, 16),
		new ItemSlot(39, 39, 16, 16),
		new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT),
	};

	private final Slot[] craftingSlotsOwnBackground = new ItemSlot[]{
		new ItemSlot( 3,  3, 16, 16).drawOwnBackground(),
		new ItemSlot(21,  3, 16, 16).drawOwnBackground(),
		new ItemSlot(39,  3, 16, 16).drawOwnBackground(),
		new ItemSlot( 3, 21, 16, 16).drawOwnBackground(),
		new ItemSlot(21, 21, 16, 16).drawOwnBackground(),
		new ItemSlot(39, 21, 16, 16).drawOwnBackground(),
		new ItemSlot( 3, 39, 16, 16).drawOwnBackground(),
		new ItemSlot(21, 39, 16, 16).drawOwnBackground(),
		new ItemSlot(39, 39, 16, 16).drawOwnBackground(),
		new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground(),
	};

	private final Slot[] smallCraftingSlotsOwnBackground = new ItemSlot[]{
		new ItemSlot(12, 12, 16, 16).drawOwnBackground(),
		new ItemSlot(30, 12, 16, 16).drawOwnBackground(),
		new ItemSlot(12, 30, 16, 16).drawOwnBackground(),
		new ItemSlot(30, 30, 16, 16).drawOwnBackground(),
		new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT).drawOwnBackground(),
	};

	private final Slot[] craftingSlots = new ItemSlot[]{
		new ItemSlot( 3,  3, 16, 16),
		new ItemSlot(21,  3, 16, 16),
		new ItemSlot(39,  3, 16, 16),
		new ItemSlot( 3, 21, 16, 16),
		new ItemSlot(21, 21, 16, 16),
		new ItemSlot(39, 21, 16, 16),
		new ItemSlot( 3, 39, 16, 16),
		new ItemSlot(21, 39, 16, 16),
		new ItemSlot(39, 39, 16, 16),
		new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT),
	};

	private final Slot[] smallCraftingSlots = new ItemSlot[]{
		new ItemSlot(12, 12, 16, 16),
		new ItemSlot(30, 12, 16, 16),
		new ItemSlot(12, 30, 16, 16),
		new ItemSlot(30, 30, 16, 16),
		new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT),
	};

	private final Slot[] furnaceSlots = new ItemSlot[]{
		new ItemSlot(13, 21, 16, 16),
		new ItemSlot(50, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT),
	};

	@Override
	public void generateRecipes(RecipeGenerator generator)
	{
		RecipeTemplate craftingTemplate;
		RecipeTemplate smallCraftingTemplate;

		if(CraftGuide.newerBackgroundStyle)
		{
			craftingTemplate = generator.createRecipeTemplate(craftingSlotsOwnBackground, null);
			smallCraftingTemplate = generator.createRecipeTemplate(smallCraftingSlotsOwnBackground, null);
		}
		else
		{
			craftingTemplate = new DefaultRecipeTemplate(
					craftingSlots, RecipeGeneratorImplementation.workbench,
					new TextureClip(
							DynamicTexture.instance("recipe_backgrounds"),
							1, 1, 79, 58),
					new TextureClip(
							DynamicTexture.instance("recipe_backgrounds"),
							82, 1, 79, 58));

			smallCraftingTemplate = new DefaultRecipeTemplate(
					smallCraftingSlots, RecipeGeneratorImplementation.workbench,
					new TextureClip(
							DynamicTexture.instance("recipe_backgrounds"),
							1, 61, 79, 58),
					new TextureClip(
							DynamicTexture.instance("recipe_backgrounds"),
							82, 61, 79, 58));
		}

		RecipeTemplate shapelessTemplate = new DefaultRecipeTemplate(
					shapelessCraftingSlots,
					RecipeGeneratorImplementation.workbench,
					new TextureClip(
							DynamicTexture.instance("recipe_backgrounds"),
							1, 121, 79, 58),
					new TextureClip(
							DynamicTexture.instance("recipe_backgrounds"),
							82, 121, 79, 58));

		RecipeTemplate furnaceTemplate = new DefaultRecipeTemplate(
				furnaceSlots,
				new ItemStack(Block.furnaceBurning),
				new TextureClip(
						DynamicTexture.instance("recipe_backgrounds"),
						1, 181, 79, 58),
				new TextureClip(
						DynamicTexture.instance("recipe_backgrounds"),
						82, 181, 79, 58));

		addCraftingRecipes(craftingTemplate, smallCraftingTemplate, shapelessTemplate, generator);
		addFurnaceRecipes(furnaceTemplate, generator);
	}

	private void addFurnaceRecipes(RecipeTemplate template, RecipeGenerator generator)
	{
		Map furnaceRecipes = FurnaceRecipes.smelting().getSmeltingList();

		for(Object o: furnaceRecipes.keySet())
		{
			int blockID = (Integer)o;
			Item item = Item.itemsList[blockID];

			if(item != null)
			{
				ItemStack output = (ItemStack)furnaceRecipes.get(o);
				if(item.getHasSubtypes())
				{
					List<ItemStack> items = new ArrayList<ItemStack>();
					item.getSubItems(blockID, null, items);

					for(ItemStack subItem: items)
					{
						generator.addRecipe(
								template,
								new Object[]{
										subItem,
										output
								});
					}
				}
				else
				{
					generator.addRecipe(
							template,
							new Object[]{
									new ItemStack(blockID, 1, 0),
									output
							});
				}
			}
		}


		try
		{
			Field forgeMetadataSmelting = FurnaceRecipes.class.getDeclaredField("metaSmeltingList");
			forgeMetadataSmelting.setAccessible(true);
			Map<List<Integer>, ItemStack> recipes = (Map<List<Integer>, ItemStack>)forgeMetadataSmelting.get(FurnaceRecipes.smelting());

			for(List<Integer> input: recipes.keySet())
			{
				int blockID = input.get(0);
				int metadata = input.get(1);
				ItemStack in = new ItemStack(blockID, 1, metadata);
				ItemStack out = recipes.get(input);

				generator.addRecipe(template, new ItemStack[]{in, out});
			}
		}
		catch(NoSuchFieldException | IllegalAccessException ignored) {}
    }

	private void addCraftingRecipes(RecipeTemplate template, RecipeTemplate templateSmall, RecipeTemplate templateShapeless, RecipeGenerator generator)
	{
		List recipes = CraftingManager.getInstance().getRecipeList();

		int errCount = 0;

		for(Object o: recipes)
		{
			try
			{
				IRecipe recipe = (IRecipe)o;

				Object[] items = generator.getCraftingRecipe(recipe, true);

				if(items == null)
				{
					continue;
				}
				else if(items.length == 5)
				{
					generator.addRecipe(templateSmall, items);
				}
				else if(isShapelessRecipe(recipe))
				{
					generator.addRecipe(templateShapeless, items);
				}
				else
				{
					generator.addRecipe(template, items);
				}
			}
			catch(Exception e)
			{
				if (errCount == -1)
				{
				}
				else if(errCount++ >= 5)
				{
					CraftGuideLog.log("CraftGuide DefaultRecipeProvider: Stack trace limit reached, further stack traces from this invocation will not be logged to the console. They will still be logged to (.minecraft)/config/CraftGuide/CraftGuide.log", true);
					errCount = -1;
				}
				else
				{
					e.printStackTrace();
				}

				CraftGuideLog.log(e);
			}
		}
	}

	private boolean isShapelessRecipe(IRecipe recipe)
	{
		return recipe instanceof ShapelessRecipes ||
			(RecipeGeneratorImplementation.forgeExt != null
					&& RecipeGeneratorImplementation.forgeExt.isShapelessRecipe(recipe));
	}

	@Override
	public String getInfo(ItemStack itemStack)
	{
		int fuel;

		if(Item.itemsList[itemStack.itemID] == null)
		{
			fuel = 0;
		}
		else if(itemStack.hasTagCompound())
		{
			fuel = (itemStack == null) ? 0 : itemStack.getItem().getBurnTime(itemStack) * 2;
		}
		else
		{
			fuel = getCachedBurnTime(itemStack);
		}

		if(fuel > 0)
		{
			double value = fuel / 200.0;
			int round = (int)value;
			int dec = (int)((value - round) * 100);

			StringBuilder builder = new StringBuilder("§7Can fuel ");
			builder.append(round);

			if(dec > 0)
			{
				builder.append('.');

				if(dec < 10)
				{
					builder.append('0');
				}

				builder.append(dec);
			}

			builder.append(" furnace operations");
			return builder.toString();
		}
		else
		{
			return null;
		}
	}

	private static Map<Long, Integer> burnCache = new HashMap();

	private static int getCachedBurnTime(ItemStack stack)
	{
		long lookup = (stack.itemID << 32) | (stack.getHasSubtypes()? CommonUtilities.getItemDamage(stack) : 0);

		Integer value = burnCache.get(lookup);

		if(value == null)
		{
			value = (stack == null) ? 0 : stack.getItem().getBurnTime(stack) * 2;
			burnCache.put(lookup, value);
		}

		return value;
	}
}