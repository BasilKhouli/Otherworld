package me.basil.otherworld;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class DebugOtherWorldDataCommand extends AbstractPlayerCommand {


    public DebugOtherWorldDataCommand() {
        super("otherworld", "prints info about otherworldData");

    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        OtherworldData owData = store.getComponent(ref, OtherworldData.getComponentType());
        if (owData == null || owData.getRace() == null){
            playerRef.sendMessage(Message.raw("Hytalian (No Race Selected)"));
            return;
        }

        playerRef.sendMessage(Message.raw(owData.getRace().getName()));
    }


}