package net.Lenni0451.commands;

import net.Lenni0451.CommandLib.reflection.annotations.ACommand;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

@ACommand(name = "Command3", description = "A test with raw reflection")
public class Command3 {
	
	public void onCommandExecuteEventStuffHere(final ArrayHelper<String> args) {
		System.out.println("test command 3");
	}
	
}
