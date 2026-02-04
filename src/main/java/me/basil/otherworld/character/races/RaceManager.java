package me.basil.otherworld.character.races;


import me.basil.otherworld.character.races.fiend.Fiend;
import me.basil.otherworld.character.races.merefolk.Merefolk;
import me.basil.otherworld.character.races.spiritborn.Spiritborn;
import me.basil.otherworld.character.races.vampire.Vampire;
import me.basil.otherworld.character.races.werewolf.Werewolf;

import java.util.HashMap;
import java.util.Map;

public class RaceManager {

    public static Map<String,Race> races  = new HashMap<>();

    public static void registerRace(Race race){
        races.put(races.name,race);
    }

    public static Ability getAbility(Ability ability, String abilityName) {
        return ability.getAbility(abilityName);
    }

    publc static void initialize(){

        registerRace(new Vampire());
        registerRace(new Werewolf());
        registerRace(new Fiend());
        registerRace(new Merefolk());
        registerRace(new Spiritborn());


    }


}
