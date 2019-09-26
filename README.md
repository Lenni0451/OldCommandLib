
# CommandLib
CommandLib is an easy to use Library for managing commands and tab complete in you Java program.

### How to use
#### Create and register a command
To create a command simply create a new class and let it extend ```CommandBase```.  
To register it create a new instance of ```CommandManager``` and do ```commandManager.addCommand(new TestCommand());```.
#### Other ways of creating commands
If you like reflection you can simply create a class and let it implement ```ICommandBase```.  
After that you add the ```@ACommand``` annotation at the top to declare it as an command.  
To register it do ```commandManager.addCommand(TestCommand.class);```.  
Ensure that you have an empty constructor!
#### Another way
If you want completely freedom simply create a class and add the ```ACommand``` annotation.  
Create a method called any way you want and set as a parameter ```ArrayHelper<String> args```.  
Now register it by using ```commandManager.addCommand(TestCommand.class);```.
#### 
If you want to register multiple commands extending ```CommandBase``` you can also simply add them to a class like so ```TestCommand testCommand = null;``` and do ```commandManager.registerLocalCommands(new CommandList());```.

### TabComplete
You can also use tab complete if your application is supporting it. Simply let your class implement ```ITabCompleter``` and you are good to go.  
If you declared your class with an annotation you also have to add the ```ATabComplete``` annotation as well.  
You do not need to implement ```ITabCompleter```. You can also create a method with whatever name and add the arguments ```ArrayHelper<String> args, List<String> tabCompletions```.

### Contribute
If you want to contribute keep the added code in the same style as the existing one and mark changes so they can be easily identified.
