package com.camadeusa.module.game.uhcsg;

import java.util.ArrayList;

import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;

public class UHCSGCommands {
	public static ArrayList<String> debugList = new ArrayList<>();
	
	@Command(name = "nextSegment", usage = "/nextSegment")
	public void hub(CommandArgs args) {
		UHCSGOrionGame.getInstance().getCurrentSegment().nextSegment();
	}
	
	@Command(name = "debug", usage = "/debug <Time/Tier/(More to be added)>")
	public void debug(CommandArgs args) {
		if (args.getArgs().length == 0) {
			args.getPlayer().chat("/debug <What would you like to debug? (Time/Tier)>");
		} else {
			for (String db : args.getArgs()) {
				switch (db.toLowerCase()) {
				case "time":
					if (!debugList.contains("time")) {
						debugList.add("time");
					}
					break;
				case "tier":
					if (!debugList.contains("tier")) {
						debugList.add("tier");
					}
					break;
				case "clear":
					debugList = new ArrayList<>();
					break;
				
				}
			}
		}
	}
}
