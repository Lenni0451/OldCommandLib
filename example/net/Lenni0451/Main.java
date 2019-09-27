package net.Lenni0451;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import net.Lenni0451.CommandLib.CommandManager;

public class Main {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		CommandManager manager = new CommandManager();
		
		manager.registerLocalCommands(Commands.class);
		
		Scanner s = new Scanner(System.in);
		while(true) {
			String line = s.nextLine();
			if(!manager.callCommand(line)) {
				System.out.println("Command not found!");
			}
		}
	}
	
}
