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

import java.util.*;

public class PlayerExclusiveEntity implements Component<EntityStore> {
    public final List<UUID> allowedPlayerUuids = new ArrayList<>();

    public static final BuilderCodec<PlayerExclusiveEntity> CODEC = BuilderCodec.builder(PlayerExclusiveEntity.class,PlayerExclusiveEntity::new)
            .append(new KeyedCodec<>("players", new ArrayCodec<>(Codec.UUID_BINARY, UUID[]::new)  ),
                    (pee,value) -> pee.allowedPlayerUuids.addAll(Arrays.stream(value).toList()),
                    (pee) -> pee.allowedPlayerUuids.toArray(UUID[]::new)
            )
            .add()
            .build();

    public PlayerExclusiveEntity() {}
    public PlayerExclusiveEntity(Collection<UUID> _playerUuids) {
        this.allowedPlayerUuids.addAll(_playerUuids);
    }

    public static ComponentType<EntityStore,PlayerExclusiveEntity> getComponentType(){
        return Main.PEEComponentType;
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        return new PlayerExclusiveEntity();
    }
}
