package me.basil.otherworld.character.races;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.logging.Level;

public abstract class Race {
    private final String name; // technically better practice for this to be private with a getter so changed it to that but does not matter IG
    private final String description;
    private final Map<String,Ability> abilities = new HashMap<>();
    private final Map<String,Integer> appliedEffectIDs = new HashMap<>();
    protected final Map<String,Runnable> removalCallbacks = new HashMap<>();

    public Ability[] defaultEquippedAbilities;

    public Race(String raceName, String raceDescription, List<Ability> raceAbilities, Ability[] raceDefaultEquippedAbilities) {
        name = raceName;
        description = raceDescription;

        for (Ability ability : raceAbilities) {
            registerAbility(ability);
        }
                defaultEquippedAbilities = raceDefaultEquippedAbilities;
        if (defaultEquippedAbilities == null) {
            defaultEquippedAbilities = new Ability[9];
            List<Ability> listOfAbilities = abilities.values().stream().toList();
            int abilitiesPossibleToEquip = Math.min(listOfAbilities.size(), defaultEquippedAbilities.length);
            for (int i = 0; i < abilitiesPossibleToEquip; i++) {
                defaultEquippedAbilities[i] = listOfAbilities.get(i);
            }
        }

    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    abstract public Race clone();

    public void registerAbility(Ability ability) {
        String abilityName = null;
        abilityName = ability.name.toLowerCase();
        abilities.put(abilityName, ability);
    }

    public Ability getAbility(String abilityName) {
        if (abilityName == null) {return null;}
        Ability ability = abilities.getOrDefault(abilityName.toLowerCase(),null);

        return ability!= null ? ability.clone() : null ;
    }

    public Map<String, Ability> getAbilities() {
        return abilities;
    }




    public void initialize(PlayerRef playerRef,ComponentAccessor<EntityStore> componentAccessor){ //basically every class will have modifiers so set them now
    }

    public void removed(PlayerRef playerRef,ComponentAccessor<EntityStore> componentAccessor){
        Ref<EntityStore> ref = playerRef.getReference();
        assert ref != null;
        EffectControllerComponent effectController = componentAccessor.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectController != null;
        for (Map.Entry<String, Integer> entry : appliedEffectIDs.entrySet().stream().toList()){
            removeEffect(entry.getKey(),ref,componentAccessor,effectController);
        }
        appliedEffectIDs.clear();
        for (Map.Entry<String, Runnable> entry : removalCallbacks.entrySet()){
            entry.getValue().run();
        }
        removalCallbacks.clear();

    }

    protected void  addEffect(String appliedListID,String effectID, Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor, EffectControllerComponent effectController ){
        var effectIndex = EntityEffect.getAssetMap().getIndex(effectID);
        addEffect(appliedListID,effectIndex,ref,componentAccessor,effectController);
    }

    protected void addEffect(String appliedListID,int effectIndex, Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor, EffectControllerComponent effectController ){

        EntityEffect effect = EntityEffect.getAssetMap().getAsset(effectIndex);
        if (effect != null) {

            Integer existingIndex = appliedEffectIDs.getOrDefault(appliedListID,null);
            if (existingIndex!=null) {

                if(existingIndex == effectIndex && hasEffect(effectIndex,effectController)){
                    return;
                }
                else  {
                    removeEffect(appliedListID,ref,componentAccessor,effectController);
                }
            }



            effectController.addEffect(ref, effect, componentAccessor);
            appliedEffectIDs.put(appliedListID,effectIndex);
        }
        else {
            HytaleLogger.forEnclosingClass().at(Level.WARNING).log("Effect Index "+ effectIndex +" not found while adding appliedListID: " + appliedListID);
        }


    }

    protected void removeEffect(String appliedListID,Ref<EntityStore> ref,ComponentAccessor<EntityStore> componentAccessor, EffectControllerComponent effectController){
        Integer effectID = appliedEffectIDs.getOrDefault(appliedListID,null);
        if (effectID != null){
            effectController.removeEffect(ref, effectID, componentAccessor);
            appliedEffectIDs.remove(appliedListID,effectID);
        }
        else {
            HytaleLogger.forEnclosingClass().at(Level.WARNING).log("Applied effect: " +appliedListID+" not found to remove");
        }
    }

    private boolean hasEffect(int effectIndex, EffectControllerComponent effectController){
        return Arrays.stream(effectController.getActiveEffectIndexes()).anyMatch(index -> index == effectIndex);
    }



    //can make an empty method instead of abstract is wanted
    public abstract void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer);
}