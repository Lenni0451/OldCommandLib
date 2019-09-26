package net.Lenni0451.CommandLib.interfaces;

import net.Lenni0451.CommandLib.utils.ArrayHelper;

public interface ICommandBase {
	
	public void execute(final ArrayHelper<String> args);
	
}
