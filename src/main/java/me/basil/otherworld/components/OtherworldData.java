package me.basil.otherworld.components;

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
import java.util.Arrays;
import java.util.List;

public class OtherworldData implements Component<EntityStore> {

    private Race race;
    private final Ability[] equippedAbilities = new Ability[9];
    private final List<Ability> abilityPool = new ArrayList<Ability>();

    public int selectedSlot;
    public Ability selectedAbility;

    public static final BuilderCodec<OtherworldData> CODEC = BuilderCodec.builder(OtherworldData.class,OtherworldData::new).build(); //this will be built to store persistent per player data

    public void chooseRace(String raceName,PlayerRef playerRef){

        Race oldRace = race;
        race = RaceManager.getRace(raceName);

        if (oldRace != null){
            race.removed(playerRef);
        }

        if(race != null){
            race.initialize(playerRef);
            for (int i = 0;i < equippedAbilities.length;i++){
                if (race.defaultEquippedAbilities[i] != null){
                    addAbility(race.defaultEquippedAbilities[i].name,i);
                }

            }
        }



    }

    public void addAbility(String abilityName, int slot){

        Ability ability = race.getAbility(abilityName).clone();
        if (ability == null){return;}

        Ability oldAbility = equippedAbilities[slot];
        //call unequipped

        if (abilityPool.stream().anyMatch((Ability poolAbility)->{ return poolAbility.name.equals(abilityName);})){
            ability = abilityPool.stream().filter((Ability poolAbility)->{ return poolAbility.name.equals(abilityName);}).findFirst().orElse(ability);
        }
        else abilityPool.add(ability);
        equippedAbilities[slot] = ability;

        //call equipped here

        if (Arrays.stream(equippedAbilities).noneMatch((Ability eAbility)->{ return eAbility.name.equals(oldAbility.name); })) {//clear pool
            abilityPool.remove(oldAbility);
        }

    }

    public Ability getAbility(int slot){
        if (slot < 0 || slot >= equippedAbilities.length){ return null; }

        return equippedAbilities[slot];

    }

    public List<Ability> getAbilityPool(){
        return abilityPool;
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
