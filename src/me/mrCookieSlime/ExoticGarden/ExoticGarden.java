package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ExoticGarden.items.Crook;
import me.mrCookieSlime.ExoticGarden.items.GrassSeeds;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Juice;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;

public class ExoticGarden extends JavaPlugin {
	
	public static ExoticGarden instance;
	private static boolean skullitems;

	private List<Berry> berries = new ArrayList<>();
	private List<Tree> trees = new ArrayList<>();
	private Map<String, ItemStack> items = new HashMap<>();

	protected Config cfg;
	
	private Category category_main;
	private Category category_food;
	private Category category_drinks;
	private Category category_magic;
	private Kitchen kitchen;

	
	@Override
	public void onEnable() {
		if (!new File("plugins/ExoticGarden").exists()) new File("plugins/ExoticGarden").mkdirs();
    	if (!new File("plugins/ExoticGarden/schematics").exists()) new File("plugins/ExoticGarden/schematics").mkdirs();
		
    	instance = this;
    	cfg = new Config(this);

		skullitems = cfg.getBoolean("options.item-heads");

		category_main = new Category(new CustomItem(getSkull(Material.NETHER_WART, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="), "&a植物和水果", "", "&a> 點擊開啟"));
		category_food = new Category(new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&a美味佳餚", "", "&a> 點擊開啟"));
		category_drinks = new Category(new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&a飲品類", "", "&a> 點擊開啟"));
		category_magic = new Category(new CustomItem(Material.BLAZE_POWDER, "&5魔力植物", "", "&a> 點擊開啟"));

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&b冰塊"), "ICE_CUBE", RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&b冰塊"), 4))
		.register();

		kitchen = new Kitchen(this);

			registerBerry("葡萄","Grape", "&c", Color.RED, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlOTc2NDliZDk5OTk1NTQxM2ZjYmYwYjI2OWM5MWJlNDM0MmIxMGQwNzU1YmFkN2ExN2U5NWZjZWZkYWIwIn19fQ=="));
			registerBerry("藍莓","Blueberry", "&9", Color.BLUE, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="));
			registerBerry("接骨木果","Elderberry", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU0ODgzYTFlMjJjMzI0ZTc1MzE1MWUyYWM0MjRjNzRmMWNjNjQ2ZWVjOGVhMGRiMzQyMGYxZGQxZDhiIn19fQ=="));
			registerBerry("覆盆子","Raspberry", "&d", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI2MmM0NDViYzJkZDFjNWJiYzhiOTNmMjQ4MmY5ZmRiZWY0OGE3MjQ1ZTFiZGIzNjFkNGE1NjgxOTBkOWI1In19fQ=="));
			registerBerry("黑莓","Blackberry", "&8", Color.GRAY, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0="));
			registerBerry("蔓越莓","Cranberry", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmZTZjNzE4ZmJhNzE5ZmY2MjIyMzdlZDllYTY4MjdkMDkzZWZmYWI4MTRiZTIxOTJlOTY0M2UzZTNkNyJ9fX0="));
			registerBerry("越橘","Cowberry", "&c", Color.FUCHSIA, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0ZTU0YmYyNTVhYjBiMWM0OThjYTNhMGNlYWU1YzdjNDVmMTg2MjNhNWEwMmY3OGE3OTEyNzAxYTMyNDkifX19"));
			registerBerry("草莓","Strawberry", "&4", Color.FUCHSIA, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0="));

			registerPlant("番茄","Tomato", "&4", Material.APPLE, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkxNzIyMjZkMjc2MDcwZGMyMWI3NWJhMjVjYzJhYTU2NDlkYTVjYWM3NDViYTk3NzY5NWI1OWFlYmQifX19"));
			registerPlant("生菜","Lettuce", "&2", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3ZGQ4NDJjOTc1ZDhmYjAzYjFhZGQ2NmRiODM3N2ExOGJhOTg3MDUyMTYxZjIyNTkxZTZhNGVkZTdmNSJ9fX0="));
			registerPlant("茶葉","Tea Leaf", "&a", Material.OAK_LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNGM4YjQ2MTI0N2FiMTdmZTM2MDZlNmUyZjRkMzYzZGNjYWU5ZWQ1YmVkZDAxMmI0OThkN2FlOGViMyJ9fX0="));
			registerPlant("捲心菜", "Cabbage","&2", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNkNmQ2NzMyMGM5MTMxYmU4NWExNjRjZDdjNWZjZjI4OGYyOGMyODE2NTQ3ZGIzMGEzMTg3NDE2YmRjNDViIn19fQ=="));
			registerPlant("甜馬鈴薯","Sweet Potato", "&6", Material.OAK_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZmNDg1NzhiNjY4NGUxNzk5NDRhYjFiYzc1ZmVjNzVmOGZkNTkyZGZiNDU2ZjZkZWY3NjU3NzEwMWE2NiJ9fX0="));
			registerPlant("芥末","Mustard Seed", "&e", Material.GOLD_NUGGET, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ1M2E0MjQ5NWZhMjdmYjkyNTY5OWJjM2U1ZjI5NTNjYzJkYzMxZDAyN2QxNGZjZjdiOGMyNGI0NjcxMjFmIn19fQ=="));
			registerPlant("九里香", "Curry Leaf", "&2", Material.BIRCH_LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzJhZjdmYThiZGYzMjUyZjY5ODYzYjIwNDU1OWQyM2JmYzJiOTNkNDE0MzcxMDM0MzdhYjE5MzVmMzIzYTMxZiJ9fX0="));
			registerPlant("洋蔥", "Onion", "&c", Material.ACACIA_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlMDM2ZTMyN2NiOWQ0ZDhmZWYzNjg5N2E4OTYyNGI1ZDliMThmNzA1Mzg0Y2UwZDdlZDFlMWZjN2Y1NiJ9fX0="));
			registerPlant("大蒜", "Garlic", "&r", Material.ACACIA_LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA1MmQ5YzExODQ4ZWJjYzlmODM0MDMzMjU3N2JmMWQyMmI2NDNjMzRjNmFhOTFmZTRjMTZkNWE3M2Y2ZDgifX19"));
			registerPlant("香菜", "Cilantro", "&a", Material.OAK_LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYxNDkxOTZmM2E4ZDZkNmYyNGU1MWIyN2U0Y2I3MWM2YmFiNjYzNDQ5ZGFmZmI3YWEyMTFiYmU1NzcyNDIifX19"));
			registerPlant("黑胡椒", "Black Pepper", "&8", Material.SPRUCE_LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MmI5YmY5ZjFmNjI5NTg0MmIwZWZiNTkxNjk3YjE0NDUxZjgwM2ExNjVhZTU4ZDBkY2ViZDk4ZWFjYyJ9fX0="));


		registerPlant("玉米", "Corn","&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJkMzgwMmU1ZmFjMDNhZmFiNzQyYjBmM2NjYTQxYmNkNDcyM2JlZTkxMWQyM2JlMjljZmZkNWI5NjVmMSJ9fX0="));
			registerPlant("鳳梨","Pineapple", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGRkODJlNTc1ZGZkNWI3NTc5ZDg5ZGNkMjM1MGM5OTFmMDQ4M2E3NjQ3Y2ZmZDNkMmM1ODdmMjEifX19"));

			registerTree("蘋果", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "OAK_APPLE", "&c", Color.FUCHSIA, "Oak Apple Juice","蘋果汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("椰子", Material.COCOA_BEANS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyN2RlZDU3Yjk0Y2Y3MTViMDQ4ZWY1MTdhYjNmODViZWY1YTdiZTY5ZjE0YjE1NzNlMTRlN2U0MmUyZTgifX19", "COCONUT", "&6", Color.MAROON, "Coconut Milk", "椰奶", false, Material.SAND);
			registerTree("櫻桃", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyMDc2NmI4N2QyNDYzYzM0MTczZmZjZDU3OGIwZTY3ZDE2M2QzN2EyZDdjMmU3NzkxNWNkOTExNDRkNDBkMSJ9fX0=", "CHERRY", "&c", Color.FUCHSIA, "Cherry Juice","櫻桃汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("石榴", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "POMEGRANATE", "&4", Color.RED, "Pomegranate Juice","石榴汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("檸檬", Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU3ZmQ1NmNhMTU5Nzg3NzkzMjRkZjUxOTM1NGI2NjM5YThkOWJjMTE5MmM3YzNkZTkyNWEzMjliYWVmNmMifX19", "LEMON", "&e", Color.YELLOW, "Lemon Juice","檸檬汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("李子", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlkNjY0MzE5ZmYzODFiNGVlNjlhNjk3NzE1Yjc2NDJiMzJkNTRkNzI2Yzg3ZjY0NDBiZjAxN2E0YmNkNyJ9fX0=", "PLUM", "&5", Color.RED, "Plum Juice","梅子汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("萊姆", Material.SLIME_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1MTUzNDc5ZDlmMTQ2YTVlZTNjOWUyMThmNWU3ZTg0YzRmYTM3NWU0Zjg2ZDMxNzcyYmE3MWY2NDY4In19fQ==", "LIME", "&a", Color.LIME, "Lime Juice","萊姆汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("橘子", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==", "ORANGE", "&6", Color.ORANGE, "Orange Juice","橘子汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("桃子", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiYTQxZmU4Mjc1Nzg3MWU4Y2JlYzlkZWQ5YWNiZmQxOTkzMGQ5MzM0MWNmODEzOWQxZGZiZmFhM2VjMmE1In19fQ==", "PEACH", "&5", Color.RED, "Peach Juice","水蜜桃汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("梨子", Material.SLIME_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRlMjhkZjg0NDk2MWE4ZWNhOGVmYjc5ZWJiNGFlMTBiODM0YzY0YTY2ODE1ZThiNjQ1YWVmZjc1ODg5NjY0YiJ9fX0=", "PEAR", "&a", Color.LIME, "Pear Juice","梨子汁", true, Material.DIRT, Material.GRASS_BLOCK);
			registerTree("火龍果", Material.APPLE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3ZDczYTkxYjUyMzkzZjJjMjdlNDUzZmI4OWFiM2Q3ODQwNTRkNDE0ZTM5MGQ1OGFiZDIyNTEyZWRkMmIifX19\\", "DRAGON_FRUIT", "&d", Color.FUCHSIA, "Dragon Fruit Juice","火龍果汁", true, Material.DIRT, Material.GRASS_BLOCK);

			registerDishes();

			registerMagicalPlant("煤炭","Coal", new ItemStack(Material.COAL, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4OGY1ZGRhZjUyYzU4NDIyODdiOTQyN2E3NGRhYzhmMDkxOWViMmZkYjFiNTEzNjVhYjI1ZWIzOTJjNDcifX19",
			new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

			registerMagicalPlant("鐵","Iron", new ItemStack(Material.IRON_INGOT), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI5N2JkZjkyYjYxOTI2ZTM5ZjVjZGRmMTJmOGY3MTMyOTI5ZGVlNTQxNzcxZTBiNTkyYzhiODJjOWFkNTJkIn19fQ==",
			new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

			registerMagicalPlant("金","Gold", SlimefunItems.GOLD_4K, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkZjg5MjI5M2E5MjM2ZjczZjQ4ZjllZmU5NzlmZTA3ZGJkOTFmN2I1ZDIzOWU0YWNmZDM5NGY2ZWNhIn19fQ==",
			new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});
		
			registerMagicalPlant("銅", "Copper", new CustomItem(SlimefunItems.COPPER_DUST, 8),  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkyM2RiZmQ4ZjMxMTI2OTBiYjVhNjE2OGE4ZDNjYTVhYjllN2Q0M2IxZDExY2ZjYjY0M2RlN2RmZTIxIn19fQ==",
				new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

			registerMagicalPlant("紅石","Redstone", new ItemStack(Material.REDSTONE, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThkZWVlNTg2NmFiMTk5ZWRhMWJkZDc3MDdiZGI5ZWRkNjkzNDQ0ZjFlM2JkMzM2YmQyYzc2NzE1MWNmMiJ9fX0=",
			new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

			registerMagicalPlant("青金石", "Lapis", new ItemStack(Material.LAPIS_LAZULI, 16), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFhMGQwZmVhMWFmYWVlMzM0Y2FiNGQyOWQ4Njk2NTJmNTU2M2M2MzUyNTNjMGNiZWQ3OTdlZDNjZjU3ZGUwIn19fQ==",
			new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

			registerMagicalPlant("珍珠","Ender", new ItemStack(Material.ENDER_PEARL, 4), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUzNWFhZGU4MTI5MmU2ZmY0Y2QzM2RjMGVhNmExMzI2ZDA0NTk3YzBlNTI5ZGVmNDE4MmIxZDE1NDhjZmUxIn19fQ==",
			new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

			registerMagicalPlant("石英","Quartz", new ItemStack(Material.QUARTZ, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZkZTU4ZDU4M2MxMDNjMWNkMzQ4MjQzODBjOGE0NzdlODk4ZmRlMmViOWE3NGU3MWYxYTk4NTA1M2I5NiJ9fX0=",
			new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

			registerMagicalPlant("鑽石","Diamond", new ItemStack(Material.DIAMOND), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg4Y2Q2ZGQ1MDM1OWM3ZDU4OThjN2M3ZTNlMjYwYmZjZDNkY2IxNDkzYTg5YjllODhlOWNiZWNiZmU0NTk0OSJ9fX0=",
			new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

			registerMagicalPlant("綠寶石","Emerald", new ItemStack(Material.EMERALD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZjNDk1ZDFlNmViNTRhMzg2MDY4YzZjYjEyMWM1ODc1ZTAzMWI3ZjYxZDcyMzZkNWYyNGI3N2RiN2RhN2YifX19",
			new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

			registerMagicalPlant("螢光石","Glowstone", new ItemStack(Material.GLOWSTONE_DUST, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2JlZDhkZjcxNGNlYTA2M2U0NTdiYTVlODc5MzExNDFkZTI5M2RkMWQ5YjkxNDZiMGY1YWIzODM4NjYifX19",
			new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

			registerMagicalPlant("黑曜石","Obsidian", new ItemStack(Material.OBSIDIAN, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0MGI4N2Q1MjI3MWQyYTc1NWRlZGM4Mjg3N2UwZWQzZGY2N2RjYzQyZWE0NzllYzE0NjE3NmIwMjc3OWE1In19fQ==",
			new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

			registerMagicalPlant("史萊姆","Slime", new ItemStack(Material.SLIME_BALL, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlNjVlNmU1MTEzYTUxODdkYWQ0NmRmYWQzZDNiZjg1ZThlZjgwN2Y4MmFhYzIyOGE1OWM0YTk1ZDZmNmEifX19",
			new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		ItemStack grass_seeds = new CustomItem(Material.PUMPKIN_SEEDS, "&r草種子", "", "&7&o可以被種在泥土上");

		new Crook(Categories.TOOLS, new CustomItem(Material.WOODEN_HOE, "&r鐮刀", "", "&7+ &b25% &7種子掉落率"), "CROOK", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null})
		.register(false);

		new GrassSeeds(category_main, grass_seeds, "GRASS_SEEDS", new RecipeType(new CustomItem(Material.GRASS, "&7用手破壞雜草")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(false);

		new PlantsListener(this);
		new FoodListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		items.put("OAK_SAPLING", new ItemStack(Material.OAK_SAPLING));
		items.put("SPRUCE_SAPLING", new ItemStack(Material.SPRUCE_SAPLING));
		items.put("BIRCH_SAPLING", new ItemStack(Material.BIRCH_SAPLING));
		items.put("JUNGLE_SAPLING", new ItemStack(Material.JUNGLE_SAPLING));
		items.put("ACACIA_SAPLING", new ItemStack(Material.ACACIA_SAPLING));
		items.put("DARK_OAK_SAPLING", new ItemStack(Material.DARK_OAK_SAPLING));
		items.put("GRASS_SEEDS", grass_seeds);

		Iterator<String> iterator = items.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			cfg.setDefaultValue("grass-drops." + key, true);
			if (!cfg.getBoolean("grass-drops." + key)) iterator.remove();
		}
		cfg.save();
	}

	private void registerDishes() {
		new Juice(category_drinks, new CustomPotion("&a萊姆冰沙", Color.LIME, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "LIME_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LIME_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&4番茄汁", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o回復 &b&o" + "3.0" + " &7&o飽食度"), "TOMATO_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATO"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&c葡萄酒", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "WINE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("GRAPE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&e檸檬冰茶", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "LEMON_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&d覆盆子冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "RASPBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("RASPBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&d桃子冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "PEACH_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PEACH"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&4草莓冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "STRAWBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("STRAWBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&c櫻桃冰茶", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "CHERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CHERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&6泰式奶茶", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 14, 0), "", "&7&o回復 &b&o" + "7.0" + " &7&o飽食度"), "THAI_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("COCONUT_MILK"), null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0="), "&r南瓜麵包", "", "&7&o回復 &b&o" + "4.0" + " &7&o飽食度"), "PUMPKIN_BREAD",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4ZDUzNmM4YzJjMjU5NmJjYzE3MDk1OTBhOWQ3ZTMzMDYxYzU2ZTY1ODk3NGNkODFiYjgzMmVhNGQ4ODQyIn19fQ=="), "&r美乃滋"), "MAYO", RecipeType.GRIND_STONE, false,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZTk5NjIxYjk3NzNiMjllMzc1ZTYyYzY0OTVmZjFhYzg0N2Y4NWIyOTgxNmMyZWI3N2I1ODc4NzRiYTYyIn19fQ=="), "&e芥末"), "MUSTARD", RecipeType.GRIND_STONE, false,
		new ItemStack[] {getItem("MUSTARD_SEED"), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg2ZjE5YmYyM2QyNDhlNjYyYzljOGI3ZmExNWVmYjhhMWYxZDViZGFjZDNiODYyNWE5YjU5ZTkzYWM4YSJ9fX0="), "&cBBQ醬"), "BBQ_SAUCE", RecipeType.ENHANCED_CRAFTING_TABLE, false,
		new ItemStack[] {getItem("TOMATO"), getItem("MUSTARD"), getItem("SALT"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFjYjI4ZmI4YTMxMDQ0M2FmMDJjN2ExMjgzYWNlOTVhOTkwNmIyZTBlNmYzNjM2NTk3ZWRiZThjYWQ0ZSJ9fX0="), "&r植物油"), "VEGETABLE_OIL", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT_SEEDS), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(Material.SUGAR, "&r玉米澱粉"), "CORNMEAL", RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("CORN"), null, null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.SUGAR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA2YmUyZGYyMTIyMzQ0YmRhNDc5ZmVlY2UzNjVlZTBlOWQ1ZGEyNzZhZmEwZThjZThkODQ4ZjM3M2RkMTMxIn19fQ=="), "&r酵母"), "YEAST", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDdiMTU1ZWRmNDQwY2I4N2VjOTQ0ODdjYmE2NGU4ZDEyODE3MWViMTE4N2MyNmQ1ZmZlNThiZDc5NGMifX19"), "&8糖蜜"), "MOLASSES", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT), new ItemStack(Material.SUGAR_CANE), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.SUGAR, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY0ZDQyNDcyNzhlMTQ5ODM3NGFhNmIwZTQ3MzY4ZmU0ZjEzOGFiYzk0ZTU4M2U4ODM5OTY1ZmJlMjQxYmUifX19"), "&r黑糖"), "BROWN_SUGAR", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("MOLASSES"), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZmE5NDM5YmZkODM4NDQ2NDE0NmY5YzY3ZWJkNGM1ZmJmNDE5NjkyNDg5MjYyN2VhZGYzYmNlMWZmIn19fQ=="), "&r鄉村肉汁"), "COUNTRY_GRAVY", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), getItem("BLACK_PEPPER"), null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COCOA_BEANS, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ=="), "&r巧克力棒", "", "&7&o回復 &b&o" + "1.5" + " &7&o飽食度"), "CHOCOLATE_BAR",
		new ItemStack[] {new ItemStack(Material.COCOA_BEANS), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.MUSHROOM_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ=="), "&r馬鈴薯沙拉", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "POTATO_SALAD",
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), getItem("ONION"), new ItemStack(Material.BOWL), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r雞肉三明治", "", "&7&o回復 &b&o" + "5.5" + " &7&o飽食度"), "CHICKEN_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r鮮魚三明治", "", "&7&o回復 &b&o" + "5.5" + " &7&o飽食度"), "FISH_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_COD), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTAyZTkyZjEzZGUzYmVlNjkyMjhjMzg0NDc4ZTc2MTIzMDY4MWU1ZmNlOWJkYTE5NWRhZWFmODQ4NDEzOTMzMSJ9fX0="), "&rBagel", "", "&7&oRestores &b&o" + "2.0" + " &7&oHunger"), "BAGEL",
		new ItemStack[] {getItem("YEAST"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.MUSHROOM_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ=="), "&r蛋沙拉", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "EGG_SALAD",
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.MUSHROOM_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYzNjZmMTc0MjhhNDk5MDEyNjg0NGY3NGEwMmRiZjU1MjRmMzViZTEzMjNmOGZhYjBiZjYxYTU3ZmY0MWRlMyJ9fX0="), "&4番茄湯", "", "&7&o回復 &b&o" + "5.5" + " &7&o飽食度"), "TOMATO_SOUP",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATO"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.MUSHROOM_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ=="), "&c草莓田園沙拉", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "STRAWBERRY_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("STRAWBERRY"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.MUSHROOM_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ=="), "&c葡萄田園沙拉", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "GRAPE_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("GRAPE"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.RABBIT_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ZTBkZDU0ODlmMDNlZmRjODA4MzA4OGY1MjFiODI5NDZjZGVjOThmYzFjOTRjNGUwOTc5MmU0NzM1MTg0YSJ9fX0="), "&6咖哩雞", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), "CHICKEN_CURRY",
		new ItemStack[] {getItem("CILANTRO"), new ItemStack(Material.COOKED_CHICKEN), getItem("BROWN_SUGAR"), getItem("CURRY_LEAF"), getItem("VEGETABLE_OIL"), getItem("CURRY_LEAF"), getItem("ONION"), new ItemStack(Material.BOWL), getItem("GARLIC")},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.RABBIT_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ZTBkZDU0ODlmMDNlZmRjODA4MzA4OGY1MjFiODI5NDZjZGVjOThmYzFjOTRjNGUwOTc5MmU0NzM1MTg0YSJ9fX0="), "&f泰式椰香咖哩雞", "", "&7&o回復 &b&o" + "9.5" + " &7&o飽食度"), "COCONUT_CHICKEN_CURRY",
		new ItemStack[] {getItem("COCONUT"), getItem("COCONUT"), getItem("CHICKEN_CURRY"), null, null, null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWYwOTQ0NTZmZDc5NGI2NTMxZmM2ZGVjNmYzOTZiNjgwYjk1MzYwMDIwNjNlMTFjZTI0ZDBhNzRiMGI3ZDg4NSJ9fX0="), "&6比司吉", "", "&7&o回復 &b&o" + "2.0" + " &7&o飽食度"), "BISCUIT",
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.RABBIT_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhiYmI4MzVlMjJkOWVjNjJlMjI0MTFiOGUwMTUxMzhkNTU5NzI4M2FkMzZlNjE4ZmU0NGJhNWYxYTZiNjBmZCJ9fX0="), "&f香腸肉汁佐比司吉", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "BISCUITS_GRAVY",
		new ItemStack[] {getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("BISCUIT"), getItem("BISCUIT"), getItem("BISCUIT"), null, new ItemStack(Material.BOWL), null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&r起司蛋糕", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), "CHEESECAKE",
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&c櫻桃起司蛋糕", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "CHERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CHERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&9藍莓起司蛋糕", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "BLUEBERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6南瓜起司蛋糕", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "PUMPKIN_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6甜梨起司蛋糕", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "SWEETENED_PEAR_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PEAR"), null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZjMzY1MjNjMmQxMWI4YzhlYTJlOTkyMjkxYzUyYTY1NDc2MGVjNzJkY2MzMmRhMmNiNjM2MTY0ODFlZSJ9fX0="), "&8黑莓餡餅", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "BLACKBERRY_COBBLER",
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("BLACKBERRY"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&r帕芙洛娃", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "PAVLOVA",
		new ItemStack[] {getItem("LEMON"), getItem("STRAWBERRY"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(Material.GOLDEN_CARROT, "&6玉米棒", "", "&7&o回復 &b&o" + "4.5" + " &7&o飽食度"), "CORN_ON_THE_COB",
		new ItemStack[] {SlimefunItems.BUTTER, getItem("CORN"), null, null, null, null, null, null, null},
		9)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.MUSHROOM_STEW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE3NGIzNGM1NDllZWQ4YmFmZTcyNzYxOGJhYjY4MjFhZmNiMTc4N2I1ZGVjZDFlZWNkNmMyMTNlN2U3YzZkIn19fQ=="), "&r奶油玉米", "", "&7&o回復 &b&o" + "4.0" + " &7&o飽食度"), "CREAMED_CORN",
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("CORN"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		8)
		.register();
		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKED_PORKCHOP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ=="), "&r培根", "", "&7&o回復 &b&o" + "1.5" + " &7&o飽食度"), "BACON",
		new ItemStack[] {new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r三明治", "", "&7&o回復 &b&o" + "9.5" + " &7&o飽食度"), "SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rBLT三明治", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "BLT",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r生菜雞肉三明治", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "LEAFY_CHICKEN_SANDWICH",
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		1)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r生菜魚肉三明治", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "LEAFY_FISH_SANDWICH",
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r漢堡", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "HAMBURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r起司堡", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "CHEESEBURGER",
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r培根起司堡", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "BACON_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r雙重起司堡", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), "DELUXE_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMzZmE3ZDNlNjNiMjgwYTVkN2UyYmIwOTMzMmRmZjg2YjE3ZGVjZDJiMDllY2NkZDYyZGE1MjY1NTk3Zjc0ZCJ9fX0="), "&r大蒜麵包", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "GARLIC_BREAD",
		new ItemStack[] {getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMzZmE3ZDNlNjNiMjgwYTVkN2UyYmIwOTMzMmRmZjg2YjE3ZGVjZDJiMDllY2NkZDYyZGE1MjY1NTk3Zjc0ZCJ9fX0="), "&r大蒜起司麵包", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "GARLIC_CHEESE_BREAD",
		new ItemStack[] {SlimefunItems.CHEESE, getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19"), "&r胡蘿蔔蛋糕", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "CARROT_CAKE",
		new ItemStack[] {new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r雞肉堡", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "CHICKEN_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r雞肉起司堡", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "CHICKEN_CHEESEBURGER",
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&r培根堡", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "BACON_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r培根三明治", "", "&7&o回復 &b&o" + "9.5" + " &7&o飽食度"), "BACON_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&r塔可", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&r魚肉塔可", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "FISH_TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_COD), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkN2MwYTA0ZjE0ODVjN2EzZWYyNjFhNDhlZTgzYjJmMWFhNzAxYWIxMWYzZmM5MTFlMDM2NmE5Yjk3ZSJ9fX0="), "&r美味塔可", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "STREET_TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("CILANTRO"), getItem("ONION"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQwMGRmYjNhNTdjMDY4YTBjYzdiNjI0ZDhkODg1MjA3MDQzNWQyNjM0YzBlNWRhOWNiYmFiNDYxNzRhZjBkZiJ9fX0="), "&c果醬餅", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "JAMMY_DODGER",
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null}, 
		8)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&r薄煎餅", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "PANCAKES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&r藍莓煎餅", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "BLUEBERRY_PANCAKES",
		new ItemStack[] {getItem("PANCAKES"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ0Y2E5OWUzMDhhMTg2YjMwMjgxYjIwMTdjNDQxODlhY2FmYjU5MTE1MmY4MWZlZWE5NmZlY2JlNTcifX19"), "&r甜莓鬆餅", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "SWEET_BERRY_PANCAKES",
		new ItemStack[] {getItem("PANCAKES"), new ItemStack(Material.SWEET_BERRIES), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTYzYjhhZWFmMWRmMTE0ODhlZmM5YmQzMDNjMjMzYTg3Y2NiYTNiMzNmN2ZiYTljMmZlY2FlZTk1NjdmMDUzIn19fQ=="), "&r薯條", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "FRIES",
		new ItemStack[] {new ItemStack(Material.POTATO), getItem("SALT"), null, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&r爆米花", "", "&7&o回復 &b&o" + "4.0" + " &7&o飽食度"), "POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&r爆米花 &7(甜的)", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "SWEET_POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&r爆米花 &7(鹹的)", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "SALTY_POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), getItem("SALT"), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&r牧羊人派", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), "SHEPARDS_PIE",
		new ItemStack[] {getItem("CABBAGE"), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&r雞肉派", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "CHICKEN_POT_PIE",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO), null, null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&r巧克力蛋糕", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "CHOCOLATE_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&r奶油餅乾", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "CREAM_COOKIE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&r藍莓馬芬", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "BLUEBERRY_MUFFIN",
		new ItemStack[] {getItem("BLUEBERRY"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&r南瓜馬芬", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "PUMPKIN_MUFFIN",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&r巧克力脆片馬芬", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "CHOCOLATE_CHIP_MUFFIN",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&r波士頓奶油派", "", "&7&o回復 &b&o" + "4.5" + " &7&o飽食度"), "BOSTON_CREAM_PIE",
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&r熱狗", "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "HOT_DOG",
		new ItemStack[] {null, null, null, null, new ItemStack(Material.COOKED_PORKCHOP), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&r培根焗烤起司熱狗", "&7&o\"When I chef\" - @Eyamaz", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "BACON_WRAPPED_CHEESE_FILLED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rBBQ燒烤培根熱狗", "&7&o\"wanna talk about hot dogs?\" - @Pahimar", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "BBQ_BACON_WRAPPED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rBBQ燒烤培根雙倍起司熱狗", "&7&o\"When I chef\" - @Eyamaz", "", "&7&o回復 &b&o" + "10.0" + " &7&o飽食度"), "BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE",
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.COOKED_PORKCHOP), getItem("BACON"), getItem("CORNMEAL"), getItem("CHEESE"), getItem("CORNMEAL")},
		20)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhlOTRkZGQ3NjlhNWJlYTc0ODM3NmI0ZWM3MzgzZmQzNmQyNjc4OTRkN2MzYmVlMDExZThlNGY1ZmNkNyJ9fX0="), "&a甜茶", "", "&7&o回復 &b&o" + "3.0" + " &7&o飽食度"), "SWEETENED_TEA",
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExNTExYmRkNTViY2I4MjgwM2M4MDM5ZjFjMTU1ZmQ0MzA2MjYzNmUyM2Q0ZDQ2YzRkNzYxYzA0ZDIyYzIifX19"), "&6熱巧克力", "", "&7&o回復 &b&o" + "4.0" + " &7&o飽食度"), "HOT_CHOCOLATE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&6椰林飄香", "", "&7&o回復 &b&o" + "7.0" + " &7&o飽食度"), "PINACOLADA",
		new ItemStack[] {getItem("PINEAPPLE"), getItem("ICE_CUBE"), getItem("COCONUT_MILK"), null, null, null, null, null, null},
		14)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.NETHER_WART, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ0ZWQ3YzczYWMyODUzZGZjYWE5Y2E3ODlmYjE4ZGExZDQ3YjE3YWQ2OGIyZGE3NDhkYmQxMWRlMWE0OWVmIn19fQ=="), "&c巧克力草莓", "", "&7&o回復 &b&o" + "2.5" + " &7&o飽食度"), "CHOCOLATE_STRAWBERRY",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		5)
		.register();

		new Juice(category_drinks, new CustomPotion("&e清新檸檬汁", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 8, 0), "", "&7&o回復 &b&o" + "4.0" + " &7&o飽食度"), "LEMONADE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&r甜馬鈴薯派", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), "SWEET_POTATO_PIE",
		new ItemStack[] {getItem("SWEET_POTATO"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&r萊明頓蛋糕", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "LAMINGTON",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCONUT"), null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&r鬆餅", "", "&7&o回復 &b&o" + "6.0" + " &7&o飽食度"), "WAFFLES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&r俱樂部三明治", "", "&7&o回復 &b&o" + "9.5" + " &7&o飽食度"), "CLUB_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATO"), getItem("LETTUCE"), getItem("MUSTARD"), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&r捲餅", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "BURRITO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&r雞肉捲餅", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "CHICKEN_BURRITO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_CHICKEN), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ=="), "&r炙燒三明治", "", "&7&o回復 &b&o" + "5.5" + " &7&o飽食度"), "GRILLED_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDNhMzU3NGE4NDhmMzZhZTM3MTIxZTkwNThhYTYxYzEyYTI2MWVlNWEzNzE2ZjZkODI2OWUxMWUxOWUzNyJ9fX0="), "&r千層麵", "", "&7&o回復 &b&o" + "8.5" + " &7&o飽食度"), "LASAGNA",
		new ItemStack[] {getItem("TOMATO"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATO"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzNjZjYTE3OTc0ODkyZTRmZDRjN2I5YjE4ZmViMTFmMDViYTJlYzQ3YWE1MDM1YzgxYTk1MzNiMjgifX19"), "&r冰淇淋", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), "ICE_CREAM",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), getItem("STRAWBERRY"), null, null, null, null},
		16)
		.register();

		new Juice(category_drinks, new CustomPotion("&6鳳梨汁", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o回復 &b&o" + "3.0" + " &7&o飽食度"), "PINEAPPLE_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("PINEAPPLE"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion("&6鳳梨冰沙", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), "PINEAPPLE_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r提拉米蘇", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), "TIRAMISU",
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r草莓提拉米蘇", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "TIRAMISU_WITH_STRAWBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r覆盆子提拉米蘇", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "TIRAMISU_WITH_RASPBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("RASPBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOWBALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&r黑莓提拉米蘇", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "TIRAMISU_WITH_BLACKBERRIES",
		new ItemStack[] {getItem("TIRAMISU"), getItem("BLACKBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&r巧克力梨子蛋糕", "", "&7&o回復 &b&o" + "9.5" + " &7&o飽食度"), "CHOCOLATE_PEAR_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&c蘋果梨子蛋糕", "", "&7&o回復 &b&o" + "9.0" + " &7&o飽食度"), "APPLE_PEAR_CAKE",
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public void registerTree(String name, Material material, String texture, String fruitName, String color, Color pcolor, String juice_id, String juice, boolean pie, Material... soil) {
		Tree tree = new Tree(fruitName, fruitName, texture, soil);
		trees.add(tree);

		items.put(fruitName + "_SAPLING", new CustomItem(Material.OAK_SAPLING, color + name + "樹苗", 0));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + name + "樹苗"), fruitName + "_SAPLING", new RecipeType(new CustomItem(Material.GRASS, "&7用手破壞草")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		try {
			new EGPlant(category_main, new CustomItem(getSkull(material, texture), color + name), fruitName, new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7藉由特定的樹獲得")), true,
			new ItemStack[] {null, null, null, null, getItem(fruitName + "_SAPLING"), null, null, null, null})
			.register();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (pcolor != null) {
			new Juice(category_drinks, new CustomPotion(color + juice, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o回復 &b&o" + "3.0" + " &7&o飽食度"), juice_id.toUpperCase().replace(" ", "_"), RecipeType.JUICER,
			new ItemStack[] {getItem(fruitName), null, null, null, null, null, null, null, null})
			.register();
		}

		if (pie) {
			try {
				new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + name + "派", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), fruitName+ "_PIE",
				new ItemStack[] {getItem(fruitName), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
				13)
				.register();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!new File("plugins/ExoticGarden/schematics", fruitName + "_TREE.schematic").exists())
			saveResource("schematics/" + fruitName + "_TREE.schematic", false);
	}

	public void registerBerry(String name, String id, String color, Color pcolor, PlantType type, PlantData data) {
		Berry berry = new Berry(id.toUpperCase(), type, data);
		berries.add(berry);

		items.put(id.toUpperCase() + "_BUSH", new CustomItem(Material.OAK_SAPLING, color + name + "苗"));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + name + "苗"), id.toUpperCase() + "_BUSH", new RecipeType(new CustomItem(Material.GRASS, "&7用手破壞草")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(Material.NETHER_WART, data.getTexture()), color + name), id.toUpperCase(), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7藉由特定的植物獲得")), true,
		new ItemStack[] {null, null, null, null, getItem(id.toUpperCase() + "_BUSH"), null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + name + "汁", pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o回復 &b&o" + "3.0" + " &7&o飽食度"), id.toUpperCase() + "_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem(id.toUpperCase()), null, null, null, null, null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + name + "果昔", pcolor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o回復 &b&o" + "5.0" + " &7&o飽食度"), id.toUpperCase() + "_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(id.toUpperCase() + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		try {
			new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4YTkzOTA5M2FiMWNkZTY2NzdmYWY3NDgxZjMxMWU1ZjE3ZjYzZDU4ODI1ZjBlMGMxNzQ2MzFmYjA0MzkifX19"), color + name + "果醬三明治", "", "&7&o回復 &b&o" + "8.0" + " &7&o飽食度"), id.toUpperCase() + "_JELLY_SANDWICH",
			new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(id.toUpperCase() + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
			16)
			.register();

			new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + name + "派", "", "&7&o回復 &b&o" + "6.5" + " &7&o飽食度"), id.toUpperCase() + "_PIE",
			new ItemStack[] {getItem(id.toUpperCase()), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem(): null;
	}

	public static ItemStack getSkull(Material material, String texture) {
		try {
			return skullitems && !texture.equals("NO_SKULL_SPECIFIED") ? CustomSkull.getItem(texture) : new ItemStack(material);
		} catch (Exception e) {
			e.printStackTrace();
			return new ItemStack(material);
		}
	}

	public void registerPlant(String name, String id, String color, Material material, PlantType type, PlantData data) {
		Berry berry = new Berry(id.toUpperCase().replace(" ", "_"), type, data);
		berries.add(berry);

		items.put(id.toUpperCase() + "_BUSH", new CustomItem(Material.OAK_SAPLING, color + name + "苗"));

		new SlimefunItem(category_main, new CustomItem(Material.OAK_SAPLING, color + name + "苗"), id.toUpperCase().replace(" ", "_") + "_BUSH", new RecipeType(new CustomItem(Material.GRASS, "&7用手破壞草")),
		new ItemStack[] {null, null, null, null, new CustomItem(Material.GRASS, 1), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(material, data.getTexture()), color + name), id.toUpperCase().replace(" ", "_"), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7藉由特定的果叢獲得")), true,
		new ItemStack[] {null, null, null, null, getItem(id.toUpperCase().replace(" ", "_") + "_BUSH"), null, null, null, null})
		.register();
	}

	public void registerMagicalPlant(String name, String id, ItemStack item, String skull, ItemStack[] recipe) {
		ItemStack essence = new CustomItem(Material.BLAZE_POWDER, "&r魔力本質", "", "&7" + name);

		Berry berry = new Berry(essence, id.toUpperCase() + "_ESSENCE", PlantType.ORE_PLANT, new PlantData(skull));
		berries.add(berry);

		new SlimefunItem(category_magic, new CustomItem(Material.OAK_SAPLING, "&r" + name + "苗"), id.toUpperCase().replace(" ", "_") + "_PLANT", RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register();

		HandledBlock plant = new HandledBlock(category_magic, essence, id.toUpperCase().replace(" ", "_") + "_ESSENCE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {essence, essence, essence, essence, null, essence, essence, essence, essence});

		plant.setRecipeOutput(item.clone());
		plant.register();
	}

	public static Berry getBerry(Block block) {
		SlimefunItem item = BlockStorage.check(block);
        if (item instanceof HandledBlock) {
			for (Berry berry : instance.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) return berry;
			}
		}
		return null;
	}

	public static ItemStack harvestPlant(Block block) {
		ItemStack itemstack = null;
		SlimefunItem item = BlockStorage.check(block);
		if (item != null) {
			for (Berry berry : instance.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) {
					switch (berry.getType()) {
						case ORE_PLANT:
						case DOUBLE_PLANT: {
							Block plant;
							if (BlockStorage.check(block.getRelative(BlockFace.DOWN)) == null) {
								plant = block;
                                BlockStorage.clearBlockInfo(block.getRelative(BlockFace.UP));
								block.getWorld().playEffect(block.getRelative(BlockFace.UP).getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.getRelative(BlockFace.UP).setType(Material.AIR);
							}
							else {
								plant = block.getRelative(BlockFace.DOWN);
                                BlockStorage.clearBlockInfo(block);
								block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.setType(Material.AIR);
							}
							plant.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
                            BlockStorage._integrated_removeBlockInfo(block.getLocation(), false);
							BlockStorage.store(plant, getItem(berry.toBush()));
							break;
						}
						default: {
							block.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
                            BlockStorage._integrated_removeBlockInfo(block.getLocation(), false);
							BlockStorage.store(block, getItem(berry.toBush()));
							break;
						}
					}
				}
			}
		}
		return itemstack;
	}
	
	public static Kitchen getKitchen() {
		return instance.kitchen;
	}
	
	public static List<Tree> getTrees() {
		return instance.trees;
	}
	
	public static List<Berry> getBerries() {
		return instance.berries;
	}

	public static Map<String, ItemStack> getItems() {
		return instance.items;
	}

}
