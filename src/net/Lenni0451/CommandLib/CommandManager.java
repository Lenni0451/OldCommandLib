package net.Lenni0451.CommandLib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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
	
	
	/**
	 * Register all commands which are present as local variable in the current class instance
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	protected void registerLocalCommands() throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		this.registerLocalCommands(this);
	}

	/**
	 * Register all commands which are present as local variable in the given class instance
	 * 
	 * @param instance The instance of the class which has commands registered as local variable
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	public void registerLocalCommands(final Object instance) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Field[] fields = instance.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(CommandBase.class.isAssignableFrom(field.getType())) {
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
			} else {
				this.addCommand(field.getType());
			}
		}
	}
	
	/**
	 * Register all commands which are present as local variable in the given class
	 * 
	 * @param clazz The class which has all commands as local variables
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	public void registerLocalCommands(final Class<?> clazz) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			if(!Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if(CommandBase.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				CommandBase commandBase = (CommandBase) field.get(null);
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
					field.set(null, commandBase);
				}
				
				this.addCommand(commandBase);
			} else {
				this.addCommand(field.getType());
			}
		}
	}
	
	/**
	 * Register a Command extending CommandBase
	 * 
	 * @param command The command to add to the list
	 */
	public void addCommand(final CommandBase command) {
		this.registeredCommands.add(command);
	}
	
	/**
	 * Register a command with the @ACommand annotation
	 * 
	 * @param clazzes The classes which may have the @ACommand annotation.<br>If a class does not have the annotation it will get skipped without exceptions.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
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
	
	
	/**
	 * Get a copy of the list with all commands
	 * 
	 * @return copy of registeredCommands
	 */
	public List<CommandBase> getCommands() {
		return new ArrayList<>(this.registeredCommands);
	}
	
	/**
	 * Get the count of registered commands
	 * 
	 * @return command count
	 */
	public int getCommandCount() {
		return this.registeredCommands.size();
	}
	
	/**
	 * Get a command instance by the name of the command
	 * 
	 * @param name of the command
	 * @return the command instance
	 */
	public CommandBase getCommandByName(final String name) {
		for(CommandBase commandBase : this.registeredCommands) {
			if(commandBase.getName().equalsIgnoreCase(name)) {
				return commandBase;
			}
		}
		return null;
	}
	
	/**
	 * Get a command instance by the name or alias of the command
	 * 
	 * @param name or alias of the command
	 * @return the command instance
	 */
	public CommandBase getCommandByNameOrAlias(final String name) {
		for(CommandBase commandBase : this.registeredCommands) {
			if(commandBase.getName().equalsIgnoreCase(name) || commandBase.getAliases().contains(name.toLowerCase())) {
				return commandBase;
			}
		}
		return null;
	}
	
	/**
	 * Get a command instance by its class
	 * 
	 * @param clazz of the command instance
	 * @return the command instance
	 */
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
	
	
	/**
	 * Call the given command with arguments
	 * 
	 * @param text which a user entered to execute a command
	 * @return if the command was executed successfully
	 */
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
	
	/**
	 * Get a list with all tab completions which are available at the current state
	 * 
	 * @param text which a user entered and is trying to tab complete now
	 * @return the list with possible tab completions
	 */
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
		
		if(!text.endsWith(" ")) {
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
