package me.basil.otherworld.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.Main;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class CleanUpComponent implements Component<EntityStore> {
    public float secondsTillDespawn = 1f;
    public boolean bufferTick = false;
    public static final BuilderCodec<CleanUpComponent> CODEC = BuilderCodec.builder(CleanUpComponent.class,CleanUpComponent::new)
            .append(new KeyedCodec<>("SecondsTillDespawn", BuilderCodec.FLOAT),
                    (cuc,value) -> cuc.secondsTillDespawn = value,
                    (cuc) -> cuc.secondsTillDespawn
            )
            .add()
            .build();

    public static ComponentType<EntityStore,CleanUpComponent> getComponentType() {
        return Main.CUCComponentType;
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new  CleanUpComponent();
    }
}
