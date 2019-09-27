package net.Lenni0451.commands;

import net.Lenni0451.CommandLib.interfaces.ICommandBase;
import net.Lenni0451.CommandLib.reflection.annotations.ACommand;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

@ACommand(name = "Command2", description = "A test with @ACommand annotation and interface")
public class Command2 implements ICommandBase {

	@Override
	public void execute(ArrayHelper<String> args) {
		System.out.println("test command 2");
	}
	
}
