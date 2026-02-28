package me.basil.otherworld.character.races.werewolf;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.GeneralModifier;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.character.races.werewolf.abilities.CallCurse;
import me.basil.otherworld.character.races.werewolf.abilities.Howl;
import me.basil.otherworld.utils.TimeOfDayUtil;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Werewolf extends Race {

    public Werewolf() {

        List<Ability> raceAbilities = List.of(
                new CallCurse(),
                new Howl()



        );
        super("Werewolf", "A human cursed to transform into a beast under the full moon", raceAbilities, null);
    }

    private static final String Passive_EFFECT_ID = "Werewolf_Passive_Effect";
    private static final String CURSE_EFFECT_ID = "Werewolf_Curse_Effect";
    public boolean forceCurse = false;
    public boolean swapForm = false;
    public boolean curseActive = false;

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
        EffectControllerComponent  effectControllerComponent = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectControllerComponent != null;

        addEffect("Passive", Passive_EFFECT_ID,ref,commandBuffer,effectControllerComponent);


        boolean isFullMoon = timeResource.getMoonPhase() == 0 && !TimeOfDayUtil.isDayTime(store);
        boolean shouldBeWerewolf = (isFullMoon != swapForm) || forceCurse;
        boolean wrongFrom = swapForm && !forceCurse;

        if (wrongFrom){
            EntityStatMap statMap = commandBuffer.getComponent(ref, EntityStatMap.getComponentType());
            assert statMap != null;
            EntityStatValue currentMana = statMap.get(DefaultEntityStatTypes.getMana());
            assert currentMana != null;
            final float manaCostPerSecond = !isFullMoon ? 0.3f : 0.6f;
            final float manaCost = Math.min(1,manaCostPerSecond * deltaTime);//if the server lags hard caps the delta time mod
            final float regenDelay = -0.1f;
            final int ManaRegenDelayIndex = EntityStatType.getAssetMap().getIndex("ManaRegenDelay");

            if (currentMana.get() > manaCost) {
                statMap.subtractStatValue(DefaultEntityStatTypes.getMana(), manaCost);

                EntityStatValue mtdValue = statMap.get(ManaRegenDelayIndex);
                if  (mtdValue == null || mtdValue.get() > regenDelay) {
                    statMap.setStatValue(ManaRegenDelayIndex,regenDelay);
                }
            }
            else {
                swapForm = false;
                EntityStatValue mtdValue = statMap.get(ManaRegenDelayIndex);
                if  (mtdValue == null || mtdValue.get() > regenDelay*100) {
                    statMap.setStatValue(ManaRegenDelayIndex,regenDelay*100);
                }
            }
        }

        if (shouldBeWerewolf) {
            speedModifiers.remove("Passive");

            if (!curseActive) {
                curseActive = true;
                addEffect("Curse", CURSE_EFFECT_ID,ref,commandBuffer,effectControllerComponent);
                speedModifiers.put("Curse",new GeneralModifier(1.20f,true ));
                if (isFullMoon && !swapForm) { //plays on natural full Moon Transformation
                }

            }
        }
        else{
            speedModifiers.put("Passive",new GeneralModifier(0.85f,true ));
            if (curseActive) {
                curseActive = false;
                speedModifiers.remove("Curse");
                removeEffect("Curse",ref,commandBuffer,effectControllerComponent);
            }
        }



        if (curseActive){
            //commandBuffer.removeComponent(ref, PlayerInput.getComponentType());
        }
        else{
            //commandBuffer.ensureAndGetComponent(ref, PlayerInput.getComponentType());
        }




    }

    @Override
    public void handlePacket(AtomicBoolean stopPacket, boolean out, GamePacketHandler gpHandler, Packet packet, PlayerRef playerRef) {

    }

    @Override
    public void removed(PlayerRef playerRef, ComponentAccessor<EntityStore> componentAccessor) {
        super.removed(playerRef, componentAccessor);

    }

    @Override
    public Race clone() {
        return  new Werewolf();
    }
}
