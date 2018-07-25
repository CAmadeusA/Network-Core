package com.camadeusa.player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.camadeusa.NetworkCore;
import com.camadeusa.module.Module;
import com.camadeusa.utility.Random;
import com.camadeusa.utility.fetcher.UUIDFetcher;
import com.camadeusa.utility.subservers.packet.PacketUpdateDatabaseValue;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.rethinkdb.RethinkDB;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;


// Look into undisguising for specific people or for entire, and if that requires a respawn.

public class GameProfileManager extends Module {
	static String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);

	public static String[] namesList = new String[] {
			"Rodder", "Alien", "Alli", "Almond", "Alpaca", "Angel", "Ant", "Ape", "Apple", "Apricot", "Arrow", "Avo", "Avocado", "Baboon", "Baby", "Badger", "Ball", "Banana", "Bandit", "Bat", "Bear", "Beaver", "Bee", "Beetle", "Buzz", "Bing", "Bong", "Bang", "Berry", "Birdie",
			"Bison", "berry", "Blood", "Blossom", "Blue", "Boar", "Boomer", "Bot", "Boulder", "Boy", "Bronco", "Brown", "Bug", "Bull", "Bullet", "Buster", "Cake", "Calf", "Camel", "Candy", "Captain", "Cat", "Centaur", "Champ", "Charger", "Cheap", "Cherry", "Chick", "Chicken", 
			"Chief", "Chimera", "Chimp", "Chomp", "Clam", "Cobra", "Coco", "Cod", "Coffee", "Colonel", "Colt", "Cookie", "Cow", "Rod", "Crane", "Crow", "Cub", "Custard", "Cyborg", "Cyclops", "Daemon", "Deer", "Demon", "Devil", "Dino", "Diva", "Dog", "Doggy", "John", "James",
			"Aaron", "Heman", "Awww", "Fight", "Bring", "One", "Two", "Three", "Four", "Five", "Chelsea", "Really", "Weird", "Sorry", "Why", "Is", "This", "List", "So", "Big", "Leanne", "Boot", "Foot", "Hand", "Leg", "Jessica", "Event", "Robert", "Will", "Dan", "Kevin", "Anthony",
			"Matthew", "Matt", "Ed", "Jose", "Gary", "Michelle", "Carol", "Karen", "Lisa", "Nancy", "Kim", "Kylie", "Kourtney", "Kendall", "Brian", "Chris", "Jen", "Pat", "Tom", "Mike", "Charlie", "Charles", "Dave", "Ron", "Ronald", "Harry", "Hermy", "XOXO",
			"Dolphin", "Donkey", "Dots", "Dove", "Dragon", "Drake", "Droid", "Drummer", "Duck", "Duke", "Dunker", "Dwarf", "Eagle", "Eel", "Eland", "Elephant", "Elf", "Emu", "Enigma", "Fairy", "Doctor", "Mister", "Falcon", "Fawn", "Fay", "Ferret", "Fiend", "Fig", "Figure", "Fish",
			"Fishy", "Flash", "Flower", "Fly", "Foal", "Fortune", "Fowl", "Fox", "Frog", "Frogger", "Frog", "Fry", "Furry", "Fury", "Gen", "Genie", "Gerbil", "Ghost", "Giant", "Giraffe", "Girl", "Gnoll", "Gnu", "Goat", "Goblin", "God", "Goddess", "Gamer", "Minecraft", "Sponge",
			"Stone", "Iron", "Goldfish", "Goliath", "Goose", "Grape", "Griffin", "Grin", "Guard", "Gull", "Guy", "Hare", "Harpy", "Haunt", "Hawk", "Hazelnut", "Herobrine", "Hedgehog", "Hero", "Salmon", "Herring", "Hippo", "Hobbit", "Hoglet", "Hooper", "Hopper", "Horse", "Hound",
			"Hunter", "Tim", "Timmy", "Tom", "Tongue", "Jan", "Janet", "Cat", "Cath", "Catherine", "Kate", "Katie", "Katy", "Katherine", "Kong", "Hydra", "Hyena", "Ibis", "Idol", "Immortal", "Imp", "Incubus", "Infant", "Jackal", "Jaguar", "Judge", "Juniper", "Kangaroo", "King",
			"Kit", "Kitten", "Kitty", "Kiwi", "Knight", "Koala", "Kraken", "Lady", "Lamb", "Lark", "Larva", "Leaf", "Leech", "Lemon", "Lemur", "Leopard", "Lice", "Lime", "Lion", "Lizard", "Llama", "Lobster", "Locust", "Lord", "Lynx", "Macaw", "Machine", "Mage", "Maggot", "Magpie",
			"Maiden", "Rod", "Rod", "Rod", "Mango", "Mantis", "Maple", "Melon", "Mermaid", "Merman", "Minotaur", "Mite", "Mobster", "Mole", "Nut", "Monk", "Monkey", "Monster", "Moose", "Mouse", "Mule", "Muppet", "Mutant", "Newt", "Ninja", "Nova", "Nugget", "Nymph", "Oak", "Ocelot",
			"Octo", "Ogre", "Okapi", "Olive", "Oracle", "Orange", "Orc", "Ostrich", "Otter", "Owl", "Owlet", "Ox", "Oyster", "Palm", "Panda", "Panther", "Papaya", "Parrot", "Patriot", "Pattern", "Peafowl", "Peanut", "Pear", "Pecan", "Pegasus", "Pelican", "Penguin", "Pepper",
			"Petal", "Phantom", "Phoenix", "Pie", "Pig", "Pigeon", "Piggy", "Piglet", "Pirate", "Pixie", "Plum", "Pony", "Potato", "Priest", "Prince", "Princess", "Prophet", "Prowler", "Pug", "Puggle", "Pumpkin", "Pup", "Puppy", "Pygmy", "Python", "Quail", "Queen", "Rabbit",
			"Raccoon", "Raider", "Ram", "Ranger", "Raptor", "Rascal", "Rat", "Reindeer", "Rhino", "Roach", "Lover", "Hater", "Nope", "Survival", "Game", "FFA", "David", "Steve", "Simon", "Michael", "Liam", "Brogan", "Sophie", "Lewis", "Alex", "Remy", "Golden", "Brown", "Robot", "Rock",
			"Rogue", "Rose", "Rover", "Runner", "Sage", "Sailor", "Saint", "Salmon", "Sardine", "Satyr", "Scorpion", "Craft", "Diamond", "Emerald", "Seahorse", "Seal", "Seer", "Serpent", "Shade", "Shadow", "Shark", "Sheep", "Shifter", "Siren", "Rod", "Skunk", "Slider", "Sling", "Shot",
			"Rod", "Sloth", "Smile", "Smirk", "Snail", "Snake", "Snowman", "Soldier", "Soul", "Sparkle", "Sparks", "Sparrow", "Specter", "Sphinx", "Spider", "Spike", "Spirit", "Spook", "Sprite", "Squab", "Squid", "Squire", "ray", "Coal", "Dirt", "Fight", "Rod",
			"Sword", "Iron", "Gapple", "block", "dude", "fast", "boy", "sirius", "Storm", "Stripes", "Swallow", "Swan", "Tea", "Techy", "Termite", "Terror", "Thief", "Thunder", "Tiger", "Titan", "Toad", "Toffee", "Tomato", "Tortoise", "Toucan", "Troll", "Rod", "Trout", "Tucan", "Tulip",
			"Turkey", "Turtle", "Twin", "Twinkle", "Toe", "Finger", "Thumb", "Unicorn", "Urchin", "Valkyrie", "Vampire", "Vanilla", "Viking", "Villain", "Viper", "Void", "Vulture", "Walker", "Wallaby", "Walrus", "Warhog", "Warrior", "Warthog", "Wasp", "Weasel",
			"Werewolf", "Whale", "Whelp", "Willow", "Witch", "Wizard", "Wolf", "Wombat", "Worm", "Wraith", "Wrecker", "Yak", "Yeti", "Yew", "Zebra", "Zombie", "Cube", "Digger", "Wheat", "Pickaxe", "Shovel", "Mobman", "Snowman", "Santa" 
	};
	public static ArrayList<JSONObject> skinList = new ArrayList<JSONObject>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage("");
		NetworkPlayer aP = NetworkPlayer.getNetworkPlayerByUUID(event.getPlayer().getUniqueId().toString());

		// Getting craftplayer
		Class<?> craftPlayer = null;
		try {
			craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Invoking method getHandle() for the player
		Object handle = null;
		try {
			handle = craftPlayer.getMethod("getHandle").invoke(event.getPlayer());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Field ff = null;
			for (Field f : handle.getClass().getSuperclass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.getType() == (GameProfile.class)) {
					ff = f;
					break;
				}
			}
			if (ff != null) {
				ff.setAccessible(true);
				GameProfile gp = (GameProfile) ff.get(handle);
				JSONObject skin = new JSONObject();
				for (Property p : gp.getProperties().get("textures")) {
					if (p.getName().equalsIgnoreCase("textures")) {
						skin.put("value", p.getValue());
						skin.put("signature", p.getSignature());
						break;
					}
				}
				SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(event.getPlayer().getUniqueId().toString(), "skin", skin.toString()));

			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		disguise(aP);

	}
	
	public static void disguise(NetworkPlayer np) {
		String name = generateName();
		if (skinList.size() > 3) {
			JSONObject skin = skinList.get(Random.instance().nextInt(skinList.size() - 1));
			setGameProfile(np, name, skin.getString("value"), skin.getString("signature"));
		} else {
			setGameProfile(np, name,
					"eyJ0aW1lc3RhbXAiOjE1MTc3OTQ0NzM4ODksInByb2ZpbGVJZCI6IjYyMjQ0YTVlMzNkZDQzZGQ5NWNiODEwMmYyNzM5MTFhIiwicHJvZmlsZU5hbWUiOiJEemJzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MmE2MzJkNmE4MGM1Y2M1NjI5OWYyMGM5MTIzYjYyYTFmOGNjNjJiNjU1ZGZhOGI5ZmFiMmI3NmQ2MDZhIn0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YzNjYTdlZTJhNDk4ZjFiNWQyNThkNWZhOTI3ZTYzZTQzMzE0M2FkZDU1MzhjZjYzYjZhOWI3OGFlNzM1In19fQ==",
					"K5ESEcZB92e2d7rgJZx6ReNrCZsHanZD4bzRK88SqX4JWf3fmoEIoXpog1aUKj38FSdFknc109gBIq49fa028D590IzEHVX/r8kIKtLfVmjvXvKBGnL423ftpMynUNxoyfIp7oPoq2GO5u+relvYE0fxVsqprOqghVReAGJbkJ4TyL42BBIRjFYGtkUE8UNlmoc5uqR/7mSwyUSBUeX+PJSdXWNIEtxax4SJr2Uqny20rcMzFFewhnzwsDRqvSZfg75npeP9+QKtT3kKK6u/gwQ4hxYcSzr5j2UWGP+aJfTmrniI5VEMBbQ/HdZFzWqCPrdHKo0wBf7O7gFKtpndGO96F55mE3WzPu8r3PhOmesq9cs/G4seH6ZuJYho5qqYNLpjVrLK98VGZpstAzF3FaS/tv/IEljwJJyNwS8qrtg0hjes/jU0u0v0JrcdADw7GYXZ/ap0lWWhtNPSbGluVsDUILUO53J4K+KC1bi6R298xQWFXqWnTln1PwibnpsntszuxoTwDc9+UttHYMnuXxDCohPWBu7hDjoWcOQiB8h4oKzm1n8rSXn+kxFqmyq1TqlnODvES0PKVjtljy5Ix0hXuhJiSe5yd2bnVpR5pKvjxX2qiuomJCD0aSmz2PBN5lgpS55r+P/Pwl3Cpnc92JISpamBvo4qhCO0CHCxK1A=");
		}
	}
	
	public static void gatherRandomSkins() {
		String content = RethinkDB.r.table("playerdata").getField("skin").sample(20).toJson().run(NetworkCore.getInstance().getCon());
		JSONArray skins = new JSONArray(content);
		for (int i = 0; i < skins.length(); i++) {
			skinList.add(new JSONObject(skins.getString(i)));
		}
	}
	
	public static String generateName() {
		String name = "", prefix = "", word1, word2, suffix = "";
		boolean word2bool = false;
		Random random = Random.instance();

		int startingRandom = random.nextInt(50);

		if (startingRandom <= 7) {
			prefix = "xX";
			suffix = "Xx";
		} else if (startingRandom > 7 && startingRandom <= 11) {
			prefix = "_";
			suffix = "_";
		} else if (startingRandom > 11 && startingRandom <= 15) {
			prefix = "Mr";
			suffix = (random.nextBoolean()) ? "" : "Boi";
		} else if (startingRandom > 15 && startingRandom <= 19) {
			prefix = (random.nextBoolean()) ? "Dr" : ((random.nextBoolean()) ? "BB" : "");
			suffix = (random.nextBoolean()) ? "Dude" : "";
		} else if (startingRandom > 19 && startingRandom <= 23) {
			prefix = (random.nextBoolean()) ? "" : "Hey";
		} else if (startingRandom > 23 && startingRandom <= 27) {
			prefix = (random.nextBoolean()) ? "Its" : "";
		} else if (startingRandom > 23 && startingRandom <= 27) {
			prefix = (random.nextBoolean()) ? "" : "Oh";
			suffix = (random.nextBoolean()) ? "Dude" : "";
		}

		if (prefix.isEmpty()) {
			if (random.nextBoolean())
				if (random.nextBoolean()) {
					prefix = "x";
				} else {
					prefix = random.nextBoolean() ? "xI" : ((random.nextBoolean()) ? "_" : "");
				}
		}

		if (suffix.isEmpty()) {
			if (random.nextBoolean())
				if (random.nextBoolean()) {
					suffix = random.nextBoolean() ? "x" : "";
				} else {
					suffix = random.nextBoolean() ? ""
							: ((random.nextBoolean()) ? "Ix" : ((random.nextBoolean()) ? "X" : "x"));
				}
		}

		word1 = random.nextBoolean() ? namesList[random.nextInt(namesList.length)]
				: namesList[random.nextInt(namesList.length)];

		if (!random.nextBoolean())
			word1 = word1.toLowerCase();

		if ((prefix + word1 + suffix).length() > 13) {
			name = prefix + word1 + suffix;
		} else if ((prefix + word1 + suffix).length() <= 7) {
			word2bool = true;
			if (random.nextBoolean()) {
				word1 = (random.nextBoolean() ? random.nextInt(28) : "x") + word1
						+ (random.nextBoolean() ? "_" : random.nextInt(28));
			}
		} else {
			if (random.nextBoolean()) {
				word2bool = true;
				if (random.nextBoolean())
					word1 += random.nextInt(23);
			} else {
				name = prefix + word1 + suffix;
			}
		}

		/* Second word */
		if (word2bool) {
			word2 = random.nextBoolean() ? namesList[random.nextInt(namesList.length)]
					: namesList[random.nextInt(namesList.length)];
			int lowerUpper = random.nextInt(7);
			if (lowerUpper <= 2)
				word2 = word2.toLowerCase();
			if (lowerUpper > 3 && lowerUpper <= 5)
				word2 = word2.toUpperCase();

			if ((prefix + word1 + word2 + suffix).length() > 13) {
				if (random.nextBoolean())
					name = (random.nextBoolean() ? suffix : prefix) + word1 + random.nextInt(9);
				else
					name = random.nextInt(9)
							+ (random.nextBoolean() ? word2
									: (random.nextBoolean() ? word1 : namesList[random.nextInt(namesList.length)]))
							+ suffix;
			} else {
				name = prefix + word1 + word2 + suffix;
			}
		}

		return name;
	}
	
	public static void clearDisguise(NetworkPlayer np) {
		JSONObject skin = fetchSkinBlobs(np.getUUID());
		setGameProfile(np, np.getOriginalPlayerName(), skin.getString("value"), skin.getString("signature"));
	}

	public static void setSkinOnly(NetworkPlayer np, String name) {
		setSkinOnly(np, UUIDFetcher.getUUID(name));
	}

	public static void setSkinOnly(NetworkPlayer np, UUID uuid) {
		JSONObject skin = fetchSkinBlobs(uuid);
		setGameProfile(np, null, skin.getString("value"), skin.getString("signature"));
	}
	
	public static void setNameOnly(NetworkPlayer np, String name) {
		setGameProfile(np, name, null, null);
	}
	
	public static void setGameProfile(NetworkPlayer np, String name, String textures, String signature) {
		String bukkitversion = Bukkit.getServer().getClass().getPackage()
                .getName().substring(23);
        // Getting craftplayer
        Class<?> craftPlayer = null;
		try {
			craftPlayer = Class.forName("org.bukkit.craftbukkit."
			        + bukkitversion + ".entity.CraftPlayer");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        // Invoking method getHandle() for the player
        Object handle = null;
		try {
			handle = craftPlayer.getMethod("getHandle").invoke(np.getPlayer());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			Field ff = null;
			for (Field f : handle.getClass().getSuperclass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.getType() == (GameProfile.class)) {
					ff = f;
					break;					
				}
			}
			if (ff != null) {
				
				GameProfile currentGp = (GameProfile) ff.get(handle);
				JSONObject skin = new JSONObject();
				for (Property p : currentGp.getProperties().get("textures")) {
					if (p.getName().equalsIgnoreCase("textures")) {
						skin.put("value", p.getValue());
						skin.put("signature", p.getSignature());
						break;
					}
				}
				
				ff.setAccessible(true);
				GameProfile gp = new GameProfile(UUID.fromString(np.getPlayer().getUniqueId().toString()), !name.equals(null) ? name:currentGp.getName());
				// get the following from somewhere and put textures
				gp.getProperties().put("textures", new Property("textures", !textures.equals(null) ? textures:skin.getString("value"), !signature.equals(null) ? signature:skin.getString("signature")));
				ff.set(handle, gp);				
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static JSONObject fetchSkinBlobs(UUID uuid) {
		JSONObject skin = new JSONObject();
		NetworkPlayer np = NetworkPlayer.getNetworkPlayerByUUID(uuid.toString());
		if (np != null && np.getData().has("skin")) {
			skin = new JSONObject(np.getData().getString("skin"));
		} else if (Bukkit.getOfflinePlayer(uuid) != null) {
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			// Getting craftplayer
			Class<?> craftPlayer = null;
			try {
				craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Invoking method getHandle() for the player
			Object handle = null;
			try {
				handle = craftPlayer.getMethod("getHandle").invoke(op);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Field ff = null;
				for (Field f : handle.getClass().getSuperclass().getDeclaredFields()) {
					f.setAccessible(true);
					if (f.getType() == (GameProfile.class)) {
						ff = f;
						break;
					}
				}
				if (ff != null) {
					ff.setAccessible(true);
					GameProfile gp = (GameProfile) ff.get(handle);
					for (Property p : gp.getProperties().get("textures")) {
						if (p.getName().equalsIgnoreCase("textures")) {
							skin.put("value", p.getValue());
							skin.put("signature", p.getSignature());
							break;
						}
					}
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ((boolean) RethinkDB.r.table("playerdata").contains(uuid).run(NetworkCore.getInstance().getCon()) && (new JSONObject(((String) RethinkDB.r.table("playerdata").get(uuid).toJson().run(NetworkCore.getInstance().getCon()))).has("skin"))) {
			skin = new JSONObject(new JSONObject(((String) RethinkDB.r.table("playerdata").get(uuid).toJson().run(NetworkCore.getInstance().getCon()))).getString("skin"));
		} else {
			try {
			// Fetch from external source.
				//MineTools.eu
					String content = IOUtils.toString(new URL("https://api.minetools.eu/profile/" + uuid.toString().replace("-", "")), Charset.forName("UTF-8"));
					if (!content.toLowerCase().contains("invalid uuid")) {
						JSONObject minetools = new JSONObject(content);
						for (int i = 0; i < minetools.getJSONObject("raw").getJSONArray("properties").length(); i++) {
							if (minetools.getJSONObject("raw").getJSONArray("properties").getJSONObject(i).getString("name").equals("textures")) {
								skin.put("value", minetools.getJSONObject("raw").getJSONArray("properties").getJSONObject(i).getString("value"));
								skin.put("signature", minetools.getJSONObject("raw").getJSONArray("properties").getJSONObject(i).getString("signature"));
								skin.put("name", minetools.getJSONObject("raw").getString("name"));
								break;
							}
						}
						
						// Log with saving to database value
						SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(uuid.toString(), "skin", skin.toString()));
						SubAPI.getInstance().getSubDataNetwork().sendPacket(new PacketUpdateDatabaseValue(uuid.toString(), "name", skin.getString("name")));
						
					}
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return skin;
	}

}
