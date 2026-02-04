package me.basil.otherworld.character.races;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.basil.otherworld.character.races.AbilityManager.registerAbility;

public abstract class Race {
    public final String name;
    private final Map<String,Ability> abilities = new HashMap<>();
    protected final Map<String, Modifier> modifiers;

    public Race(String raceName, List<Ability> raceAbilities, Map<String, Modifier> raceModifiers) {
        this.name = raceName;

        for (Ability ability : raceAbilities) {
            registerAbility(ability);
        }
        modifiers = raceModifiers;
    }

    abstract public Race clone();

    public void registerAbility(Ability ability) {
        String abilityName = null;
        abilityName = ability.name.toLowerCase();
        abilities.put(abilityName, ability);
    }

    pubic Ability getAbility(String abilityName) {
        abilityName = abilityName.toLowerCase();
        if (!(abilities.containsKey(abilityName))) {
            return null;
        }
        return abilities.get(abilityName);
    }

    public Map<String, Ability> getAbilities() {
        return abilities;
    }

}