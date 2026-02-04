package me.basil.otherworld.character.races;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    public static Map<String,Ability> abilities = new HashMap<>();

    public static void registerAbility(Ability ability) {
        abilities.put(ability.name, ability);
    }



}
