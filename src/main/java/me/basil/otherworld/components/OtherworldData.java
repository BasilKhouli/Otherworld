package me.basil.otherworld.components;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class OtherworldData implements Component<EntityStore> {

    private Race race;
    private final Ability[] equippedAbilities = new Ability[9];
    private final List<Ability> abilityPool = new ArrayList<>();


    public int selectedSlot;
    public Ability selectedAbility;


    public boolean isInitialized = false;
    private boolean abilitiesInitialized = false;
    Race previousRaceBuffer; // Null when race is initialized properly


    public static final BuilderCodec<OtherworldData> CODEC = BuilderCodec.builder(OtherworldData.class,OtherworldData::new)
            .append(new KeyedCodec<>("Race", BuilderCodec.STRING),
                    (owd,value) -> owd.chooseRace(value),
                    (owd) -> owd.getRaceName()
                    )
            .add()
            .append(new KeyedCodec<>("Abilities", BuilderCodec.STRING_ARRAY),
                    (owd,value) -> owd.setEquippedAbilities(value),
                    (owd) -> owd.getEquippedAbilityNames()
            )
            .add()
            .build();




    public void chooseRace(String raceName){
        if (previousRaceBuffer == null){
            previousRaceBuffer = race;
        }
        race = RaceManager.getRace(raceName);
        isInitialized = false;
        clearAbilities();
        abilitiesInitialized = false;



    }

    public void initializeRace(PlayerRef playerRef,ComponentAccessor<EntityStore> componentAccessor){


        if (previousRaceBuffer != null){
            previousRaceBuffer.removed(playerRef,componentAccessor);
        }

        if (race != null){
            race.initialize(playerRef,componentAccessor);

            if (!abilitiesInitialized){
                setEquippedAbilities(Arrays.stream(race.defaultEquippedAbilities).map((a)-> a != null ? a.name : null).toArray(String[]::new));
                for (int i = 0;i < equippedAbilities.length;i++){
                    if (race.defaultEquippedAbilities[i] != null){
                        addAbility(race.defaultEquippedAbilities[i].name,i);
                    }
                }
            }

        }


        previousRaceBuffer = null;
        isInitialized = true;



    }

    public void addAbility(String abilityName, int slot){
        if (race == null){
            return;
        }

        Ability ability = race.getAbility(abilityName);
        if  (ability != null){
            ability = ability.clone();
        }



        Ability oldAbility = equippedAbilities[slot];

        if (oldAbility != null){
            //call unequipped

            if (Arrays.stream(equippedAbilities).noneMatch((Ability eAbility)-> eAbility!=null && eAbility.name.equals(oldAbility.name))) {//clear pool
                abilityPool.remove(oldAbility);
            }
        }


        if (ability != null){
            if (abilityPool.stream().anyMatch((Ability poolAbility)-> poolAbility.name.equals(abilityName))){
                ability = abilityPool.stream().filter((Ability poolAbility)-> poolAbility.name.equals(abilityName)).findFirst().orElse(ability);
            }
            else {
                abilityPool.add(ability);
            }

            //call equipped here
        }

        equippedAbilities[slot] = ability;

    }

    public void swapAbilities(int slot1, int slot2){
        Ability oldAbility = equippedAbilities[slot1];
        equippedAbilities[slot1] = equippedAbilities[slot2];
        equippedAbilities[slot2] = oldAbility;
    }

    public Ability getAbility(int slot){
        if (slot < 0 || slot >= equippedAbilities.length){ return null; }

        return equippedAbilities[slot];
    }

    public void setEquippedAbilities(String[] _equippedAbilities){
        for  (int i = 0; i < equippedAbilities.length; i++){
            addAbility(_equippedAbilities[i], i);
        }
        if (Arrays.stream(_equippedAbilities).anyMatch(Objects::nonNull)){
            abilitiesInitialized = true;
        }

    }


    public String[] getEquippedAbilityNames(){
        String[] names = new String[equippedAbilities.length];
        for (int i = 0; i < equippedAbilities.length; i++){
            Ability ability = equippedAbilities[i];
            names[i] =  ability != null ?  ability.name : null;
        }
        return names;
    }

    public List<Ability> getAbilityPool(){
        return abilityPool;
    }

    public void clearAbilities(){
        Arrays.fill(equippedAbilities,null);
        abilityPool.clear();
    }
    public  String getRaceName(){
        return race != null ? race.getName() : null;
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

    public void tick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        if (race != null){
            race.passiveTick(deltaTime,ref,playerRef,store,commandBuffer);
            race.applySpeedModifiers(playerRef,ref,commandBuffer);
        }else{
            MovementManager movementManager = commandBuffer.getComponent(ref, MovementManager.getComponentType());
            if (movementManager != null) {
                movementManager.resetDefaultsAndUpdate(ref,commandBuffer)   ;
            }
        }


        for (Ability ability : abilityPool){
            if (ability.cooldown > 0){
                ability.cooldown -= deltaTime;
            }
            ability.passiveTick(deltaTime, ref, playerRef, store, commandBuffer);// ability passives
        }

        if (selectedAbility != null){
            selectedAbility.selectedTick(deltaTime,ref,playerRef,store,commandBuffer);
        }
    }


    public void packetHandling(AtomicBoolean stopPacket, boolean out, GamePacketHandler gpHandler, Packet packet, PlayerRef playerRef) {
        if (race != null){
            race.handlePacket(stopPacket,out,gpHandler,packet,playerRef);
        }
        for (Ability ability : abilityPool){
            ability.handlePacket(stopPacket,out,gpHandler,packet,playerRef);// ability passives
        }
    }
}
