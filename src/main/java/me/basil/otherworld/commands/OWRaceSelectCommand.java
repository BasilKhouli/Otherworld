package me.basil.otherworld.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.commands.ArgumentType.RaceArgumentType;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class OWRaceSelectCommand extends AbstractPlayerCommand {
    private final RequiredArg<Race> raceArg;

    public OWRaceSelectCommand() {
        super("race", "Choose a race");
        addAliases("r");
        raceArg = withRequiredArg("Race", "Name of the race to choose", new RaceArgumentType());
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        Race selectedRace = raceArg.get(commandContext);
        OtherworldData owd = store.getComponent(ref,OtherworldData.getComponentType());
        if (owd == null) {
            commandContext.sendMessage(Message.raw("An error occurred! Rejoin the server and try again, if the problem persist report to devs").color(Color.red));
            return;
        }
        if (selectedRace == null) {
            return;
        }
        owd.chooseRace(selectedRace.getName(),playerRef);
        commandContext.sendMessage(Message.raw("You selected race: " + selectedRace.getName()));
    }
}
