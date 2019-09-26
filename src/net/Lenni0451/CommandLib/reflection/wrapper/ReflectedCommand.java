package net.Lenni0451.CommandLib.reflection.wrapper;

import net.Lenni0451.CommandLib.CommandBase;
import net.Lenni0451.CommandLib.interfaces.ICommandBase;
import net.Lenni0451.CommandLib.reflection.annotations.ACommand;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

public class ReflectedCommand extends CommandBase {

	private final ICommandBase commandBase;
	
	public ReflectedCommand(final ACommand command, final ICommandBase commandBase) {
		super(command.name(), command.description());
		for(String alias : command.aliases()) {
			this.addAlias(alias);
		}
		this.commandBase = commandBase;
	}

	@Override
	public void execute(ArrayHelper<String> args) {
		this.commandBase.execute(args);
	}
	
	
	public Class<?> getRawClass() {
		return this.commandBase.getClass();
	}
	
	public ICommandBase getInstance() {
		return this.commandBase;
	}
	
}
