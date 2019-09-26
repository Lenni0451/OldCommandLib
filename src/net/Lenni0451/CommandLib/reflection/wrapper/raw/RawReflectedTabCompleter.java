package net.Lenni0451.CommandLib.reflection.wrapper.raw;

import java.lang.reflect.Method;
import java.util.List;

import net.Lenni0451.CommandLib.utils.ArrayHelper;

public class RawReflectedTabCompleter {
	
	private final Object instance;
	
	private Method tabCompleteMethod;
	
	public RawReflectedTabCompleter(final Object instance) {
		this.instance = instance;
	}
	
	public boolean initMethod() {
		try {
			for(Method method : this.instance.getClass().getDeclaredMethods()) {
				if(method.getParameterTypes().length == 2) {
					if(!method.getParameterTypes()[0].equals(ArrayHelper.class) || !method.getParameterTypes()[1].equals(List.class)) {
						return false;
					}
					
					this.tabCompleteMethod = method;
					return true;
				}
			}
		} catch (Throwable e) {}
		return false;
	}
	
	public void callMethod(final ArrayHelper<String> args, final List<String> tabCompletions) {
		try {
			this.tabCompleteMethod.invoke(this.instance, new Object[] {args, tabCompletions});
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public Class<?> getRawClass() {
		return this.instance.getClass();
	}
	
	public Object getInstance() {
		return this.instance;
	}
	
}
