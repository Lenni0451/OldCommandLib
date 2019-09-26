package net.Lenni0451.CommandLib;

import java.util.ArrayList;
import java.util.List;

import net.Lenni0451.CommandLib.interfaces.ICommandBase;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

public abstract class CommandBase implements ICommandBase {

	private final String name;
	private final String description;
	private final List<String> aliases;
	
	public CommandBase(final String name) {
		this(name, "");
	}
	
	public CommandBase(final String name, final String description) {
		this.name = name;
		this.description = description;
		this.aliases = new ArrayList<>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public List<String> getAliases() {
		return new ArrayList<>(this.aliases);
	}
	
	
	@Override
	public abstract void execute(final ArrayHelper<String> args);
	
	
	protected void addAlias(final String alias) {
		this.aliases.add(alias.toLowerCase());
	}
	
}
