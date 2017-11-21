package com.camadeusa.module.game.uhcsg;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.camadeusa.module.game.GoldenHead;

public enum UHCSGChestContents {
	TIER1(0.8f, 1),
	TIER2(0.18f, 2),
	TIER3(0.02f, 3);
	
private final float percent;
private final int tier;

ItemStack[] tier1Items = {
		new ItemStack(Material.STRING, 3),	 		new ItemStack(Material.IRON_CHESTPLATE, 1),	new ItemStack(Material.IRON_HELMET, 1),
		new ItemStack(Material.IRON_BOOTS, 1), 		new ItemStack(Material.GOLDEN_APPLE, 1),		new ItemStack(Material.GOLD_INGOT, 4),
		new ItemStack(Material.GOLD_INGOT, 6),		new ItemStack(Material.ARROW, 12),			new ItemStack(Material.BREAD, 2),
		new ItemStack(Material.APPLE, 1),		 	new ItemStack(Material.WATER_BUCKET, 1),		new ItemStack(Material.LAVA_BUCKET, 1),
		new ItemStack(Material.DIAMOND_HELMET, 1),	new ItemStack(Material.DIAMOND_BOOTS, 1),	new ItemStack(Material.DIAMOND, 1),
		new ItemStack(Material.WOOD, 24),			new ItemStack(Material.COBBLESTONE, 24),		new ItemStack(Material.MONSTER_EGG, 1, (byte) 100), // Horse Spawn egg -- Hopefully
		new ItemStack(Material.IRON_BARDING, 1),		new ItemStack(Material.GOLD_BARDING, 1),		new ItemStack(Material.EXP_BOTTLE, 16),
		new ItemStack(Material.EXP_BOTTLE, 20),		new ItemStack(Material.EXP_BOTTLE, 24),		getEnchantBook(Enchantment.DAMAGE_ALL, 1, false),
		getEnchantBook(Enchantment.ARROW_DAMAGE, 1, false),					getEnchantBook(Enchantment.DAMAGE_ALL, 2, false),
		getEnchantBook(Enchantment.ARROW_DAMAGE, 2, false),					getEnchantBook(Enchantment.PROTECTION_ENVIRONMENTAL, 2, false),		
};

ItemStack[] tier2Items = {
		new ItemStack(Material.GOLD_INGOT, 12),		new ItemStack(Material.GOLDEN_APPLE, 1),		new ItemStack(Material.DIAMOND_CHESTPLATE, 1),
		new ItemStack(Material.DIAMOND_LEGGINGS, 1),	new ItemStack(Material.DIAMOND_HELMET, 1),	new ItemStack(Material.DIAMOND_BOOTS, 1),
		new ItemStack(Material.IRON_SWORD, 1),		GoldenHead.getItemStack(),					new ItemStack(Material.ENDER_PEARL, 1),
		new ItemStack(Material.DIAMOND_BARDING, 1),	
		getEnchantBook(Enchantment.DAMAGE_ALL, 3, true),						getPotionItemStack(PotionType.SPEED, 2, 30, false, false),
		getPotionItemStack(PotionType.SPEED, 1, 60, false, false),				getEnchantBook(Enchantment.ARROW_DAMAGE, 3, true),
};

ItemStack[] tier3Items = {
		getEnchantBook(Enchantment.FIRE_ASPECT, 1, false), 					getEnchantBook(Enchantment.ARROW_KNOCKBACK, 2, false),
		getEnchantBook(Enchantment.ARROW_FIRE, 1, true), 						getPotionItemStack(PotionType.STRENGTH, 1, 30, false, false),
		getPotionItemStack(PotionType.FIRE_RESISTANCE, 1, 60, false, false),	getItemWithEnchant(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), Enchantment.PROTECTION_ENVIRONMENTAL, 4),
		new ItemStack(Material.ENDER_PEARL, 3),
};
	
	UHCSGChestContents(float percent, int tier) {
		this.percent = percent;
		this.tier = tier;
	}
	
	public float getPercent() {
		return percent;
	}
	
	public int getTierInt() {
		return tier;
	}
	
	public ItemStack[] getTierContents() {
		switch (tier) {
		case 1:
			return tier1Items;
		case 2:
			return tier2Items;
		case 3:
			return tier3Items;
		default:
			return tier1Items;
		}
	}
	
	private ItemStack getItemWithEnchant(ItemStack i, Enchantment e, int level) {
		i.addEnchantment(e, level);
		return i;
	}
	
	private ItemStack getEnchantBook(Enchantment e, int level, boolean ignoreLevelRestriction) {
		ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
		EnchantmentStorageMeta bm = (EnchantmentStorageMeta) book.getItemMeta();
		bm.addEnchant(e, level, ignoreLevelRestriction);
		book.setItemMeta(bm);
		
		return book;
	}
	public ItemStack getPotionItemStack(PotionType type, int level, int length, boolean extend, boolean upgraded){
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(type.getEffectType(), length, level), true);
        meta.setBasePotionData(new PotionData(type, extend, upgraded));
        potion.setItemMeta(meta);
        return potion;
    }
}
