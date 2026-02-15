package me.basil.otherworld.character.races.werewolf;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Werewolf extends Race {

    public Werewolf() {
        super("Werewolf", new ArrayList<>(), new HashMap<>(), new Ability[9]);
    }

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        World world = store.getExternalData().getWorld();
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());


    }

    @Override
    public Race clone() {
        return  new Werewolf();
    }
}
