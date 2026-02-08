package me.basil.otherworld.character.races.vampire.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.vampire.Vampire;
import me.basil.otherworld.components.OtherworldData;

import java.util.Deque;

public class DarkSightToggle extends Ability {
	public DarkSightToggle() {
        super("Dark_Sight_Toggle");
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

                    HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());
                    assert headRotation != null;
                    Vector3f rotation = headRotation.getRotation();
                    float pitch = rotation.x;
                    float normalizedPitch = (pitch + (float)Math.PI/2) / (float)Math.PI;

                    vampireRace.brightnessLevel = (int) (normalizedPitch * 15f);
                    vampireRace.hasDarkVision = vampireRace.brightnessLevel > 3;


                    EventTitleUtil.hideEventTitleFromPlayer(playerRef, 0);
                    String outString = vampireRace.hasDarkVision ? "Enabled" : "Disabled";
                    EventTitleUtil.showEventTitleToPlayer(playerRef, Message.raw(outString),Message.raw("Dark Vision:"),false,null,2,0,1);
                    vampireRace.reloadChunks(playerRef);
                    break;
                }
                //playerRef.sendMessage(Message.raw("Packet: " + packet.getClass().getSimpleName()));
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
        return new DarkSightToggle();
    }
}
