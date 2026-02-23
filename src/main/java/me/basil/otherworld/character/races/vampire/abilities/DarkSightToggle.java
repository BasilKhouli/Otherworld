package me.basil.otherworld.character.races.vampire.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.vampire.Vampire;
import me.basil.otherworld.character.races.werewolf.Werewolf;
import me.basil.otherworld.components.OtherworldData;

import java.util.Deque;

public class DarkSightToggle extends Ability {
	public DarkSightToggle() {
        super("Dark_Sight_Toggle","Toggle Dark Sight","press the [Button TBD] to make a light only visible to you.");
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
                if (!(owd.getRace() instanceof Vampire vampireRace)) {return;}
                vampireRace.hasDarkSight = !vampireRace.hasDarkSight;

                EventTitleUtil.hideEventTitleFromPlayer(playerRef, 0);
                String outString = vampireRace.hasDarkSight ? "Enabled" : "Disabled";
                playerRef.sendMessage(Message.raw(outString));
                //EventTitleUtil.showEventTitleToPlayer(playerRef, Message.raw(outString),Message.raw("Dark Vision:"),false,null,2,0,1);

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
        return new DarkSightToggle();
    }
}
