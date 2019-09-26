package net.Lenni0451.CommandLib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.Lenni0451.CommandLib.interfaces.ICommandBase;
import net.Lenni0451.CommandLib.interfaces.ITabCompleter;
import net.Lenni0451.CommandLib.reflection.annotations.ACommand;
import net.Lenni0451.CommandLib.reflection.annotations.ATabComplete;
import net.Lenni0451.CommandLib.reflection.wrapper.ReflectedCommand;
import net.Lenni0451.CommandLib.reflection.wrapper.raw.RawReflectedCommand;
import net.Lenni0451.CommandLib.reflection.wrapper.raw.RawReflectedTabCompleter;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

public class CommandManager {
	
	private final List<CommandBase> registeredCommands;
	
	public CommandManager() {
		this.registeredCommands = new ArrayList<>();
	}
	
	
	protected void registerLocalCommands() throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		this.registerLocalCommands(this);
	}
	
	public void registerLocalCommands(final Object instance) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Field[] fields = instance.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(!ICommandBase.class.isAssignableFrom(field.getType())) {
				continue;
			}
			
			field.setAccessible(true);
			CommandBase commandBase = (CommandBase) field.get(instance);
			if(commandBase == null) {
				Constructor<?> constr = null;
				for(Constructor<?> constructor : field.getType().getConstructors()) {
					if(constructor.getParameterTypes().length == 0) {
						constr = constructor;
						break;
					}
				}
				
				if(constr == null) continue;
				commandBase = (CommandBase) constr.newInstance();
				field.set(instance, commandBase);
			}
			
			this.addCommand(commandBase);
		}
	}
	
	public void addCommand(final CommandBase command) {
		this.registeredCommands.add(command);
	}
	
	public void addCommand(final Class<?>... clazzes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for(Class<?> clazz : clazzes) {
			if(!clazz.isAnnotationPresent(ACommand.class)) {
				continue;
			}
			ACommand commandAnnotation = clazz.getAnnotation(ACommand.class);
			if(ICommandBase.class.isAssignableFrom(clazz)) {
				ICommandBase commandBase;
				
				Constructor<?> constr = null;
				for(Constructor<?> constructor : clazz.getConstructors()) {
					if(constructor.getParameterTypes().length == 0) {
						constr = constructor;
						break;
					}
				}
				
				if(constr == null) continue;
				commandBase = (ICommandBase) constr.newInstance();
				
				ReflectedCommand reflectedCommand = new ReflectedCommand(commandAnnotation, commandBase);
				this.addCommand(reflectedCommand);
			} else {
				Object commandBase;
				
				Constructor<?> constr = null;
				for(Constructor<?> constructor : clazz.getConstructors()) {
					if(constructor.getParameterTypes().length == 0) {
						constr = constructor;
						break;
					}
				}
				
				if(constr == null) continue;
				commandBase = constr.newInstance();
				
				RawReflectedCommand rawReflectedCommand = new RawReflectedCommand(commandAnnotation, commandBase);
				this.addCommand(rawReflectedCommand);
			}
		}
	}
	
	
	public List<CommandBase> getCommands() {
		return new ArrayList<>(this.registeredCommands);
	}
	
	public int getCommandCount() {
		return this.registeredCommands.size();
	}
	
	public CommandBase getCommandByName(final String name) {
		for(CommandBase commandBase : this.registeredCommands) {
			if(commandBase.getName().equalsIgnoreCase(name)) {
				return commandBase;
			}
		}
		return null;
	}
	
	public CommandBase getCommandByNameOrAlias(final String name) {
		for(CommandBase commandBase : this.registeredCommands) {
			if(commandBase.getName().equalsIgnoreCase(name) || commandBase.getAliases().contains(name.toLowerCase())) {
				return commandBase;
			}
		}
		return null;
	}
	
	public CommandBase getCommandByClass(final Class<?> clazz) {
		for(CommandBase commandBase : this.registeredCommands) {
			if(commandBase instanceof ReflectedCommand) {
				if(((ReflectedCommand) commandBase).getRawClass().equals(clazz)) return commandBase;
			} else if(commandBase instanceof RawReflectedCommand) {
				if(((RawReflectedCommand) commandBase).getRawClass().equals(clazz)) return commandBase;
			} else {
				if((commandBase.getClass().equals(clazz))) return commandBase;
			}
		}
		return null;
	}
	
	
	public boolean callCommand(final String text) {
		String commandName = text.split(" ")[0];
		String[] rawArgs = text.split(" ");
		rawArgs = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
		ArrayHelper<String> args = ArrayHelper.<String>instanceOf(rawArgs);
		
		CommandBase command;
		if((command = this.getCommandByNameOrAlias(commandName)) != null) {
			command.execute(args);
			return true;
		}
		
		return false;
	}
	
	public List<String> callTabComplete(final String text) {
		List<String> tabCompletions = new ArrayList<>();
		
		String commandName = text.split(" ")[0];
		String[] rawArgs = text.split(" ");
		rawArgs = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
		
		if(rawArgs.length == 0 && !text.endsWith(" ")) {
			for(CommandBase commandBase : this.registeredCommands) {
				if(commandBase.getName().toLowerCase().startsWith(commandName)) {
					tabCompletions.add(commandBase.getName());
				}
			}
		} else {
			ArrayHelper<String> args;
			if(!text.endsWith(" ")) {
				rawArgs = Arrays.copyOfRange(rawArgs, 0, rawArgs.length - 1);
			}
			args = ArrayHelper.<String>instanceOf(rawArgs);
			
			CommandBase commandBase = this.getCommandByNameOrAlias(commandName);
			if(commandBase != null) {
				if(commandBase instanceof ReflectedCommand) {
					ReflectedCommand reflectedCommand = (ReflectedCommand) commandBase;
					if(reflectedCommand.getRawClass().isAnnotationPresent(ATabComplete.class)) {
						if(ITabCompleter.class.isAssignableFrom(reflectedCommand.getRawClass())) {
							((ITabCompleter) reflectedCommand.getInstance()).handleTabComplete(args, tabCompletions);
						} else {
							RawReflectedTabCompleter rawReflectedTabCompleter = new RawReflectedTabCompleter(reflectedCommand.getInstance());
							if(rawReflectedTabCompleter.initMethod()) {
								rawReflectedTabCompleter.callMethod(args, tabCompletions);
							}
						}
					}
				} else if(commandBase instanceof RawReflectedCommand) {
					RawReflectedCommand rawReflectedCommand = (RawReflectedCommand) commandBase;
					if(rawReflectedCommand.getRawClass().isAnnotationPresent(ATabComplete.class)) {
						if(ITabCompleter.class.isAssignableFrom(rawReflectedCommand.getRawClass())) {
							((ITabCompleter) rawReflectedCommand.getInstance()).handleTabComplete(args, tabCompletions);
						} else {
							RawReflectedTabCompleter rawReflectedTabCompleter = new RawReflectedTabCompleter(rawReflectedCommand.getInstance());
							if(rawReflectedTabCompleter.initMethod()) {
								rawReflectedTabCompleter.callMethod(args, tabCompletions);
							}
						}
					}
				} else if(commandBase instanceof ITabCompleter) {
					((ITabCompleter) commandBase).handleTabComplete(args, tabCompletions);
				}
			}
		}
		
		{
			rawArgs = text.split(" ");
			
			Iterator<String> iterator = tabCompletions.iterator();
			while(iterator.hasNext()) {
				String tabCompletion = iterator.next();
				if(!tabCompletion.toLowerCase().startsWith(rawArgs[rawArgs.length - 1].toLowerCase())) {
					iterator.remove();
				}
			}
		}
		return tabCompletions;
	}
	
}
