package com.camadeusa.module.game;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.utility.ItemStackBuilderUtil;

public class GoldenHead extends Module {
	
	public static ShapedRecipe getRecipe() {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(NetworkCore.getInstance(), "Golden-Head"), getItemStack());
		recipe.shape("ggg", "ghg", "ggg");
		recipe.setIngredient('g', Material.GOLD_INGOT);
		recipe.setIngredient('h', new ItemStackBuilderUtil().toSkullBuilder().buildSkull().getData());
		//recipe.setIngredient('h', new ItemStackBuilderUtil().toSkullBuilder().buildSkull().getType()); //If the above doesnt work...
		return recipe;
	}
	
	public static ItemStack getItemStack() {
		return new ItemStackBuilderUtil(Material.GOLDEN_APPLE).withName("Golden-Head").withLocalizedName("Golden-Head").addEnchantment(Enchantment.ARROW_INFINITE, 3).withItemFlags(ItemFlag.HIDE_ENCHANTS).buildStack();
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getItemMeta().hasLocalizedName()) {
			if (event.getItem().getItemMeta().getLocalizedName().equals("Golden-Head")) {
				for (PotionEffect effect : event.getPlayer().getActivePotionEffects())
			        event.getPlayer().removePotionEffect(effect.getType());
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 7, 3, false, true, Color.MAROON), true);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 7, 3, false, true, Color.MAROON), true);
			}
		}
	}
}
