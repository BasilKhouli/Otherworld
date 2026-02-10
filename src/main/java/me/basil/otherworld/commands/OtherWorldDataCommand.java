package me.basil.otherworld.commands;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.ui.ItemGridSlot;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class OtherWorldDataCommand extends AbstractPlayerCommand {


    public OtherWorldDataCommand() {
        super("otherworld", "prints info about otherworldData");

    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        OtherworldData owData = store.getComponent(ref, OtherworldData.getComponentType());
        if (owData == null || owData.getRace() == null){
            playerRef.sendMessage(Message.raw("Hytalian (No Race Selected)"));
            return;
        }
    }


}