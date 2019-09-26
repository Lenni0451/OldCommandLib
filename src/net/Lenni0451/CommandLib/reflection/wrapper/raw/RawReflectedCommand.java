package net.Lenni0451.CommandLib.reflection.wrapper.raw;

import java.lang.reflect.Method;

import net.Lenni0451.CommandLib.CommandBase;
import net.Lenni0451.CommandLib.reflection.annotations.ACommand;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

public class RawReflectedCommand extends CommandBase {

	private final Object commandBase;
	
	public RawReflectedCommand(final ACommand command, final Object commandBase) {
		super(command.name(), command.description());
		for(String alias : command.aliases()) {
			this.addAlias(alias);
		}
		this.commandBase = commandBase;
	}

	@Override
	public void execute(ArrayHelper<String> args) {
		Method execute = null;
		for(Method method : this.commandBase.getClass().getDeclaredMethods()) {
			if(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(ArrayHelper.class)) {
				execute = method;
				break;
			}
		}
		try {
			execute.invoke(this.commandBase, new Object[] {args});
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public Class<?> getRawClass() {
		return this.commandBase.getClass();
	}
	
	public Object getInstance() {
		return this.commandBase;
	}
	
}
