package me.basil.otherworld.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.Main;
import me.basil.otherworld.components.InteractionOverwriterComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class InteractionOverwriterSystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float deltaTime, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        InteractionOverwriterComponent interactionOverwriterComponent = store.getComponent(ref, InteractionOverwriterComponent.getComponentType());
        assert interactionOverwriterComponent != null;

        boolean unarmed = isUnarmed(ref,commandBuffer);

        if (unarmed == interactionOverwriterComponent.wasArmed){
            interactionOverwriterComponent.updated = false;
        }

        if (!interactionOverwriterComponent.updated){
            Interactions interactions = commandBuffer.ensureAndGetComponent(ref,Interactions.getComponentType());
            if (unarmed){
                for (var entry : interactionOverwriterComponent.getUnarmedInteractions().entrySet()){
                    interactions.setInteractionId(entry.getKey(), entry.getValue());
                }
            }

            for (var entry : interactionOverwriterComponent.getInteractions().entrySet()){
                interactions.setInteractionId(entry.getKey(), entry.getValue());
            }
        }

        interactionOverwriterComponent.wasArmed = unarmed;
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(InteractionOverwriterComponent.getComponentType(),Player.getComponentType());
    }

    private boolean isUnarmed(@Nonnull Ref<EntityStore> playerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Player player = componentAccessor.getComponent(playerRef, Player.getComponentType());
        if (player == null) return true;

        Inventory inventory = player.getInventory();

        inventory.getActiveHotbarItem();
        return inventory.getActiveHotbarItem() == null;
    }

}
