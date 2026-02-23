package me.basil.otherworld.character.races.werewolf.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.werewolf.Werewolf;
import me.basil.otherworld.components.OtherworldData;

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
    @Override
    public void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent movementStatesComponent = store.getComponent(ref,MovementStatesComponent.getComponentType());
        assert movementStatesComponent != null;
        boolean isWalking = movementStatesComponent.getMovementStates().walking;

        if (isWalking) {
            if (!wasWalking) {
                OtherworldData owd = store.getComponent(ref, OtherworldData.getComponentType());
                assert owd != null;
                if (!(owd.getRace() instanceof Werewolf werewolfRace)) {return;}
                werewolfRace.swapForm = !werewolfRace.swapForm;
                playerRef.sendMessage(Message.raw("Swapped"));
            }

        }
        wasWalking = isWalking;
    }

    @Override
    public void unselected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public Ability clone() {
        return new CallCurse();
    }
}
