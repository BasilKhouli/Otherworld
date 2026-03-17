package me.basil.otherworld.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.Main;
import me.basil.otherworld.components.InteractionOverwriterComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class InteractionOverwriterSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float deltaTime, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        InteractionOverwriterComponent interactionOverwriterComponent = store.getComponent(ref, InteractionOverwriterComponent.getComponentType());
        assert interactionOverwriterComponent != null;

        Interactions interactions = commandBuffer.ensureAndGetComponent(ref,Interactions.getComponentType());

        //TODO check if unarmed, if so, use unarmed interaction overrides then apply interaction other overrides

    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Main.getComponentType(InteractionOverwriterComponent.class);
    }
}
