package me.basil.otherworld.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.components.CleanUpComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CleanUpSystem extends EntityTickingSystem<EntityStore> {


    @Override
    public void tick(float deltaTime, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {//Despawns 2 ticks after secondsTillDespawn hits 0
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        CleanUpComponent cuc = store.getComponent(ref,CleanUpComponent.getComponentType());
        assert cuc != null;

        boolean timerOut = cuc.secondsTillDespawn<=0;

        if (timerOut && cuc.bufferTick) {
            commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
            return;
        }
        if (timerOut) {
            cuc.bufferTick = true;
            return;
        }
        cuc.secondsTillDespawn -= deltaTime;
        cuc.bufferTick = false;
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return CleanUpComponent.getComponentType();
    }
}
