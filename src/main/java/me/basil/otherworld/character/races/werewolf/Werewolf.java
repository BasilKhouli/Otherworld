package me.basil.otherworld.character.races.werewolf;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.utils.TimeOfDayUtil;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class Werewolf extends Race {

    public Werewolf() {
        super("Werewolf", "A human cursed to transform into a beast under the full moon", new ArrayList<>(), new Ability[9]);
    }

    private static final String passiveID = "Werewolf_Passive_Effect";
    private static final String curseID = "Werewolf_Curse_Effect";
    public boolean forceCurse = false;
    public boolean curseActive = false;

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
        EffectControllerComponent  effectControllerComponent = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectControllerComponent != null;


        addEffect(passiveID,ref,commandBuffer,effectControllerComponent);

        boolean isFullMoon = timeResource.getMoonPhase() == 0 && !TimeOfDayUtil.isDayTime(store);
        if (isFullMoon || forceCurse) {
            if (!curseActive) {
                curseActive = true;
                addEffect(curseID,ref,commandBuffer,effectControllerComponent);
                playerRef.sendMessage(Message.raw("What a horrible night to have a curse"));
            }


        }
        else{
            if (curseActive) {
                playerRef.sendMessage(Message.raw("The curse fades"));
                curseActive = false;
                removeEffect(curseID,ref,commandBuffer,effectControllerComponent);
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
    public void removed(PlayerRef playerRef, ComponentAccessor<EntityStore> componentAccessor) {
        super.removed(playerRef, componentAccessor);

    }

    @Override
    public Race clone() {
        return  new Werewolf();
    }
}
