package com.camadeusa.module.game.uhcsg;

import com.camadeusa.utility.command.Command;
import com.camadeusa.utility.command.CommandArgs;

public class UHCSGCommands {
	@Command(name = "nextSegment", usage = "/nextSegment")
	public void hub(CommandArgs args) {
		UHCSGOrionGame.getInstance().getCurrentSegment().nextSegment();
	}
}
