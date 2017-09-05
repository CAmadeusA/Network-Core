package com.camadeusa.utility.xoreboard;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class XoreBoardGlobalSidebar {
    private ConcurrentHashMap<String, Integer> lines = new ConcurrentHashMap<>();
    private XoreBoard xoreBoard;
    private String displayName;

    XoreBoardGlobalSidebar(XoreBoard xoreBoard) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', xoreBoard.getName());
        this.xoreBoard = xoreBoard;
        this.xoreBoard.getPlayers().forEach(this::showSidebar);
    }

    public void setDisplayName(String displayName) {
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        if(displayName.length() > 32) {
            System.out.println("[XoreBoardUtil] Error! DisplayName is longer than 32 characters (" + displayName + ")");
            return;
        }
        if(!this.displayName.equals(displayName)) {
            for(Player p : xoreBoard.getPlayers())
                XoreBoardPackets.sendPacket(p, XoreBoardPackets.getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 2));
            this.displayName = displayName;
        }
    }

    public void putLine(String key, int value) {
        for(Player p : xoreBoard.getPlayers())
            XoreBoardPackets.sendPacket(p, XoreBoardPackets.getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE));
        lines.put(key, value);
    }

    public void putLines(HashMap<String, Integer> lines) {
        this.lines.forEach((key, value) -> {
            if(lines.containsKey(key) && lines.get(key).equals(value))
                lines.remove(key);
        });
        for(Player p : xoreBoard.getPlayers())
            lines.forEach((key, value) -> XoreBoardPackets.sendPacket(p, XoreBoardPackets.getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE)));
        this.lines.putAll(lines);
    }

    public void rewriteLines(HashMap<String, Integer> lines) {
        this.lines.forEach((key, value) -> {
            if(!lines.containsKey(key))
                clearLine(key);
        });
        lines.forEach((key, value) -> {
            if(!this.lines.containsKey(key) || !this.lines.get(key).equals(value))
                putLine(key, value);
        });
    }

    public void clearLine(String key) {
        if(!this.lines.containsKey(key))
            return;
        for(Player p : xoreBoard.getPlayers())
            XoreBoardPackets.sendPacket(p, XoreBoardPackets.getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), 0, XoreBoardPackets.EnumScoreboardAction.REMOVE));
        this.lines.remove(key);
    }

    public void clearLines() {
        for(Player p : xoreBoard.getPlayers())
            this.lines.forEach((key, value) -> XoreBoardPackets.sendPacket(p, XoreBoardPackets.getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), 0, XoreBoardPackets.EnumScoreboardAction.REMOVE)));
        this.lines.clear();
    }

    public void hideSidebar() {
        xoreBoard.getPlayers().forEach(this::hideSidebar);
    }

    public void hideSidebar(Player p) {
        boolean showedGlobalSidebar = xoreBoard.getSidebar(p).isShowedGlobalSidebar();
        if(showedGlobalSidebar) {
            xoreBoard.getSidebar(p).setShowedGlobalSidebar(false);
            XoreBoardPackets.sendPacket(p, XoreBoardPackets.getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 1));
            return;
        }
        showSidebar(p);
        hideSidebar(p);
    }

    public void showSidebar() {
        xoreBoard.getPlayers().forEach(this::showSidebar);
    }

    public void showSidebar(Player p) {
        boolean showedGlobalSidebar = xoreBoard.getSidebar(p).isShowedGlobalSidebar();
        if(!showedGlobalSidebar) {
            xoreBoard.getEntries().get(p).setShowedGlobalSidebar(true);
            XoreBoardPackets.sendPacket(p, XoreBoardPackets.getSidebarPacket(xoreBoard.getId() + "." + p.getEntityId(), displayName, 0));
            XoreBoardPackets.sendPacket(p, XoreBoardPackets.getDisplayNamePacket(xoreBoard.getId() + "." + p.getEntityId()));
            lines.forEach((key, value) -> XoreBoardPackets.sendPacket(p, XoreBoardPackets.getLinePacket(xoreBoard.getId() + "." + p.getEntityId(), ChatColor.translateAlternateColorCodes('&', key), value, XoreBoardPackets.EnumScoreboardAction.CHANGE)));
            return;
        }
        hideSidebar(p);
        showSidebar(p);
    }

    public HashMap<String, Integer> getLines() {
        return new HashMap<>(lines);
    }
}
