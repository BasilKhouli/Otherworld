package me.basil.otherworld.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.RegeneratingValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;

public class ManaRegenSystem extends EntityTickingSystem<EntityStore> {
    @Override
    public void tick(float dt, int i, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        PlayerRef playerRef = store.getComponent(ref,PlayerRef.getComponentType());
        assert playerRef != null;
        Player player = store.getComponent(ref,Player.getComponentType());
        assert player != null;
        if (player.getGameMode() ==  GameMode.Creative) return;

        Instant currentTime = store.getResource(TimeResource.getResourceType()).getNow();

        EntityStatMap statMap = store.getComponent(ref,EntityStatMap.getComponentType());
        assert statMap != null;

        EntityStatValue manaValue = statMap.get(DefaultEntityStatTypes.getMana());

        assert manaValue != null;
        /*

        RegeneratingValue[] regenValues = manaValue.getRegeneratingValues();
        float totalAdded = 0;
        float total = 0;
        if  (regenValues != null){
            for (RegeneratingValue regenValue : regenValues) {
                float amount = regenValue.regenerate(commandBuffer,ref,currentTime,dt,manaValue,manaValue.get());
                if  (amount > 0.0F) {
                    totalAdded += amount;

                }
                total += amount;

            }

        }
        statMap.subtractStatValue(DefaultEntityStatTypes.getMana(),totalAdded);
        if (total > 0.0F) {
            statMap.addStatValue(DefaultEntityStatTypes.getMana(),0.5f);
        }
         */




    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(PlayerRef.getComponentType(),EntityStatMap.getComponentType());
    }
}
