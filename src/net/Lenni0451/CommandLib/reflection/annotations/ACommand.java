package net.Lenni0451.CommandLib.reflection.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface ACommand {
	
	String name();
	String description() default "";
	String[] aliases() default {};
	
}
