/*
 * Decompiled with CFR 0_122.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  io.xime.common.player.Rank
 *  org.bukkit.ChatColor
 */
package com.camadeusa.utility;

import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TextUtil {
    private static final char COLOR_CHAR = '\u00a7';
    private static final Pattern chatSplitPattern = Pattern.compile(" ");
    private static final Pattern chatColorPattern = Pattern.compile("&([A-FK-OR0-9])", 2);
    private static final Pattern chatExcessiveLetters = Pattern.compile("(.)\\1{4,}", 2);
    private static final Pattern excessivelyBlockedWords = Pattern.compile("(f[uvwy]ck|sh[li1][t7]|c[uvwy][mn][t7]|[bd][li1][t7]ch|[mn][li1]gg[[a4][e3][li1][o0][uvwy]]|[pq][e3][mn][li1]s|[uvwy][a4]g[li1][mn][a4]|f[a4]g|[pq][uvwy]ss[uvwy]|c[o0]ck|[pq][o0]r[mn]|[bd][li1]ck|[bd][a4]s[t7][a4]r[bd]|[uvwy]h[o0]r[e3]|[pq][pq]r[o0]s[t7][li1][t7][uvwy][t7][e3]|k[mn][uvwy][li1][li1][a4]|s[li1][uvwy][mn][a4]|[mn][e3]g[e3]r|k[uvwy]k|s[mn][o0][pq][pq]|f[li1][t7][t7][a4]|[pq][o0]rr|h[o0]r[uvwy][mn]g[e3]|s[li1][a4][mn][pq][a4]|[pq]r[o0]s[t7][li1][t7][uvwy][e3]r[a4][bd]|j[o0][bd][e3]r|[mn][li1][e3]r[bd][a4]|c[o0]\u00f1[o0]|[pq][e3]rr[a4]|[mn][a4]r[li1]c\u00f3[mn]|c[o0][mn]ch[a4]|[uvwy][e3]rg[a4]|[bd][a4]s[t7][a4]r[bd][o0]|[pq][uvwy][t7][a4]|[pq]r[o0]s[t7][li1][t7][uvwy][t7][a4]|f[o0][uvwy][t7]r[e3]|[mn][e3]r[bd][e3]|ch[li1][e3][mn][mn][e3]|[mn][o0][li1]r|[pq]\u00e9[mn][li1]s|[uvwy][a4]g[li1][mn]|[t7][a4][pq][e3][t7][t7][e3]|f[o0][uvwy]f[o0][uvwy][mn][e3]|[bd]\u00e2[t7][a4]r[bd]|s[a4][li1][o0][pq][e3]|[pq]r[o0]s[t7][li1][t7][uvwy]\u00e9[e3])(([A-Z]+)?)", 2);
    private static final Pattern chatBlockedWordsPartial = Pattern.compile("([A-Z0-9]+)?(fuck|shit|cunt|bitch|nigg[aeiou]|pa?enis|vagina|fag|pussy|cock|porn|dick|bastard|whore|prostitute|knulla|slyna|neger|kuk|snopp|fitta|porr|horunge|slampa|prostituerad|joder|mierda|co\u00f1o|perra|maric\u00f3n|concha|verga|bastardo|puta|prostituta|foutre|merde|chienne|noir|p\u00e9nis|vagin|tapette|foufoune|b\u00e2tard|salope|prostitu\u00e9e)([A-Z0-9]+)?", 2);
    private static final Pattern chatBlockedWordsExact = Pattern.compile("\\b(kys|am\u0131|am\u0131na|am\u0131n\u0131|amc\u0131k|g\u00f6t|orospu|o\u00e7|o\\.\u00e7\\.|pi\u00e7|siktir|sikiyim|sikerim|siktim|sikiyorum|s\u0131\u00e7|yarrak|yarra\u011f\u0131|yarak|yara\u011f\u0131|pezevenk|ibne|amk|am\u0131na koyar\u0131m|orosbu|pezevenk|pi\u00e7|sikmek|sikim|siksin|siker|yarram|yarra\u011f\u0131m|yara\u011f\u0131m|yarra\u011f\u0131m\u0131|yara\u011f\u0131m\u0131|yarram\u0131|anan|anan\u0131|amk|amq|yav\u015fak|yavsak)\\b", 2);
    private static final Pattern chatAvoidCharacters = Pattern.compile("( |\\.|\\\\|\\-|\\=|\\`|\\/|\\||\\?|\\<|\\,|\\>|\\!|\\@|\\#|\\$|\\%|\\^|\\\u00a7([a-f0-9A-F])|\\&|\\*|\\+|\\_|\\:|\\;|\\'|\\\")", 2);
    private static final Pattern chatBlockRestrict = Pattern.compile("(?:[4h][!1i]v[3e]|r[e3]b[e3][l!1][l!1][!1i][o0]n|b[4a]d ?[lI][i1][o0]n|gch[e3][a4]t|judge?me?nt ?day|cub[e3] ?cr[a4]ft|[o0]l[i1]mp[o0] ?cr[a4]ft|k[o0]h[i1!]|n[e3]xu[s5]|z[!1i]nn[!1i]a|ebola|change\\.org|br[!i1]ngb[a@]ckv[!1l]|mojewcapes|(mc)?(the)? ?fr[!1i]dg[3e]|hyp[!l1i](?:ck|x)(?:l[3e]|[3e]l)|c[0o]rrupt|m[!l1i]n[3e][4a][!l1i]t[s5]|m[!l1i]n[3ei]p[!l1i][3eo]x|mojang\\.link|#View#Bots#Rock|ibeat|#unban|#bringback|#wewant|#pardon|mc(?:sg|g|g.m.r) ?is ?d(?:ying|ieing|ding|ead|ed))", 2);
    private static final Pattern chatBlockAddress = Pattern.compile("(?:(?:[A-Za-z0-9]+\\.)+(?:[A-Za-z\\-]{2,9})(?:\\:[0-9]{1,5})?|[0-9]{1,3}[., ]{1,3}[0-9]{1,3}[., ]{1,3}[0-9]{1,3}[., ]{1,3}[0-9]{1,3}(?:\\:[0-9]{1,5}))", 2);
    private static final Pattern chatTrashTalk = Pattern.compile("(gg[1lI][0o]|[3e]zgg|( |^)[3e]z( |$)|gg[3e]z|g[3e]t g[0o][0o]d|fgt|r[3e]kt|l2p|[3e]zpz|2ez)(([A-Z]+)?)", 2);
    private static final Pattern emailPattern = Pattern.compile("[a-z0-9!#$%&\\'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\\'*+\\/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
    private static final ChatColor[] rainbowOrder = new ChatColor[]{ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.BLUE, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE};
    private static final BiMap<Character, Character> chatColorReplacements = HashBiMap.create();
    private static final BiMap<Character, Character> chatColorReplacementsInverse;

    public static String editBlockedWords(String string) {
        if (string == null) {
            return "";
        }
        String newString = TextUtil.replaceCharacters(string, false);
        newString = chatBlockedWordsPartial.matcher(newString).replaceAll("#@%&");
        newString = chatBlockedWordsExact.matcher(newString).replaceAll("#@%&");
        return TextUtil.replaceCharacters(newString, true);
    }

    private static String replaceCharacters(String string, boolean inverse) {
        char[] chars = string.toCharArray();
        boolean handleNext = false;
        for (int i = 0; i < chars.length; ++i) {
            if (handleNext) {
                chars[i] = !inverse ? ((Character)chatColorReplacements.get((Object)Character.valueOf(chars[i]))).charValue() : ((Character)chatColorReplacementsInverse.get((Object)Character.valueOf(chars[i]))).charValue();
            }
            handleNext = chars[i] == '\u00a7';
        }
        return new String(chars);
    }

    public static boolean containsExcessiveLetters(String string) {
        return chatExcessiveLetters.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).find();
    }

    public static boolean containsRestrictedWord(String string) {
        return chatBlockRestrict.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).find();
    }

    public static boolean containsAddress(String string) {
        return chatBlockAddress.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).find();
    }

    public static boolean containsFilterAvoid(String string) {
        String avoidCharactersRemoved = chatAvoidCharacters.matcher(string).replaceAll("");
        return chatBlockRestrict.matcher(avoidCharactersRemoved).find() || chatBlockedWordsPartial.matcher(avoidCharactersRemoved).find() || chatBlockedWordsExact.matcher(avoidCharactersRemoved).find();
    }

    public static boolean containsExcessivelyBlockedWords(String string) {
        return excessivelyBlockedWords.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).find();
    }

    public static boolean containsBlockedWords(String string) {
        return chatBlockedWordsPartial.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).find();
    }

    public static boolean containsTrashTalk(String string) {
        String avoidCharactersRemoved = chatAvoidCharacters.matcher(Normalizer.normalize(string, Normalizer.Form.NFD)).replaceAll("");
        return chatTrashTalk.matcher(avoidCharactersRemoved).find();
    }

    public static String editCaps(String string) {
        if (string.length() < 4) {
            return string;
        }
        int caps = 0;
        for (int i = 0; i < string.length(); ++i) {
            if (!Character.isUpperCase(string.charAt(i))) continue;
            ++caps;
        }
        if ((double)caps / (double)string.length() >= 0.3) {
            return string.toLowerCase();
        }
        return string;
    }

    public static String toMinecraftColorCodes(String string) {
        if (string == null) {
            return "";
        }
        String newstring = string;
        newstring = chatColorPattern.matcher(newstring).replaceAll("\u00a7$1");
        return newstring;
    }

    public static String toBoldRainbow(String string) {
        String newstring = "";
        for (int i = 0; i < string.length(); ++i) {
            newstring = newstring + (Object)rainbowOrder[i % rainbowOrder.length] + "" + (Object)ChatColor.BOLD + string.charAt(i);
        }
        return newstring + (Object)ChatColor.RESET;
    }

    public static String toRainbow(String string) {
        String newstring = "";
        for (int i = 0; i < string.length(); ++i) {
            newstring = newstring + rainbowOrder[i % rainbowOrder.length].toString() + string.charAt(i);
        }
        return newstring;
    }

    public static String toHalfBoldRainbow(String string, String secondHalfPrefix) {
        int i;
        String newString = "";
        int half = string.length() / 2;
        for (i = 0; i < half; ++i) {
            newString = newString + (Object)rainbowOrder[i % rainbowOrder.length] + "" + (Object)ChatColor.BOLD + string.charAt(i);
        }
        newString = newString + (Object)ChatColor.RESET;
        newString = newString + secondHalfPrefix;
        for (i = half; i < string.length(); ++i) {
            newString = newString + string.charAt(i);
        }
        return newString;
    }

    public static String toRomanNumeral(int number) {
        if (number < 1 || number >= 4000) {
            return "";
        }
        String[] roman = new String[]{"M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int[] arabic = new int[]{1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (number > 0 || arabic.length == i - 1) {
            while (number - arabic[i] >= 0) {
                number -= arabic[i];
                result.append(roman[i]);
            }
            ++i;
        }
        return result.toString();
    }

    public static String toBrackets(long number, ChatColor color) {
        return TextUtil.toBrackets("" + number, color);
    }

    public static String toBrackets(int number, ChatColor color) {
        return TextUtil.toBrackets("" + number, color);
    }

    public static String toBrackets(String string, ChatColor color) {
        return (Object)ChatColor.DARK_GRAY + "[" + (Object)color + string + (Object)ChatColor.DARK_GRAY + "]";
    }

    public static String toBrackets(String string, String color) {
        return (Object)ChatColor.DARK_GRAY + "[" + color + string + (Object)ChatColor.DARK_GRAY + "]";
    }

    public static String toTime(int seconds, ChatColor inside, ChatColor outside) {
        return TextUtil.toTime(seconds, inside, outside, false);
    }

    public static String toTime(int seconds, ChatColor inside, ChatColor outside, boolean small) {
        float time = seconds;
        String unit = (small ? "" : " ") + (Object)outside;
        unit = time > 2419200.0f ? unit + (small ? "m" : new StringBuilder().append("month").append((time /= 2419200.0f) != 1.0f ? "s" : "").toString()) : (time > 604800.0f ? unit + (small ? "w" : new StringBuilder().append("week").append((time /= 604800.0f) != 1.0f ? "s" : "").toString()) : (time > 86400.0f ? unit + (small ? "d" : new StringBuilder().append("day").append((time /= 86400.0f) != 1.0f ? "s" : "").toString()) : (time > 3600.0f ? unit + (small ? "h" : new StringBuilder().append("hour").append((time /= 3600.0f) != 1.0f ? "s" : "").toString()) : (time > 60.0f ? unit + (small ? "m" : new StringBuilder().append("minute").append((time /= 60.0f) != 1.0f ? "s" : "").toString()) : unit + (small ? "s" : new StringBuilder().append("second").append(time != 1.0f ? "s" : "").toString())))));
        String timeText = String.format("%.1f", Float.valueOf(time));
        if (timeText.endsWith(".0")) {
            timeText = timeText.substring(0, timeText.length() - 2);
        }
        return (small ? new StringBuilder().append((Object)inside).append("").append(time).toString() : TextUtil.toBrackets(timeText, inside)) + unit;
    }

    public static String toMinuteAndSeconds(int seconds) {
        int m = (int)Math.floor((double)seconds / 60.0);
        int s = seconds - m * 60;
        return String.format("%d:%02d", m, s);
    }

    public static String toProperTime(int seconds) {
        int d = (int)TimeUnit.SECONDS.toDays(seconds);
        int w = (int)Math.floor(d / 7);
        long h = TimeUnit.SECONDS.toHours(seconds) - (long)(d * 24);
        long m = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toHours(seconds) * 60;
        long s = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toMinutes(seconds) * 60;
        d -= w * 7;
        String result = "";
        if (w > 0) {
            result = result + w + "w";
        }
        if (d > 0) {
            result = result + d + "d";
        }
        if (h > 0) {
            result = result + h + "h";
        }
        if (m > 0) {
            result = result + m + "m";
        }
        result = result + s + "s";
        return result;
    }

    public static /* varargs */ String combineLines(String ... text) {
        String ret = "";
        if (text == null) {
            return ret;
        }
        for (int i = 0; i < text.length; ++i) {
            ret = ret + (text[i] == null ? "" : text[i]);
            if (i >= text.length - 1) continue;
            ret = ret + "\n";
        }
        return ret;
    }

    public static /* varargs */ String merge(String separator, String ... args) {
        if (args == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null) continue;
            sb.append(args[i]);
            if (i >= args.length - 1) continue;
            sb.append(separator);
        }
        String str = sb.toString();
        return str;
    }

    public static String merge(String[] arguments) {
        String ret = "";
        for (String arg : arguments) {
            ret = ret + arg + " ";
        }
        return ret.trim();
    }

    public static String merge(Object[] arguments) {
        String ret = "";
        for (Object arg : arguments) {
            ret = ret + arg.toString() + " ";
        }
        return ret.trim();
    }

    public static String merge(String[] arguments, int startIndex) {
        String ret = "";
        for (int i = startIndex; i < arguments.length; ++i) {
            ret = ret + arguments[i] + " ";
        }
        return ret.trim();
    }

    public static String merge(Object[] arguments, int startIndex) {
        String ret = "";
        for (int i = startIndex; i < arguments.length; ++i) {
            ret = ret + arguments[i].toString() + " ";
        }
        return ret.trim();
    }

    public static List<String> splitLine(String line, int length) {
        return TextUtil.splitLine(line, length, "");
    }

    public static List<String> splitLine(String line, int length, String append) {
        ArrayList<String> lines = new ArrayList<String>();
        if (length >= line.length()) {
            lines.add(append + line);
            return lines;
        }
        while (line.length() > 0) {
            if (line.length() >= length) {
                String l = line.substring(0, length);
                l = l.substring(0, l.lastIndexOf(" ") + 1);
                lines.add(append + l);
                line = line.substring(l.lastIndexOf(" ") + 1);
                continue;
            }
            lines.add(append + line);
            break;
        }
        return lines;
    }

    public static /* varargs */ boolean matchesAnyOf(String input, String ... tests) {
        for (String test : tests) {
            if (test == null || !test.equalsIgnoreCase(input)) continue;
            return true;
        }
        return false;
    }

    public static String safeSubstring(String msg, int beginIndex) {
        return TextUtil.safeSubstring(msg, beginIndex, msg.length());
    }

    public static String safeSubstring(String msg, int beginIndex, int endIndex) {
        if (beginIndex > endIndex) {
            return "";
        }
        if (beginIndex > msg.length()) {
            return "";
        }
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex > msg.length()) {
            endIndex = msg.length();
        }
        return msg.substring(beginIndex, endIndex);
    }

    public static String saturateChatColors(String message) {
        String[] words = chatSplitPattern.split(message);
        String newMsg = "";
        for (int i = 0; i < words.length; ++i) {
            if (i > 0) {
                newMsg = newMsg + ChatColor.getLastColors((String)newMsg);
            }
            newMsg = newMsg + words[i] + " ";
        }
        if (newMsg.length() > 0) {
            newMsg = newMsg.substring(0, newMsg.length() - 1);
        }
        return newMsg;
    }

    public static String toString(UUID uuid, boolean insertColorChar) {
        StringBuilder result = new StringBuilder(8);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        buffer.flip();
        while (buffer.hasRemaining()) {
            if (insertColorChar) {
                result.append('\u00a7');
            }
            result.append((char)buffer.getShort());
        }
        return result.toString();
    }

    public static String compress256(String string, boolean insertColorChar) {
        StringBuilder result = new StringBuilder((int)Math.ceil((double)string.length() / 2.0) * (insertColorChar ? 2 : 1));
        ByteBuffer buffer = ByteBuffer.allocate(string.length() + (string.length() % 2 == 1 ? 1 : 0));
        for (char character : string.toCharArray()) {
            buffer.put((byte)(character & 255));
        }
        if (string.length() % 2 == 1) {
            buffer.put((byte) 0);
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            if (insertColorChar) {
                result.append('\u00a7');
            }
            result.append((char)buffer.getShort());
        }
        return result.toString();
    }

    public static String decompress256(String string, boolean hasColorChar) {
        int size = hasColorChar ? string.length() : string.length() * 2;
        StringBuilder result = new StringBuilder(size);
        ByteBuffer buffer = ByteBuffer.allocate(size * 2);
        for (int i = 0; i < size / 2; ++i) {
            buffer.putChar(string.charAt(hasColorChar ? i * 2 + 1 : i));
        }
        buffer.flip();
        byte segment = 0;
        while (buffer.hasRemaining()) {
            segment = buffer.get();
            if (segment == 0) continue;
            result.append((char)segment);
        }
        return result.toString();
    }

    public static int getLevenshteinDistance(String s, String t) {
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        int i = 0;
        while (i <= n) {
            p[i] = i++;
        }
        for (int j = 1; j <= m; ++j) {
            char t_j = t.charAt(j - 1);
            d[0] = j;
            for (i = 1; i <= n; ++i) {
                int cost = s.charAt(i - 1) == t_j ? 0 : 1;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }
            int[] _d = p;
            p = d;
            d = _d;
        }
        return p[n];
    }

    public static String getRandomString(int length, boolean insertColorChar) {
        StringBuilder result = new StringBuilder(length);
        Random random = new Random();
        byte[] bytes = new byte[2];
        for (int i = 0; i < length; ++i) {
            if (insertColorChar) {
                result.append('\u00a7');
            }
            random.nextBytes(bytes);
            result.append((char)(bytes[0] << 8 & 65280 | bytes[1] & 255));
        }
        return result.toString();
    }

    public static boolean canAppend(String origin, String append, int limit) {
        return origin.length() + append.length() <= limit;
    }

    public static String toTitleCase(String input) {
        switch (input.length()) {
            case 0: {
                return input;
            }
            case 1: {
                return input.toUpperCase();
            }
        }
        StringBuilder output = new StringBuilder();
        output.append(Character.toUpperCase(input.charAt(0)));
        output.append(input.substring(1).toLowerCase());
        return output.toString();
    }

    public static <T> String arrayToString(T[] array) {
        return TextUtil.arrayToString(array, Object::toString, true);
    }

    public static <T> String arrayToString(T[] array, Function<T, String> tostring) {
        return TextUtil.arrayToString(array, tostring, true);
    }

    public static <T> String arrayToString(T[] array, boolean brackets) {
        return TextUtil.arrayToString(array, Object::toString, brackets);
    }

    public static <T> String arrayToString(T[] array, Function<T, String> tostring, boolean brackets) {
        String str = "";
        for (T element : array) {
            str = str + tostring.apply(element) + " ";
        }
        if (str.length() > 1) {
            str = str.substring(0, str.length() - 1);
        }
        return brackets ? "[" + str + "]" : str;
    }

    public static boolean isValidInt(String word) {
        try {
            Integer.valueOf(word);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        return emailPattern.matcher(email).matches();
    }

    public static String worldTimeToString(int ticks) {
        if ((ticks %= 24000) >= 0 && ticks < 5800 || ticks >= 6200 && ticks < 12000) {
            return "Day";
        }
        if (ticks >= 5800 && ticks < 6200) {
            return "Noon";
        }
        if (ticks >= 12000 && ticks < 13800) {
            return "Dusk";
        }
        if (ticks >= 13800 && ticks < 17800 || ticks >= 18200 && ticks < 22200) {
            return "Night";
        }
        if (ticks >= 17800 && ticks < 18200) {
            return "Midnight";
        }
        if (ticks >= 22200 && ticks < 24000) {
            return "Dawn";
        }
        return "Unknown";
    }

    public static String boolSwitch(boolean value) {
        return value ? "Enabled" : "Disabled";
    }
    
    public static List<String> getSubStrings(String text, int n) {
        ArrayList<String> substrings = new ArrayList<String>();
        int lastsubstringpos = 0;
        for (int i = 0; i < text.length(); i++) {
            if (i % n == 0) {
                substrings.add(text.substring(lastsubstringpos, i));
                lastsubstringpos = i;
            }
        }
        substrings.add(text.substring(lastsubstringpos, text.length()));
        return substrings;
    }

    static {
        chatColorReplacements.put(Character.valueOf('0'), Character.valueOf('\u267c'));
        chatColorReplacements.put(Character.valueOf('1'), Character.valueOf('\u2673'));
        chatColorReplacements.put(Character.valueOf('2'), Character.valueOf('\u2674'));
        chatColorReplacements.put(Character.valueOf('3'), Character.valueOf('\u2675'));
        chatColorReplacements.put(Character.valueOf('4'), Character.valueOf('\u2676'));
        chatColorReplacements.put(Character.valueOf('5'), Character.valueOf('\u2677'));
        chatColorReplacements.put(Character.valueOf('6'), Character.valueOf('\u2678'));
        chatColorReplacements.put(Character.valueOf('7'), Character.valueOf('\u2679'));
        chatColorReplacements.put(Character.valueOf('8'), Character.valueOf('\u267a'));
        chatColorReplacements.put(Character.valueOf('9'), Character.valueOf('\u267b'));
        chatColorReplacements.put(Character.valueOf('a'), Character.valueOf('\u26c4'));
        chatColorReplacements.put(Character.valueOf('b'), Character.valueOf('\u26c5'));
        chatColorReplacements.put(Character.valueOf('c'), Character.valueOf('\u26c6'));
        chatColorReplacements.put(Character.valueOf('d'), Character.valueOf('\u26c7'));
        chatColorReplacements.put(Character.valueOf('e'), Character.valueOf('\u26c8'));
        chatColorReplacements.put(Character.valueOf('f'), Character.valueOf('\u26c9'));
        chatColorReplacements.put(Character.valueOf('A'), Character.valueOf('\u26ca'));
        chatColorReplacements.put(Character.valueOf('B'), Character.valueOf('\u26cb'));
        chatColorReplacements.put(Character.valueOf('C'), Character.valueOf('\u26cc'));
        chatColorReplacements.put(Character.valueOf('D'), Character.valueOf('\u26cd'));
        chatColorReplacements.put(Character.valueOf('E'), Character.valueOf('\u26ce'));
        chatColorReplacements.put(Character.valueOf('F'), Character.valueOf('\u26cf'));
        chatColorReplacements.put(Character.valueOf('l'), Character.valueOf('\u26d0'));
        chatColorReplacements.put(Character.valueOf('m'), Character.valueOf('\u26d1'));
        chatColorReplacements.put(Character.valueOf('n'), Character.valueOf('\u26d2'));
        chatColorReplacements.put(Character.valueOf('o'), Character.valueOf('\u26d3'));
        chatColorReplacements.put(Character.valueOf('k'), Character.valueOf('\u26d4'));
        chatColorReplacements.put(Character.valueOf('r'), Character.valueOf('\u26d5'));
        chatColorReplacements.put(Character.valueOf('L'), Character.valueOf('\u26d6'));
        chatColorReplacements.put(Character.valueOf('M'), Character.valueOf('\u26d7'));
        chatColorReplacements.put(Character.valueOf('N'), Character.valueOf('\u26d8'));
        chatColorReplacements.put(Character.valueOf('O'), Character.valueOf('\u26d9'));
        chatColorReplacements.put(Character.valueOf('K'), Character.valueOf('\u26da'));
        chatColorReplacements.put(Character.valueOf('R'), Character.valueOf('\u26db'));
        chatColorReplacementsInverse = chatColorReplacements.inverse();
    }
}

