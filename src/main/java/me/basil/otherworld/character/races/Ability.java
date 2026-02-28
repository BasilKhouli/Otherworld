package me.basil.otherworld.character.races;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Ability {

    public final String name;
    public final String description;
    public final String usageGuide;
    public float cooldown;


    protected Ability(String name, String description, String UsageGuide) {
        this.name = name;
        this.description = description;
        this.usageGuide = UsageGuide;
    }

    protected Ability(String name, String description) {
        this.name = name;
        this.description = description;
        this.usageGuide = "No usage guide";
    }

    protected Ability(String name) {

        this.name = name;
        this.description = "";
        this.usageGuide = "No usage guide";
    }

    public abstract void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public abstract void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public abstract void unselected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public abstract void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public abstract void handlePacket(AtomicBoolean stopPacket, boolean out, GamePacketHandler gpHandler, Packet packet, PlayerRef playerRef);


    public abstract Ability clone();

    public Message getAbilityMessage(PlayerRef playerRef){
        Message abilityMessage = Message.raw(name);
        if (cooldown > 0){
            abilityMessage.color(Color.red);
            abilityMessage = Message.join(abilityMessage, Message.raw("("+cooldown+"s)"));

        }
        return abilityMessage;
    }


}
