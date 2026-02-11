package me.basil.otherworld.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class OwRaceClearCommand extends AbstractPlayerCommand {
    public OwRaceClearCommand() {
        super("clear","clear your race and become human");
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        OtherworldData owd = store.getComponent(ref,OtherworldData.getComponentType());
        if (owd == null) {
            commandContext.sendMessage(Message.raw("An error occurred! Rejoin the server and try again, if the problem persist report to devs").color(Color.red));
            return;
        }

        owd.chooseRace(null);
        commandContext.sendMessage(Message.raw("The race has been cleared! You are now a human"));
    }
}
