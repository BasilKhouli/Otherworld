package me.basil.otherworld.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.components.PlayerExclusiveEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;

public class HiddenEntitiesSystem extends EntityTickingSystem<EntityStore> {


    @Override
    public void tick(float deltaTime, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = commandBuffer.getComponent(ref,PlayerRef.getComponentType());
        assert playerRef != null;
        EntityTrackerSystems.EntityViewer entityViewer = commandBuffer.getComponent(ref, EntityTrackerSystems.EntityViewer.getComponentType());
        assert entityViewer != null;
        Iterator<Ref<EntityStore>> iterator = entityViewer.visible.iterator();

        while (iterator.hasNext()) {
            Ref<EntityStore> eRef = iterator.next();
            PlayerExclusiveEntity peeComponent =commandBuffer.getComponent(eRef,PlayerExclusiveEntity.getComponentType());
            if (peeComponent != null && !peeComponent.allowedPlayerUuids.contains(playerRef.getUuid())) {
                entityViewer.hiddenCount++;
                iterator.remove();
            }

        }
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(EntityTrackerSystems.EntityViewer.getComponentType(), PlayerRef.getComponentType());
    }
}
