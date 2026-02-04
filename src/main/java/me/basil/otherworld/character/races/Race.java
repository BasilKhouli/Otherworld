package me.basil.otherworld.character.races;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.basil.otherworld.character.races.AbilityManager.registerAbility;

public abstract class Race {
    private final String name; // technically better practice for this to be private with a getter so changed it to that but does not matter IG
    private final Map<String,Ability> abilities = new HashMap<>();
    protected final Map<String, Modifier> modifiers;


    public Race(String raceName, List<Ability> raceAbilities, Map<String, Modifier> raceModifiers) {
        this.name = raceName;

        for (Ability ability : raceAbilities) {
            registerAbility(ability);
        }
        modifiers = raceModifiers;
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
        abilityName = abilityName.toLowerCase();
        if (!(abilities.containsKey(abilityName))) {
            return null;
        }
        return abilities.get(abilityName);
    }

    public Map<String, Ability> getAbilities() {
        return abilities;
    }




    public void initalize(PlayerRef playerRef){ //basically every class will have modifiers so set them now
        Ref<EntityStore> ref = playerRef.getReference();
        assert ref != null;
        Store<EntityStore> store = ref.getStore();

        EntityStatMap statMap = store.getComponent(ref,EntityStatMap.getComponentType());
        //TODO Implement later

    }

    public void removed(PlayerRef playerRef){

    }

    //can make an empty method instead of abstract is wanted
    public abstract void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer);
}