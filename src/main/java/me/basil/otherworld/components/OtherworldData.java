package me.basil.otherworld.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.Main;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.character.races.RaceManager;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class OtherworldData implements Component<EntityStore> {

    private Race race;
    private final List<Ability> abilityList = new ArrayList<Ability>();

    public static final BuilderCodec<OtherworldData> CODEC = BuilderCodec.builder(OtherworldData.class,OtherworldData::new).build(); //this will be built to store persistent per player data

    public void chooseRace(String raceName,PlayerRef playerRef){

        Race oldRace = race;
        race = RaceManager.getRace(raceName);

        if (oldRace != null){
            race.removed(playerRef);
        }

        if(race != null){
            race.initalize(playerRef);
        }



    }

    public Race getRace() {
        return race;
    }

    public static ComponentType<EntityStore,OtherworldData> getComponentType(){
        return Main.OWDcomponentType;
    }

    @Override
    public @Nonnull Component<EntityStore> clone() {
        return new OtherworldData();
    }

    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        race.passiveTick(deltaTime,ref,playerRef,store,commandBuffer);
        //TODO: will call ability passives here too
    }

}
