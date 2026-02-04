package me.basil.otherworld.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class OtherworldData implements Component<EntityStore> {

    private Race race;
    private final List<Ability> abilityList = new ArrayList<Ability>();



    @Override
    public @Nonnull Component<EntityStore> clone() {
        return new OtherworldData();
    }
}
