package com.camadeusa.utility;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.camadeusa.player.ArchrPlayer;

public class RawMessage {
    private static final Pattern URL_PATTERN = Pattern.compile("((?:http(?:s)?:\\/\\/)?(?:[a-z0-9\\-\\.]+)\\.(?:com|net|org|info|co\\.uk|com\\.au)(?:\\/[a-z0-9.\\-_?=/#]+)?)", 2);
    private final String message;

    private RawMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public RawMessage concatenate(RawMessage other) {
        return new RawMessage(String.format("%s,%s", this.message.substring(0, this.message.length() - 1), other.message.substring(1)));
    }

    public RawMessage concatenate(String other_text) {
        return this.concatenate(RawMessage.builder(other_text).build());
    }

    public boolean send(CommandSender ... senders) {
        boolean success = true;
        for (CommandSender sender : senders) {
            success &= this.run(sender);
        }
        return success;
    }

    public boolean send(ArchrPlayer ... xps) {
        return this.send((CommandSender[])Stream.of(xps).map(ArchrPlayer::getPlayer).toArray(len -> new CommandSender[len]));
    }

    public boolean send(Collection<? extends CommandSender> senders) {
        boolean success = true;
        for (CommandSender sender : senders) {
            success &= this.run(sender);
        }
        return success;
    }

    private boolean run(CommandSender sender) {
        return Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)String.format("tellraw %s %s", sender.getName(), this.getMessage()));
    }

    public static RawMessage of(String text) {
        return new Builder(text).build();
    }

    public static Builder builder(String text) {
        return new Builder(text);
    }

    public static class HoverEventBuilder {
        private final Builder builder;

        private HoverEventBuilder(Builder builder) {
            this.builder = builder;
        }

        public HoverEventBuilder allowBleed() {
            this.builder.stophoverbleed = false;
            return this;
        }

        public Builder withActionText(String text) {
            this.builder.hoverevent = String.format("{action:show_text,value:\"%s\"}", text);
            return this.builder;
        }

        public Builder withActionSimulateItem(int id, int data, String name, String ... lore) {
            String lore_string = "";
            for (String l : lore) {
                lore_string = lore_string + String.format("\\\"%s\\\",", l);
            }
            if (!lore_string.equals("")) {
                lore_string = lore_string.substring(0, lore_string.length() - 1);
                this.builder.hoverevent = String.format("{action:show_item,value:\"{id:%s,Damage:%s,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}}}\"}", id, data, name, lore_string);
            } else {
                this.builder.hoverevent = String.format("{action:show_item,value:\"{id:%s,Damage:%s,tag:{display:{Name:\\\"%s\\\"}}}\"}", id, data, name);
            }
            return this.builder;
        }

        public Builder withActionItem(ItemStack item) {
            List<String> lorelist;
            String name;
            String lore_string = "";
            String enchants = "";
            if (item.getItemMeta() != null) {
                name = item.getItemMeta().getDisplayName();
                lorelist = item.getItemMeta().getLore();
            } else {
                name = null;
                lorelist = null;
            }
            if (lorelist != null) {
                for (String lore : lorelist) {
                    lore_string = lore_string + String.format("\\\"%s\\\",", lore);
                }
            }
            if (item.getEnchantments().size() > 0) {
                enchants = ",ench:[";
                for (Map.Entry enchant : item.getEnchantments().entrySet()) {
                    enchants = enchants + String.format("{id:%s,lvl:%s},", ((Enchantment)enchant.getKey()).getId(), (int)((Integer)enchant.getValue()));
                }
                enchants = enchants.substring(0, enchants.length() - 1);
                enchants = enchants + "]";
            }
            if (!lore_string.equals("")) {
                lore_string = lore_string.substring(0, lore_string.length() - 1);
                Object[] arrobject = new Object[5];
                arrobject[0] = item.getType().getId();
                arrobject[1] = Byte.valueOf(item.getData().getData());
                arrobject[2] = name != null ? name : item.getType().name().toLowerCase();
                arrobject[3] = lore_string;
                arrobject[4] = enchants;
                this.builder.hoverevent = String.format("{action:show_item,value:\"{id:%s,Damage:%s,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}%s}}\"}", arrobject);
            } else {
                Object[] arrobject = new Object[4];
                arrobject[0] = item.getType().getId();
                arrobject[1] = Byte.valueOf(item.getData().getData());
                arrobject[2] = name != null ? name : item.getType().name().toLowerCase();
                arrobject[3] = enchants;
                this.builder.hoverevent = String.format("{action:show_item,value:\"{id:%s,Damage:%s,tag:{display:{Name:\\\"%s\\\"}%s}}\"}", arrobject);
            }
            return this.builder;
        }

        public Builder withActionScoreboardCriterion(String criterion) {
            this.builder.hoverevent = String.format("{action:show_achievement,value:\\\"%s\\\"}", criterion);
            return this.builder;
        }
    }

    public static class ClickEventBuilder {
        private final Builder builder;

        private ClickEventBuilder(Builder builder) {
            this.builder = builder;
        }

        public ClickEventBuilder allowBleed() {
            this.builder.stopclickbleed = false;
            return this;
        }

        public Builder withActionOpenURL(String url) {
            this.builder.clickevent = String.format("{action:open_url,value:\"%s\"}", url);
            return this.builder;
        }

        public Builder withActionRunCommand(String command) {
            this.builder.clickevent = String.format("{action:run_command,value:\"%s\"}", command);
            return this.builder;
        }

        public Builder withActionSuggestCommand(String command) {
            this.builder.clickevent = String.format("{action:suggest_command,value:\"%s\"}", command);
            return this.builder;
        }
    }

    public static class Builder {
        private final Builder parent;
        private String text;
        private Builder child;
        private String color;
        private boolean bold;
        private boolean underline;
        private boolean italic;
        private boolean strikethrough;
        private boolean obfuscate;
        private String insertion;
        private String clickevent;
        private String hoverevent;
        private boolean stopclickbleed;
        private boolean stophoverbleed;

        private Builder(String text) {
            this(null, StringEscapeUtils.escapeJson((String)text), false, false);
        }

        private Builder(Builder parent, String text, boolean initClick, boolean initHover) {
            this.parent = parent;
            this.initDefaults();
            StringBuilder textBuilder = new StringBuilder();
            boolean toChild = false;
            String[] split = text.split(" ");
            for (int i = 0; i < split.length; ++i) {
                String s = split[i];
                if (URL_PATTERN.matcher(s).matches()) {
                    this.withExtra(s).withHoverEvent().withActionText((Object)ChatColor.AQUA + "Click here to open this URL.").withClickEvent().withActionOpenURL(s).withColor(ChatColor.UNDERLINE);
                    this.text = textBuilder.toString();
                    textBuilder.setLength(0);
                    toChild = true;
                    continue;
                }
                textBuilder.append(s);
                if (i >= split.length - 1) continue;
                textBuilder.append(' ');
            }
            if (toChild) {
                this.withExtra(textBuilder.toString());
            }
            if (this.text == null) {
                this.text = textBuilder.toString();
            }
            if (initClick) {
                this.clickevent = "{action:suggest_command,value:\"\"}";
            }
            if (initHover) {
                this.hoverevent = "{action:show_text,value:\"\"}";
            }
        }

        private void initDefaults() {
            this.child = null;
            this.color = null;
            this.bold = false;
            this.underline = false;
            this.italic = false;
            this.strikethrough = false;
            this.obfuscate = false;
            this.insertion = null;
            this.clickevent = null;
            this.hoverevent = null;
            this.stopclickbleed = true;
            this.stophoverbleed = true;
        }

        private String buildJSON() {
            String json = "[{";
            json = json + String.format("text:\"%s\"", this.text);
            if (this.color != null) {
                json = json + String.format(",color:%s", this.color);
            }
            if (this.bold) {
                json = json + ",bold:true";
            }
            if (this.underline) {
                json = json + ",underline:true";
            }
            if (this.italic) {
                json = json + ",italic:true";
            }
            if (this.strikethrough) {
                json = json + ",strikethrough:true";
            }
            if (this.obfuscate) {
                json = json + ",obfuscate:true";
            }
            if (this.insertion != null) {
                json = json + String.format(",insertion:\"%s\"", this.insertion);
            }
            if (this.clickevent != null) {
                json = json + String.format(",clickEvent:%s", this.clickevent);
            }
            if (this.hoverevent != null) {
                json = json + String.format(",hoverEvent:%s", this.hoverevent);
            }
            if (this.child != null) {
                String parent_built = this.child.buildJSON();
                json = json + String.format(",extra:%s", parent_built);
            }
            json = json + "}]";
            return json;
        }

        public Builder withColor(ChatColor ... colors) {
            block7 : for (ChatColor color : colors) {
                if (color.isColor() || color.equals((Object)ChatColor.RESET)) {
                    this.onChild().color = color.name().toLowerCase();
                    continue;
                }
                switch (color) {
                    case BOLD: {
                        this.bold();
                        continue block7;
                    }
                    case UNDERLINE: {
                        this.underline();
                        continue block7;
                    }
                    case ITALIC: {
                        this.italic();
                        continue block7;
                    }
                    case STRIKETHROUGH: {
                        this.strikethrough();
                        continue block7;
                    }
                    case MAGIC: {
                        this.obfuscate();
                        break;
                    }
                }
            }
            return this;
        }

        private void bold() {
            this.onChild().bold = true;
        }

        private void underline() {
            this.onChild().underline = true;
        }

        private void italic() {
            this.onChild().italic = true;
        }

        private void strikethrough() {
            this.onChild().strikethrough = true;
        }

        private void obfuscate() {
            this.onChild().obfuscate = true;
        }

        public Builder withInsertion(String insert_text) {
            this.onChild().insertion = insert_text;
            return this;
        }

        public ClickEventBuilder withClickEvent() {
            return new ClickEventBuilder(this.onChild());
        }

        public HoverEventBuilder withHoverEvent() {
            return new HoverEventBuilder(this.onChild());
        }

        public Builder withExtra(String extra_text) {
            this.child = new Builder(this, extra_text, this.stopclickbleed && this.clickevent != null, this.stophoverbleed && this.hoverevent != null);
            return this.child;
        }

        private Builder onChild() {
            if (this.child != null) {
                return this.child;
            }
            return this;
        }

        public RawMessage build() {
            if (this.parent == null) {
                return new RawMessage(this.buildJSON());
            }
            return this.parent.build();
        }
    }

}

