package me.basil.otherworld.commands;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;


public class OtherworldCommand extends AbstractPlayerCommand {


    public OtherworldCommand() {
        super("otherworld", "Command for all things Otherworld related");
        addAliases("ow");

        addSubCommand(new owAbilitySelectCommand());
        addSubCommand(new OWRaceSelectCommand());
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        OtherworldData owData = store.getComponent(ref, OtherworldData.getComponentType());
        if (owData == null || owData.getRace() == null){
            playerRef.sendMessage(Message.raw("Human (select a race with /otherworld race <race>)"));
            return;
        }
        playerRef.sendMessage(Message.raw("Race:" + owData.getRace().getName()));
    }


}