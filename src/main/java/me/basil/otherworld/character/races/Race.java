package me.basil.otherworld.character.races;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.*;

public abstract class Race {
    private final String name; // technically better practice for this to be private with a getter so changed it to that but does not matter IG
    private final Map<String,Ability> abilities = new HashMap<>();
    private final Map<String, Modifier> modifiers;
    public List<String> appliedEffectIDs = new ArrayList<>();

    public Ability[] defaultEquippedAbilities;


    public Race(String raceName, List<Ability> raceAbilities, Map<String, Modifier> raceModifiers, Ability[] raceDefaultEquippedAbilities) {
        name = raceName;

        for (Ability ability : raceAbilities) {
            registerAbility(ability);
        }
        modifiers = raceModifiers;
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
        for (String effectID : appliedEffectIDs.stream().toList()){
            removeEffect(effectID,ref,componentAccessor,effectController);
        }
    }

    protected void addEffect(String effectID, Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor, EffectControllerComponent effectController ){
        int effectIndex = EntityEffect.getAssetMap().getIndex(effectID);
        EntityEffect effect = EntityEffect.getAssetMap().getAsset(effectIndex);
        if (effect != null && !appliedEffectIDs.contains(effectID)) {
            effectController.addEffect(ref, effect, componentAccessor);
            appliedEffectIDs.add(effectID);
        }
    }

    protected void removeEffect(String effectID,Ref<EntityStore> ref,ComponentAccessor<EntityStore> componentAccessor, EffectControllerComponent effectController){
        int passiveIndex = EntityEffect.getAssetMap().getIndex(effectID);
        if (passiveIndex != Integer.MIN_VALUE || appliedEffectIDs.contains(effectID)) {
            effectController.removeEffect(ref, passiveIndex, componentAccessor);
            appliedEffectIDs.remove(effectID);
        }
    }

    //can make an empty method instead of abstract is wanted
    public abstract void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer);
}