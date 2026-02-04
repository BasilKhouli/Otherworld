package me.basil.otherworld.character.races;


import java.util.HashMap;
import java.util.Map;

public class RaceManager {

    public static Map<String,Race> races  = new HashMap<>();

    public static void registerRace(Race race){
        races.put(races.name,race);
    }


}
