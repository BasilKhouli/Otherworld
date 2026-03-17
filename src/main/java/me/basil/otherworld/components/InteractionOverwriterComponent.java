package me.basil.otherworld.components;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.Main;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class InteractionOverwriterComponent implements Component<EntityStore> {

    public static BuilderCodec<InteractionOverwriterComponent> CODEC = BuilderCodec.builder(InteractionOverwriterComponent.class,InteractionOverwriterComponent::new)
            .build();

    private final Map<InteractionType, String> interactionOverwrites = new HashMap<>();
    private final Map<InteractionType, String> unarmedInteractionOverwrites = new HashMap<>();//

    public void setUnarmedInteractionOverwrite(InteractionType type, String interactionId) {
        unarmedInteractionOverwrites.put(type, interactionId);
    }

    public String getUnarmedInteractionOverwrite(InteractionType type) {
        return unarmedInteractionOverwrites.get(type);
    }

    public void setInteractionOverwrite(InteractionType type, String interactionId) {
        interactionOverwrites.put(type, interactionId);
    }

    public String getInteractionOverwrite(InteractionType type) {
        return interactionOverwrites.get(type);
    }

    public static ComponentType<EntityStore,InteractionOverwriterComponent> getComponentType() {
        return Main.getComponentType(InteractionOverwriterComponent.class);
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new InteractionOverwriterComponent();
    }
}
