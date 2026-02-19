package me.basil.otherworld.character.races;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public abstract class Ability {

    public final String name;
    public final String description;
    public final String usageGuide;


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

    public void equipped(PlayerRef playerRef) {

    }

    public void unequipped(PlayerRef playerRef) {

    }

    public abstract void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public abstract void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public abstract void unselected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);
    public  abstract void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer);//called when not selected but equipped



    public abstract Ability clone();

}
