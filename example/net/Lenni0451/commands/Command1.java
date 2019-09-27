package net.Lenni0451.commands;

import net.Lenni0451.CommandLib.CommandBase;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

public class Command1 extends CommandBase {

	public Command1() {
		super("Command1", "Test with CommandBase");
	}

	@Override
	public void execute(ArrayHelper<String> args) {
		System.out.println("Test command 1");
	}
	
}
