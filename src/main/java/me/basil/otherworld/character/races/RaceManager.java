package me.basil.otherworld.character.races;


import me.basil.otherworld.character.races.fiend.Fiend;
import me.basil.otherworld.character.races.merefolk.Merefolk;
import me.basil.otherworld.character.races.spiritborn.Spiritborn;
import me.basil.otherworld.character.races.vampire.Vampire;
import me.basil.otherworld.character.races.werewolf.Werewolf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceManager { // Implimentation choice: instead of sta

    private static final Map<String,Race> races = new HashMap<>();

    public static void registerRace(Race race){
        races.put(race.getName().toLowerCase(),race);
    }


    public static Race getRace(String raceName){
        if (raceName == null){return null;}
        Race returnRace = races.getOrDefault(raceName.toLowerCase(),null);
        return  returnRace != null ? returnRace.clone() : null;
    }

    public static List<Race> getRaces(){
        return new ArrayList<>(races.values());
    }

    public static void initialize(){

        registerRace(new Vampire());
        //TODO: register these when they are set up properly
        /*
        registerRace(new Werewolf());
        registerRace(new Fiend());
        registerRace(new Merefolk());
        registerRace(new Spiritborn());
        */

    }


}
