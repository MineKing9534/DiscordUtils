![[Java CI]](https://github.com/MineKing9534/DiscordUtils/actions/workflows/check.yml/badge.svg)
![[Latest Version]](https://maven.mineking.dev/api/badge/latest/releases/de/mineking/DiscordUtils?prefix=v&name=Latest%20Version)

# Installation

DiscordUtils is hosted on a custom repository at [https://maven.mineking.dev](https://maven.mineking.dev/releases/de/mineking/DiscordUtils). Replace VERSION with the lastest version (without the `v` prefix).
Alternatively, you can download the artifacts from jitpack (not recommended).

### Gradle

```groovy
repositories {
  maven { url "https://maven.mineking.dev/releases" }
}

dependencies {
  implementation "de.mineking:DiscordUtils:VERSION"
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>mineking</id>
    <url>https://maven.mineking.dev/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>de.mineking</groupId>
    <artifactId>DiscordUtils</artifactId>
    <version>VERSION</version>
  </dependency>
</dependencies>
```

### Dependencies

DiscordUtils requires [JDA](https://github.com/discord-jda/JDA), so you have to have it in your bot's dependency list as well (You should already have it).
Some managers might require other dependencies, that you have to add to your project in order for that manager to work.
These additional dependencies are mentioned in the section of the corresponding manager.

# Basic Usage

To start using DiscordUtils you can simply create an instance of `DiscordUtils` with the first constructor parameter being your `JDA` instance and the second one your bot class instance.

Example:

```java
public class TestBot {
  public static void main(String[] args) {
    new TestBot(args[0]);
  }

  public final JDA jda;
  public final DiscordUtils discordUtils;

  public TestBot(String token) {
    jda = JDABuilder.createDefault(token)
            .build();

    discordUtils = DiscordUtils.create(jda, this)
            .build();
  }
}
```

You can then later access your bot instance from any place where DiscordUtils gives you access to the `DiscordUtils` instance, which helps to avoid static abuse.

# Localization

You can enable localization by calling the `setLocalizationManager` method on your `DiscordUtils` instance.
You can provide a default locale that will be set as default value and a set of locales that you support.
The third parameter is a `LocalizationFunction` that you can use to provide a localized description for a localization path.
You can also change how the paths are generated automatically.

# Console

Sometimes, you might want to mirror your bot's console to your admins' direct messages or a text channel. DiscordUtils provides a simple way to archive this.

```java
public class TestBot {
  public static void main(String[] args) {
    new TestBot(args[0]);
  }

  public final JDA jda;
  public final DiscordUtils discordUtils;

  public TestBot(String token) {
    jda = JDABuilder.createDefault(token)
            .build();

    discordUtils = DiscordUtils.create(jda, this)
            .mirrorConsole(RedirectTarget.directMessage(YOUR_USER_ID)) //Mirror the console to you dms
            .build();
  }
}
```

To send the logs to a text channel, simply use `RedirectTarget.channel(CHANNEL_ID)`. You can also provide multiple RedirectTargets at once.

# Managers

The DiscordUtils library is split into multiple Managers to allow you to decide which features you want to use.
You can either manually register a Manager with `addManager` or with the corresponding method for your Manager.
These dedicated methods start with `use`, to use the CommandManager your can use `useCommandManager`.

To get a previously registered manager by type, you can manually get it via `getManager` that holds the manager if present or `Optional.empty()` otherwise.
Alternatively you can use the dedicated method, for example `getCommandManager`.
The dedicated methods throw an `IllegalStateException` if the requested manager is not present.

Available Managers:

- [CommandManager](#command-manager)
- [Language cache](#language-cache)
- [Event Manager](#event-manager)
- [UI Manager](#ui-manager)
- [Help Manager](#help-manager)
- [List Manager](#list-manager)

## Command Manager

The CommandManager can be used to simplify the process of registering and handling discord application commands.
You can register the CommandManager by calling `useCommandManager` on your `DiscordUtils` instance.
The `useCommandManager` method takes three parameters: A function to create a Context for command execution, a function to create a Context for autocomplete and a consumer that can be used to configure the newly created `CommandManager`.

Example:

```java
//An instance of this context that will be created using the context creator function that you provided when creating the CommandManager, will be passed to every command execution. You can customize the context to your needs
public class CommandContext implements ICommandContext {
  private final GenericCommandInteractionEvent event;

  public CommandContext(GenericCommandInteractionEvent event) {
    this.event = event;
  }

  public GenericCommandInteractionEvent getEvent() {
    return event;
  }

  //Add methods like 'reply' that use your bot's localization system

  //If you have a moderation bot, you could add methods that give access to the executing users warnings so that you can do context.getWarnings() wherever you need
}

public class AutocompleteContext implements IAutocompleteContext {
  private final CommandAutocompleteEvent event;

  public AutocompleteContext(CommandAutocompleteInteractionEvent event) {
    this.event = event;
  }

  public CommandAutocompleteEvent getEvent() {
    return event;
  }
}

public class TestBot {
  public static void main(String[] args) {
    new TestBot(args[0]);
  }

  public final JDA jda;
  public final DiscordUtils discordUtils;

  public TestBot(String token) {
    jda = JDABuilder.createDefault(token)
            .build();

    discordUtils = DiscordUtils.create(jda, this)
            .useCommandManager(
                    shared.CommandContext::new, //Function to create command context
                    shared.AutocompleteContext::new, //Function to create autocomplete context
                    cmdMan -> cmdMan.updateCommands() //Consumer to configure the resulting CommandManager. The updateCommands() method schedules an update of all commands for when the bot is successfully logged in
            ).build();
  }
}
```

To actually create your commands, there are two ways: inherited and annotated. In most cases, it is recommended to use annotated commands because they are a lot easier to create and the resulting code is a lot more readable.

### Annotated

Here is a basic example of an annotated command:

```java

@ApplicationCommand(name = "echo", description = "sends back the the input") //The @ApplicationCommand annotation specifies basic information about your command. It has to be present!
public class EchoCommand {
  @ApplicationCommandMethod //The @ApplicationCommandMethod specifies the method that actually handles the command execution
  public void performCommand(shared.CommandContext context, //The method can have any name
                             @Option(name = "text") String text //All method parameters with the @Option annotation will be added to the command's option list. They will automatically be created and parsed
  ) {
    context.event.reply(text).setEphemeral(true).queue();
  }
}
```

If your option should have the same name on discord as your method parameter, you can provide no name in the `@Option` annotation.
DiscordUtils will then use the method parameters name. In order for this to function as expected, you have to compile your bot with the `-parameters` option.
In Gradle, you can archive this by adding this to `build.gradle`:

```groovy
compileJava {
  options.compilerArgs << '-parameters'
}
```

You can make options non-required by adding `required = false` in `@Option`. If no value for an optional option is provided, it will be `null`.
This means that you have to use the wrapper classes for primitive types to allow for nullability.
So instead of `@Option(required = false) int x` you have to use `@Option(required = false) Integer x`.
Alternatively, you can wrap any type in an `Optional`. You can then simply handle them like this:

```java

@ApplicationCommand(name = "test")
public class TestCommand {
  @ApplicationCommandMethod
  public void performCommand(@Option(required = false) Optional<String> text) {
    text.ifPresentOrElse(
            value -> {/* handle value */},
            () -> {/* handle missing option */}
    );
  }
}
```

If you want to use a default value, you can add the corresponding default annotation for your type.
Example:

```java

@ApplicationCommand(name = "test")
public class TestCommand {
  @ApplicationCommandMethod
  public void performCommand(@BooleanDefault(false) @Option(required = false) boolean flag) {
    //flag will never be null. If the option is not provided, it will be 'false' as specified by the annotation parameter.
  }
}
```

If you want to limit the possible values for an option to specific choices, you can register these choices the following way:

```java
public class TestCommand {
  @Choice("value")
  public List<Choice> choices = Arrays.asList( //These will be the only choices for option 'value'
          new Command.Choice("name a", "value a"),
          new Command.Choice("name b", "value b"),
          new Command.Choice("name c", "value c")
  );

  @ApplicationCommandMethod
  public void performCommand(@Option String value) {

  }
}
```

When dealing with choices, you might also have an enum that represents your possible choices. In DiscordUtils, you can do:

```java
public class TestCommand {
  public enum Test {
    A,
    B,
    C
  }

  @ApplicationCommandMethod
  public void performCommand(@Option Test value) { //When using an enum as option type, only the enum entries are allowed as choices

  }
}
```

If you want to dynamically create your choices based on the user input, you can use autocomplete. However, du to a discord limitation, autocomplete choices are not enforced:

```java
public class TestCommand {
  @Autocomplete("value")
  public void handleAutocomplete(shared.AutocompleteContext context) {
    context.event
            .replyChoice(context.event.getFocusedOption().getValue() + "!", context.event.getFocusedOption().getValue() + "!") //Suggest adding an exclamation mark at the end of the current input
            .queue();
    //If you want to access other command information like registered execution conditions, you can add a method parameter of type 'Command<?, ?>'. From there you can access all data about the current command that you want
  }

  @ApplicationCommandMethod
  public void performCommand(@Option String value) {

  }
}
```

Sometimes, you may want to ask the user for an array of options. Unfortunately, Discord doesn't allow us to do so. However, DiscordUtils has a clever trick to simulate similar behavior:

```java
public class TestCommand {
  @ApplicationCommandMethod
  public void performCommand(@OptionArray(minCount = 2, maxCount = 5) @Option String[] value) { //You can either use an array or a List. As component type, every type is supported
    //This will create 5 options, where 2 are required and 3 are not required. Their name will be 'value1', 'value2', etc.
  }
}
```

You can also register parsers to use your own types as a parameter type. In fact, all types that are supported by default are implemented like this.
For example, if you want to use a color as a parameter, you could write the following option parser:

```java
public class ColorParser implements IOptionParser {
  @Override
  public boolean accepts(@NotNull Class<?> type) {
    return type.isAssignableFrom(Color.class); //Check whether the type is Color
  }

  @NotNull
  @Override
  public OptionType getType(@NotNull CommandManager<?, ?> manager, @NotNull Class<?> type, @NotNull Type generic) {
    return OptionType.STRING; //We will be using hex codes to represent the color, which means it will be a string
  }

  @Nullable
  @Override
  public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
    try {
      return event.getOption(name, o -> Color.decode(o.getAsString())); //Parse color
    } catch(NumberFormatException e) {
      return null; //Set the parameter to null if the provided color was not valid
    }
  }

  @Override
  public void registerOption(@NotNull de.mineking.discordutils.commands.Command<?> cmd, @NotNull OptionData option, @NotNull Parameter param) {
    //The color will be in the format #rrggbb, so the length of the string is 7
    cmd.addOption(option.setMinLength(7).setMaxLength(7)); //Customize option
  }
}
```

In this case, both the `accepts` and `getType` method implementations are very simple. In this case, you can make use of the `OptionParser` class like this:

```java
public class ColorParser extends OptionParser {
  public ColorParser() {
    super(Color.class, OptionType.STRING); //Use super constructor
  }

  @Nullable
  @Override
  public Object parse(@NotNull CommandManager<?, ?> manager, @NotNull GenericCommandInteractionEvent event, @NotNull String name, @NotNull Parameter param, @NotNull Class<?> type, @NotNull Type generic) {
    try {
      return event.getOption(name, o -> Color.decode(o.getAsString())); //Parse color
    } catch(NumberFormatException e) {
      return null; //Set the parameter to null if the provided color was not valid
    }
  }

  @Override
  public void registerOption(@NotNull de.mineking.discordutils.commands.Command<?> cmd, @NotNull OptionData option, @NotNull Parameter param) {
    //The color will be in the format #rrggbb, so the length of the string is 7
    cmd.addOption(option.setMinLength(7).setMaxLength(7)); //Customize option
  }
}
```

You have to register your option parsers with `CommandManager#registerOptionParser`.

If you want to add subcommands, you can add nested classes to your main command class. These will automatically be used as subcommands:

```java

@ApplicationCommand(name = "config")
public class ConfigCommand {
  @ApplicationCommand(name = "permission") //Will automatically be registered as subcommand
  public static class PermissionCommand {

  }
}
```

Because discord only supports three command layers (command, subcommand group, subcommand), commands with higher levels will not be registered as official subcommands, but instead they will be represented with a '_' delimiter.
For example, a structure like this will be created for subcommands in layer 4 or above:

```md
- config permission list
- config permission add_user
- config permission remove_user
```

These underscores are automatically added by DiscordUtils for the discord representation. However, internally they still behave like layer four commands.

If you want to manually change some configuration of your command like adding an inherited command as subcommand, you can create a method with the `@Setup` annotation.
All methods with this annotation will be called when creating the command, giving you access to the play `Command` object that represents your command:

```java

@ApplicationCommand(name = "test")
public class TestCommand {
  @Setup
  public void setup(Command<?, ?> cmd) {
    cmd.addSubcommand(new InheritedSubcommand());
  }
}
```

In all the above examples, DiscordUtils automatically creates an instance of your command class and executes all methods on that instance.
If you want to execute the command on a dynamic instance, you can use the following registration method:

```java
commandManager.registerCommand(TestCommand.class,
        commandContext->{}, //Return the instnace you want to use based on the command context here
        autocompleteContext->{} //Return the instance you want to use based on the autocomplete context here
        )
```

You can also declare multiple commands in one class like this:

```java
public class TestCommands {
  @ApplicationCommand(name = "a")
  public void performA(shared.CommandContext context) {
    //...
  }

  @ApplicationCommand(name = "b")
  public void performB(shared.CommandContext context) {
    //...
  }
}
```

You can then register the commands bs providing `TestCommands.class` to `registerCommand`. Because the class is not annotated with `@ApplicationCommand`, DiscordUtils will automatically search for methods with the `@ApplicationCommand`
annotation and use these instead.

### Inherited

To create inherited commands, you can create a class that extends `Command`. You then have to create an instance of your command class and register it to the command manager:

```java
public class TestCommand extends Command<shared.CommandContext> {
  public TestCommand() {
    addOption(new OptionData(OptionType.STRING, "test", "description", true));
  }

  @Override
  public void performCommand(shared.CommandContext context) {
    //You have to access the option like you would normally with context.event.getOption(...)
  }
}
```

To register:

```java
commandManager.registerCommand(new TestCommand());
```

For autocomplete, you can use `AutocompleteOption`:

```java
public class TestCommand extends Command<shared.CommandContext> {
  public TestCommand() {
    addOption(new AutocompleteOption(OptionType.STRING, "test", "description", true) {
      @Override
      public void handleAutocomplete(shared.AutocompleteContext context) {
        context.event
                .replyChoice(context.event.getFocusedOption().getValue() + "!", context.event.getFocusedOption().getValue() + "!") //Suggest adding an exclamation mark at the end of the current input
                .queue();
      }
    });
  }

  @Override
  public void performCommand(shared.CommandContext context) {
    //You have to access the option like you would normally with context.event.getOption(...)
  }
}
```

To add subcommands, you can use the `addSubcommand` method in the command constructor. You can add both annotated and inherited commands as subcommands.

## Language cache

The language cache manager can be used to access a user's locale from places, where you don't have access to it from Discord's side.
The manager listens to all interactions and caches the user locale field.
The cached values will expire 4 hours after the last access or write to the user's locale value.
For caching, the caffeine library is used.
If you want to use the language cache manager, you have to add this dependency to your project:

```groovy
implementation 'com.github.ben-manes.caffeine:caffeine:3.1.8'
```

## Event Manager

The EventManager allows you to register event handlers in a more simple way. You can either write your own `EventHandler` or use one of the ones brought with DiscordUtils:

- FilteredEventHandler
- ComponentHandler
- ButtonHandler
- StringSelectHandler
- EntitySelectHandler
- ModalHandler
  You can register your handlers with like this:

```java
public class TestBot {
  public static void main(String[] args) {
    new TestBot(args[0]);
  }

  public final JDA jda;
  public final DiscordUtils discordUtils;

  public TestBot(String token) {
    jda = JDABuilder.createDefault(token)
            .build();

    discordUtils = new DiscordUtils(jda, this)
            .useEventManager(eventManager -> {
              eventManager.addEventHandler(
                      new ButtonHandler("test", event -> {
                        /* handle event */
                      })
              );
              //...
            });
  }
}
```

When you enable the EventManager, you can also use `@Listener` in annotated commands:

```java

@ApplicationCommand(name = "test")
public class TestCommand {
  @ApplicationCommandMethod
  public void performCommand(shared.CommandContext context) {
    context.event.replyModal(
            Modal.create("test:modal", "title", TextInputStyle.SHORT)
                    //...
                    .build()
    ).queue();
  }

  @Listener(type = ModalHandler.class, filter = "test:modal") //Handle modals with id "test:modal"
  public void handleModal(ModalInteractionEvent event) {
    //Handle your modal
  }
}
```

## UI Manager

The UIManager allows you to create complex menus without having to manually create event handlers and manage states.
Before you use `useUIManager` you have to call `useEventManager` because the UIManager uses the EventManager internally.
The UIManager requires this additional dependency:

```groovy
implementation 'com.google.code.gson:gson:2.10.1'
```

Example:

```java

@ApplicationCommand(name = "test")
public class TestCommand {
  public final MessageMenu menu;

  public UITestCommand(UIManager manager) {
    menu = manager.createMenu(
            "test",
            state -> new EmbedBuilder()
                    .setTitle("Test Menu")
                    .addField("Text", state.getState("text"), false)
                    .addField("Last user", state.getEvent().map(e -> e.getUser().toString()).orElse("*none*"), false)
                    .build(),
            ComponentRow.of(
                    new ButtonComponent("button", ButtonColor.BLUE, "Append !")
                            .appendHandler(state -> {
                              state.setState("text", current -> current + "!");
                              state.update();
                            }),
                    new ToggleComponent("toggle", state -> state ? ButtonColor.GREEN : ButtonColor.RED, "Toggle")
            )
    ).<Boolean>effect("toggle", value -> System.out.println("Toggle value changed: " + value));
  }

  @ApplicationCommandMethod
  public void performCommand(CommandContext context) {
    menu.createState()
            .setState("text", "abc")
            .setState("toggle", true)
            .display(context.event, false);

  }
}
```

The `state` represents the current state of the menu display. A menu can be displayed multiple times at a time, and all displays are restart-persistent.
As the example shows, the java instance of `Menu` should only be created once, and you can then display the menu multiple times via that one instance.
If you don't need to set an initial state like in the above example, you can also call the `display` method directly on the `Menu` instance.

The state is stored as json in the component ids. This means that the storage space in state is very limited, and you should only persist data in there that really have to be stored.
For example, you should not store large objects but instead an identifier to then load the actual data from a database or something like that:

```java
state.getState("xy id",id->database.getFromId(id));
```

The `effect` method can be used to detect state changes, much like in the react framework in web development. This may be useful if you want to store the value of a state in the database or something similar.

Available Components:

- [ButtonComponent](#buttoncomponent)
- [ToggleComponent](#togglecomponent)
- [MenuComponent](#menucomponent)
- [StringSelectComponent](#stringselectcomponent)
- [EntitySelectComponent](#entityselectcomponent)

### ButtonComponent

The button component is the basic component. It represents a button under the message that you can configure.
The first parameter of all constructors is the name of the component. The name is used internally to identify the component. It has to be unique in one menu to avoid conflicts.
With the multiple available constructors, you can set the color and label of the button, either constant or dependent on the current state.

You can handle a button click by adding a handler. There are `appendHandler` and `prependHandler`. By choosing `append` or `prepend` you can modify the order of execution.
Additionally, DiscordUtils supports detecting double clicks. As soon as you register a double click handler, all normal click events are delayed.
If another click is executed in the next 3 seconds, a double click is triggered; otherwise a normal click event is fired.
You can change the 3-seconds delay with `setDoubleclickTimeout`.

### ToggleComponent

The ToggleComponent is an extension for the button component. It behaves very similarly, but it adds a click handler by default.
The handler simply toggles between two states. The current toggle state is stored in a state with the same name as the component.
You can add additional handlers or listen to state changes with `effect`.

### MenuComponent

The MenuComponent is another extension for the ButtonComponent. When pressed, it displays a different menu. This mechanic can be used to have a menu with multiple frames.
By default, the current state is transferred to the new menu. To change this behavior, you can use `setStateCreator`.

### StringSelectComponent

The StringSelectComponent is a component that allows users to select between multiple options. You can dynamically set these options. You can also set a minimum and a maximum for the options a user has to select.

### EntitySelectComponent

The EntitySelectComponent is similar to StringSelectComponent, but instead of custom options, a user has to select between users, channels or roles. You can specify the entity type in the constructor.

## Help Manager

## List Manager

The ListManager can be used to paginate large amounts of data. It uses the [UIManager](#ui-manager) internally.
