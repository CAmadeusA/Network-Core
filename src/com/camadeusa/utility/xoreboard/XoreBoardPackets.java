package com.camadeusa.utility.xoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class XoreBoardPackets {
    public static String version = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

    enum EnumScoreboardAction {
        CHANGE,
        REMOVE;

        EnumScoreboardAction() {
        }
    }

    private static Object getClassInstance(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getPacketPlayOutScoreboardObjective() {
        return getClassInstance("net.minecraft.server." + version + ".PacketPlayOutScoreboardObjective");
    }

    private static Object getPacketPlayOutScoreboardDisplayObjective() {
        return getClassInstance("net.minecraft.server." + version + ".PacketPlayOutScoreboardDisplayObjective");
    }

    private static Object getPacketPlayOutScoreboardScore() {
        return getClassInstance("net.minecraft.server." + version + ".PacketPlayOutScoreboardScore");
    }

    private static Object getPacketPlayOutScoreboardTeam() {
        return getClassInstance("net.minecraft.server." + version + ".PacketPlayOutScoreboardTeam");
    }

    static Object getSidebarPacket(String packetName, String displayName, int type) {
        Object objectivePacket = getPacketPlayOutScoreboardObjective();

        rewriteField(objectivePacket, "a", packetName);
        if(type != 1) {
            rewriteField(objectivePacket, "b", displayName);
            rewriteField(objectivePacket, "c", Enum.valueOf(getClass("net.minecraft.server." + version + ".IScoreboardCriteria$EnumScoreboardHealthDisplay"), "INTEGER"));
        }
        rewriteField(objectivePacket, "d", type);

        return objectivePacket;
    }

    static Object getDisplayNamePacket(String packetName) {
        Object displayPacket = getPacketPlayOutScoreboardDisplayObjective();

        rewriteField(displayPacket, "a", 1);
        rewriteField(displayPacket, "b", packetName);

        return displayPacket;
    }

    static Object getLinePacket(String packetName, String key, int value, EnumScoreboardAction action) {
        Object scorePacket = getPacketPlayOutScoreboardScore();

        rewriteField(scorePacket, "a", key);
        rewriteField(scorePacket, "b", packetName);
        rewriteField(scorePacket, "c", value);
        rewriteField(scorePacket, "d", Enum.valueOf(getClass("net.minecraft.server." + version + ".PacketPlayOutScoreboardScore$EnumScoreboardAction"), action.name()));

        return scorePacket;
    }

    static Object getTeamPacket(String packetName, String teamName, String displayName, String prefix, String suffix, String nameTagVisibility, String collisions, int type) {
        Object teamPacket = getPacketPlayOutScoreboardTeam();

        rewriteField(teamPacket, "a", teamName);
        rewriteField(teamPacket, "b", displayName);
        rewriteField(teamPacket, "c", prefix);
        rewriteField(teamPacket, "d", suffix);
        rewriteField(teamPacket, "e", nameTagVisibility);
        rewriteField(teamPacket, "f", collisions);
        rewriteField(teamPacket, "i", 1); // IDK
        rewriteField(teamPacket, "h", type);

        return teamPacket;
    }

    private static void rewriteField(Object packet, String key, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(key);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getFieldInstance(Object instance, String fieldName) {
        try {
            Field field = getDeclaredField(getDeclaredFields(instance.getClass()), fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void invokeMethod(Object instance, String methodName, Class<?>[] classes, Object... values) {
        try {
            Method method = instance.getClass().getDeclaredMethod(methodName, classes);
            method.setAccessible(true);
            method.invoke(instance, values);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static List<Field> getDeclaredFields(Class clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if(clazz.getSuperclass() != null) {
            fieldList.addAll(getDeclaredFields(clazz.getSuperclass()));
        }
        return fieldList;
    }

    private static Field getDeclaredField(List<Field> fields, String fieldName) {
        return fields.stream().filter(f -> f.getName().equalsIgnoreCase(fieldName)).findFirst().orElse(null);
    }

    static void sendPacket(Player player, Object packet) {
        Object craftPlayer = getClass("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").cast(player);
        Object handle = getFieldInstance(craftPlayer, "entity");
        Object playerConnection = getFieldInstance(handle, "playerConnection");
        invokeMethod(playerConnection, "sendPacket", new Class[]{getPacketType()}, packet);
    }

    private static Class getPacketType() {
        try {
            return Class.forName("net.minecraft.server." + version + ".Packet");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Class getClass(String clazz) {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
