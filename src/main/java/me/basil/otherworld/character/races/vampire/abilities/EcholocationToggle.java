package me.basil.otherworld.character.races.vampire.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.vampire.Vampire;
import me.basil.otherworld.components.OtherworldData;

import java.util.Deque;

public class EcholocationToggle extends Ability {
	public EcholocationToggle() {
        super("Echolocation_Toggle","Toggle Echolocation","press the USE key to toggle echolocation");
    }

    @Override
    public void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        OtherworldData owd = store.getComponent(ref,OtherworldData.getComponentType());
        assert owd != null;
        if (!(owd.getRace() instanceof Vampire vampireRace)){
            return;
        }


        PacketHandler ph = playerRef.getPacketHandler();
        if (ph instanceof GamePacketHandler gph){

            Deque<SyncInteractionChain> packets = gph.getInteractionPacketQueue();
            for (SyncInteractionChain packet : packets) {//Mostly for debug all will be replaced with proper ways to set later
                if (packet.interactionType == InteractionType.Use && packet.initial){
                    vampireRace.hasEcholocation = !vampireRace.hasEcholocation;

                    EventTitleUtil.hideEventTitleFromPlayer(playerRef, 0);
                    String outString = vampireRace.hasEcholocation ? "Enabled" : "Disabled";
                    playerRef.sendMessage(Message.raw(outString));
                    //EventTitleUtil.showEventTitleToPlayer(playerRef, Message.raw(outString),Message.raw("Dark Vision:"),false,null,2,0,1);

                    break;
                }
            }


        }

    }

    @Override
    public void unselected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public Ability clone() {
        return new EcholocationToggle();
    }
}
