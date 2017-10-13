package com.camadeusa.utility.xoreboard;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.camadeusa.utility.Random;

/**
 * @author haelexuis
 * @version 1.0
 * ScoreBoardUtil is better ScoreBoard api based on packets (very lightweight)
 */
public class XoreBoardUtil {
    private static Scoreboard bukkitScoreboard;
    private static HashMap<String, XoreBoard> scoreboards = new HashMap<>();
    private static Team.OptionStatus collisions = Team.OptionStatus.NEVER;
    private static Team.OptionStatus nameTags;
    private static Objective belowNames;
    private static int packetIterator = 0;
    private static int nameIterator = 0;
    private static int serverStart = 0;

    
    public void init() {
        bukkitScoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        serverStart = Random.instance().nextInt(100);
    }

    public static XoreBoard getXoreBoard(String name) {
        if(!scoreboards.containsKey(name))
            scoreboards.put(name, new XoreBoard(bukkitScoreboard, serverStart + "." + getPacketIterator(), name));
        return scoreboards.get(name);
    }

    public static XoreBoard getNextXoreBoard() {
        return getXoreBoard(nameIterator++ + "");
    }

    public static void removeXoreBoard(XoreBoard xoreBoard) {
        xoreBoard.destroy();
        scoreboards.remove(xoreBoard.getName());
    }

    public static void removeXoreBoard(String name) {
        if(!scoreboards.containsKey(name))
            return;
        getXoreBoard(name).destroy();
        scoreboards.remove(name);
    }

    public static Team getTeam(String teamName) {
        Team team = bukkitScoreboard.getTeam(teamName);
        if(team == null)
            team = bukkitScoreboard.registerNewTeam(teamName);
        if(collisions != null)
            team.setOption(Team.Option.COLLISION_RULE, collisions);
        if(nameTags != null)
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, nameTags);
        return team;
    }

    public static void setUpBelowNames(String displayText) {
        belowNames = bukkitScoreboard.getObjective(DisplaySlot.BELOW_NAME);
        if(belowNames == null) {
            belowNames = bukkitScoreboard.registerNewObjective("below", "names");
            belowNames.setDisplaySlot(DisplaySlot.BELOW_NAME);
            belowNames.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayText));
        }
        else
            belowNames.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayText));
    }

    public static void updateBelowName(Player p, int value) {
        if(belowNames == null)
            return;
        bukkitScoreboard.getObjective(DisplaySlot.BELOW_NAME).getScore(p.getName()).setScore(value);
    }

    public static void setGlobalCollisions(Team.OptionStatus value) {
        collisions = value;
        for(Team t : bukkitScoreboard.getTeams()) {
            Bukkit.broadcastMessage("Team " + t.getName() + " to " + collisions.toString());
            t.setOption(Team.Option.COLLISION_RULE, collisions);
        }
    }

    public static void setGlobalNameTagVisible(Team.OptionStatus value) {
        nameTags = value;
        for(Team t : bukkitScoreboard.getTeams())
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, nameTags);
    }

    public static void disableAllCollisions() {
        for(Team t : bukkitScoreboard.getTeams())
            t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public static void destroy() {
        if(bukkitScoreboard != null) {
            for(Team team : bukkitScoreboard.getTeams()) {
                for(String entry : team.getEntries()) {
                    team.removeEntry(entry);
                }
            }
        }

        ArrayList<XoreBoard> boards = new ArrayList<>(scoreboards.values());
        for(XoreBoard xoreBoard : boards)
            removeXoreBoard(xoreBoard);

        scoreboards = null;
    }

    static int getPacketIterator() {
        return packetIterator++;
    }
}
