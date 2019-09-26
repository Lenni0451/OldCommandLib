package net.Lenni0451.CommandLib.interfaces;

import java.util.List;

import net.Lenni0451.CommandLib.utils.ArrayHelper;

public interface ITabCompleter {
	
	public void handleTabComplete(final ArrayHelper<String> args, final List<String> tabCompletions);
	
}
