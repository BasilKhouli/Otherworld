package me.basil.otherworld.character.races.werewolf.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.werewolf.Werewolf;
import me.basil.otherworld.components.OtherworldData;

import java.util.concurrent.atomic.AtomicBoolean;

public class CallCurse extends Ability {

    public CallCurse() {
        super("Call_Curse", "Call upon the curse and take or disable Wolven form",
                """
                        Press Walk[Left Alt] button to swap your current form.
                        drains mana while in werewolf form but during the full moon do the same in take human form.
                        rage does not drain while in the wrong form, revert upon running out of mana.""");
    }


    @Override
    public void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    boolean wasWalking;
    boolean started;
    @Override
    public void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

        MovementStatesComponent movementStatesComponent = store.getComponent(ref,MovementStatesComponent.getComponentType());
        assert movementStatesComponent != null;
        boolean isWalking = movementStatesComponent.getMovementStates().walking;

        if (isWalking) {
            if (!wasWalking) {
                if (cooldown <= 0) {

                    OtherworldData owd = store.getComponent(ref, OtherworldData.getComponentType());
                    assert owd != null;
                    if (!(owd.getRace() instanceof Werewolf werewolfRace)) {
                        return;
                    }
                    werewolfRace.swapForm = !werewolfRace.swapForm;
                    NotificationUtil.sendNotification(playerRef.getPacketHandler(),Message.raw("Swapped"));
                    if (werewolfRace.swapForm) {
                        started = true;
                    }
                }
                else {
                    NotificationUtil.sendNotification(playerRef.getPacketHandler(),getAbilityMessage(playerRef));
                }
            }

        }
        wasWalking = isWalking;
    }

    @Override
    public void unselected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        OtherworldData owd = store.getComponent(ref, OtherworldData.getComponentType());
        assert owd != null;
        if (!(owd.getRace() instanceof Werewolf werewolfRace)) {return;}
        if (started && !werewolfRace.swapForm){
            cooldown+=10;
            started = false;
        }

    }

    @Override
    public void handlePacket(AtomicBoolean stopPacket, boolean out, GamePacketHandler gpHandler, Packet packet, PlayerRef playerRef) {

    }

    @Override
    public Ability clone() {
        return new CallCurse();
    }
}
