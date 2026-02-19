package me.basil.otherworld.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.character.races.RaceManager;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class OwRaceListCommand extends AbstractPlayerCommand {

    public OwRaceListCommand() {
        super("list", "List all available races");
        setPermissionGroup(GameMode.Adventure);
        addAliases("l");
    }

    @Override
    protected void execute(@NonNull CommandContext context, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        StringBuilder raceList = new StringBuilder();





        List<Race> races = RaceManager.getRaces();

        if (races.isEmpty()) {
            raceList.append("No races found, if this is unexpected please report!");
        }
        else {
            raceList.append("Races:\n");
            for (int i = 0; i <races.size(); i++) {
                Race race = races.get(i);
                raceList.append(race.getName()).append(" : ").append(race.getDescription());
                if (i+1 < races.size()) {
                    raceList.append("\n");
                }
            }
        }


        context.sendMessage(Message.raw(raceList.toString()));

    }
}
