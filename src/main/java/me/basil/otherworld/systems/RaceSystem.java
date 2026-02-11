package me.basil.otherworld.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RaceSystem extends EntityTickingSystem<EntityStore> {


    @Override
    public void tick(float deltaTime, int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(ref,PlayerRef.getComponentType());
        assert  playerRef != null;
        Player player = store.getComponent(ref,Player.getComponentType());
        assert player != null;
        OtherworldData otherworldData = store.getComponent(ref,OtherworldData.getComponentType());
        assert otherworldData != null;
        if (otherworldData.getRace() == null) {return;}

        if (!otherworldData.isInitalized){
            otherworldData.initializeRace(playerRef);
        }




        int newSelectedSlot = player.getInventory().getActiveHotbarSlot();

        Ability newSelectedSkill = otherworldData.getAbility(newSelectedSlot);

        if (otherworldData.selectedAbility != newSelectedSkill){

            if (otherworldData.selectedAbility != null){
                otherworldData.selectedAbility.unselected(ref,playerRef,store,commandBuffer);
            }
            if (newSelectedSkill != null){
                NotificationUtil.sendNotification(playerRef.getPacketHandler(), Message.raw("["+ newSelectedSlot +"]" +"Selected :"+ newSelectedSkill.name));
                newSelectedSkill.selected(ref,playerRef,store,commandBuffer);
            }
            otherworldData.selectedAbility = newSelectedSkill;
        }

        if (otherworldData.selectedAbility != null){
            otherworldData.selectedAbility.selectedTick(deltaTime,ref,playerRef,store,commandBuffer);
        }
        otherworldData.passiveTick(deltaTime,ref,playerRef,store,commandBuffer); // race passive
        for (int i = 0 ; i < 9; i++){ // skill ticks
            Ability ability = otherworldData.getAbility(i);
            if (ability != null){
                if (ability == otherworldData.selectedAbility){
                    ability.selectedTick(deltaTime,ref,playerRef,store,commandBuffer);
                }
                else{
                    ability.passiveTick(deltaTime, ref, playerRef, store, commandBuffer);
                }

            }
        }


        otherworldData.selectedSlot = newSelectedSlot;


        otherworldData.passiveTick(deltaTime,ref,playerRef,store,commandBuffer);

    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(OtherworldData.getComponentType());//Integrates on all players with the OtherWorldData.Component
    }
}
