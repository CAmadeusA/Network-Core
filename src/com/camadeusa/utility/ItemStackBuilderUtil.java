package com.camadeusa.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemStackBuilderUtil {

	// Fundamentals
	private Material material = Material.AIR;
	private int amount = 1;
	private short durability = 0; // id -> 324:2 <- durability

	// Meta
	private String localizedName = null;
	private String name = null;
	private List<String> lore = null;

	// Features
	private boolean unbreakable = false;

	// Enchantments and flags
	private Map<Enchantment, Integer> enchantments = null;
	private Set<ItemFlag> itemFlags = null;

	// Construction of a new builder
	public ItemStackBuilderUtil() {
	}

	public ItemStackBuilderUtil(Material material) {
		this.material = material;
	}

	// Utility methods for smooth and extensive color parsing
	private static String parseColor(String string) {
		string = parseColorAmp(string);
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	private static String parseColorAmp(String string) {
		string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
		string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
		string = string.replace("&&", "&");
		return string;
	}

	public ItemStackBuilderUtil asMaterial(Material material) {
		this.material = material;
		return this;
	}

	public ItemStackBuilderUtil withAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemStackBuilderUtil withData(short data) {
		this.durability = data;
		return this;
	}

	// In case you are too lazy to cast (if you're using int)
	public ItemStackBuilderUtil withData(int data) {
		return withData((short) data);
	}

	// Meta
	public ItemStackBuilderUtil withLocalizedName(String localizedName) {
		this.localizedName = localizedName;
		return this;
	}

	public ItemStackBuilderUtil withName(String name) {
		this.name = name;
		return this;
	}

	// Multiple ways you can set the lore
	// I prefer #withLore("&1Line 1", "&2Line 2", "&3Etc...")
	public ItemStackBuilderUtil withLore(List<String> lines) {
		this.lore = lines;
		return this;
	}

	public ItemStackBuilderUtil withLore(String... lines) {
		return withLore(Arrays.asList(lines));
	}

	// Just calls ItemMeta#setUnbreakable(true), don't know if compatible with old
	// versions
	public ItemStackBuilderUtil makeUnbreakable() {
		this.unbreakable = true;
		return this;
	}

	// Enchantments
	public ItemStackBuilderUtil withEnchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	public ItemStackBuilderUtil addEnchantment(Enchantment enchantment, int level) {
		// Make sure we have something to add the enchantment to
		if (enchantments == null) {
			this.enchantments = new HashMap<Enchantment, Integer>();
		}

		enchantments.put(enchantment, level);
		return this;
	}

	// Flags
	public ItemStackBuilderUtil withItemFlags(Set<ItemFlag> flags) {
		this.itemFlags = flags;
		return this;
	}

	// Can be used to add only 1 ItemFlag
	// (#withItemFlags(ItemFlag.HIDE_ENCHANTMENTS))
	public ItemStackBuilderUtil withItemFlags(ItemFlag... flags) {
		return withItemFlags(new HashSet<ItemFlag>(Arrays.asList(flags)));
	}

	public SkullBuilder toSkullBuilder() {
		return new SkullBuilder(this);
	}

	/**
	 * Builds the ItemStack with durability from this instance
	 *
	 * @return ItemStack with meta
	 */
	public ItemStack buildStack() {
		// Creating a new ItemStack
		ItemStack itemStack = new ItemStack(material, amount, durability);

		// Getting the stack's meta
		final ItemMeta itemMeta = itemStack.getItemMeta();

		// Meta
		// Set localized name if not null
		if (localizedName != null) {
			itemMeta.setLocalizedName(ItemStackBuilderUtil.parseColor(localizedName));
		}
		// Set displayname if name is not null
		if (name != null) {
			itemMeta.setDisplayName(ItemStackBuilderUtil.parseColor(name));
		}
		// Set lore if it is not null nor empty
		if (lore != null && !lore.isEmpty()) {
			itemMeta.setLore(lore.stream().map(ItemStackBuilderUtil::parseColor).collect(Collectors.toList()));
		}
		// Add enchantments if any
		if (enchantments != null && !enchantments.isEmpty()) {
			// Doing this so I don't have to keep unsafe and safe enchantments separately
			// Ignore all stupid enchantment restrictions ;)
			enchantments.forEach((ench, lvl) -> itemMeta.addEnchant(ench, lvl, true));
		}
		// Add flags if any
		if (itemFlags != null && !itemFlags.isEmpty()) {
			itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[itemFlags.size()]));
		}
		// Deprecated in newer versions, but newer method does not exist in older
		// Only call when unbreakable is true, to refrain from calling as much as
		// possible
		// You could of course always implement your own unbreakable method here
		if (unbreakable)
			itemMeta.spigot().setUnbreakable(true);

		// Set the new ItemMeta
		itemStack.setItemMeta(itemMeta);

		// Lastly, return the stack
		return itemStack;
	}

	/**
	 * A simple builder for a skull with owner
	 * <p>
	 * <b>Note:</b> Uses the ItemStackBuilderUtil builder ;)
	 */
	public class SkullBuilder {

		// Fundamentals
		private ItemStackBuilderUtil stackBuilder;

		// Meta
		private String owner;

		private SkullBuilder(ItemStackBuilderUtil stackBuilder) {
			this.stackBuilder = stackBuilder;
		}

		// Meta
		public SkullBuilder withOwner(String ownerName) {
			this.owner = ownerName;
			return this;
		}

		/**
		 * Builds a skull from a owner
		 *
		 * @return ItemStack skull with owner
		 */
		public ItemStack buildSkull() {
			// Build the stack first, edit to make sure it's a skull
			ItemStack skull = stackBuilder.asMaterial(Material.SKULL_ITEM).withData(3).buildStack();

			// Edit skull meta
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(owner);
			skull.setItemMeta(meta);

			// Lastly, return the skull
			return skull;
		}
	}
}
