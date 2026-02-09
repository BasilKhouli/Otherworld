package me.basil.otherworld.components;

import au.ellie.hyui.builders.HudBuilder;
import au.ellie.hyui.builders.HyUIHud;
import au.ellie.hyui.builders.LabelBuilder;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
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

public class OtherworldData implements Component<EntityStore> {

    private Race race;
    private final Ability[] equippedAbilities = new Ability[9];
    private final List<Ability> abilityPool = new ArrayList<Ability>();

    public int selectedSlot;
    public Ability selectedAbility;
    public HyUIHud hud;

    public static final BuilderCodec<OtherworldData> CODEC = BuilderCodec.builder(OtherworldData.class,OtherworldData::new).build(); //this will be built to store persistent per player data

    public void chooseRace(String raceName,PlayerRef playerRef){

        Race oldRace = race;
        race = RaceManager.getRace(raceName);

        if (oldRace != null){
            race.removed(playerRef);
        }

        if(race != null){
            race.initialize(playerRef);
            for (int i = 0;i < equippedAbilities.length;i++){
                if (race.defaultEquippedAbilities[i] != null){
                    addAbility(race.defaultEquippedAbilities[i].name,i);
                }

            }
        }

        hud = HudBuilder.hudForPlayer(playerRef).loadHtml("Huds/SkillList.html").withRefreshRate(500)
                .onRefresh(hud ->{
                    for  (int i = 0;i < equippedAbilities.length;i++){

                        String labelText = "Empty Slot";
                        final  Ability ability = equippedAbilities[i];
                        if (ability != null){
                            labelText = ability.name;
                        }

                        final String finalLabelText = labelText;
                        boolean test = hud.getById("Ability"+i, LabelBuilder.class).isPresent();
                        hud.getById("Ability"+i, LabelBuilder.class).ifPresent(
                                label-> label.withText(finalLabelText)
                        );
                        hud.getById("RaceName",LabelBuilder.class).ifPresent(
                                labelBuilder -> labelBuilder.withText(race.getName())
                        );



                    }

                }).show();


    }

    public void addAbility(String abilityName, int slot){

        Ability ability = race.getAbility(abilityName);

        Ability oldAbility = equippedAbilities[slot];

        if (oldAbility != null){
            //call unequipped

            if (Arrays.stream(equippedAbilities).noneMatch((Ability eAbility)->{ return eAbility!=null && eAbility.name.equals(oldAbility.name); })) {//clear pool
                abilityPool.remove(oldAbility);
            }
        }


        if (ability != null){
            if (abilityPool.stream().anyMatch((Ability poolAbility)-> poolAbility.name.equals(abilityName))){
                ability = abilityPool.stream().filter((Ability poolAbility)->{ return poolAbility.name.equals(abilityName);}).findFirst().orElse(ability);
            }
            else abilityPool.add(ability);

            //call equipped here
        }

        equippedAbilities[slot] = ability != null ? ability.clone() : ability ;



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

    public List<Ability> getAbilityPool(){
        return abilityPool;
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

    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        race.passiveTick(deltaTime,ref,playerRef,store,commandBuffer);
        //TODO: will call ability passives here too
    }

}
