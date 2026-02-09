package me.basil.otherworld.character.races;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public abstract class Ability {

    public final String name;
    public final String description;
    public final ItemStack repItem;//Represents this in UI


    protected Ability(String name, String description, ItemStack repItem) {
        this.name = name;
        this.description = description;
        this.repItem = repItem;
    }

    protected Ability(String name) {

        this.name = name;
        this.description = name + " Ability";
        this.repItem = new ItemStack("Soil_Dirt");
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
