import java.util.List;

import net.Lenni0451.CommandLib.CommandBase;
import net.Lenni0451.CommandLib.CommandManager;
import net.Lenni0451.CommandLib.interfaces.ICommandBase;
import net.Lenni0451.CommandLib.interfaces.ITabCompleter;
import net.Lenni0451.CommandLib.reflection.annotations.ACommand;
import net.Lenni0451.CommandLib.reflection.annotations.ATabComplete;
import net.Lenni0451.CommandLib.utils.ArrayHelper;

public class Test {
	
	public static void main(String[] args) throws Throwable {
		CommandManager manager = new CommandManager();
		manager.addCommand(new Test1());
		manager.addCommand(Test2.class);
		manager.addCommand(Test3.class);

		System.out.println(manager.callCommand("test1"));
		System.out.println(manager.callCommand("test2"));
		System.out.println(manager.callCommand("test3"));
		
		System.out.println();

		System.out.println(manager.callTabComplete("test1 te terret"));
		System.out.println(manager.callTabComplete("test2 te ttr"));
		System.out.println(manager.callTabComplete("test3 te tes"));
	}
	
	public static class Test1 extends CommandBase implements ITabCompleter {

		public Test1() {
			super("Test1");
		}

		@Override
		public void execute(ArrayHelper<String> args) {
			System.out.println("Test1");
		}

		@Override
		public void handleTabComplete(ArrayHelper<String> args, List<String> tabCompletions) {
			tabCompletions.add("test1_tab");
		}
		
	}
	
	@ACommand(name = "Test2")
	@ATabComplete
	public static class Test2 implements ICommandBase, ITabCompleter {

		@Override
		public void execute(ArrayHelper<String> args) {
			System.out.println("Test2");
		}

		@Override
		public void handleTabComplete(ArrayHelper<String> args, List<String> tabCompletions) {
			tabCompletions.add("test2_tab");
		}
		
	}

	@ACommand(name = "Test3")
	@ATabComplete
	public static class Test3 {

		public void thisIsATest(ArrayHelper<String> args) {
			System.out.println("Test3");
		}
		
		public void youCanCallThisHoweverYouWant_luul(ArrayHelper<String> args, List<String> tabCompletions) {
			tabCompletions.add("test3_tab");
		}
		
	}
	
}
