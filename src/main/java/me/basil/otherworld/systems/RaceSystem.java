package me.basil.otherworld.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RaceSystem extends EntityTickingSystem<EntityStore> {


    @Override
    public void tick(float deltaTime, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(ref,PlayerRef.getComponentType());
        assert  playerRef != null;
        OtherworldData otherworldData = store.getComponent(ref,OtherworldData.getComponentType());
        assert otherworldData != null;
        if (otherworldData.getRace() == null) {return;}

        otherworldData.passiveTick(deltaTime,ref,playerRef,store,commandBuffer);

    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(OtherworldData.getComponentType());//Integrates on all players with the OtherWorldData.Component
    }
}
