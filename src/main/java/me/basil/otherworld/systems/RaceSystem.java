package me.basil.otherworld.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.components.OtherworldData;
import me.basil.otherworld.ui.huds.SkillList;
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
        if (!otherworldData.isInitialized){
            otherworldData.initializeRace(playerRef,commandBuffer);
        }

        //Universal logic here

        if (otherworldData.getRace() == null) {
            //Human logic here
            return;
        }

        int newSelectedSlot = player.getInventory().getActiveHotbarSlot();

        Ability newSelectedSkill = otherworldData.getAbility(newSelectedSlot);

        if (otherworldData.selectedAbility != newSelectedSkill){

            if (otherworldData.selectedAbility != null){
                otherworldData.selectedAbility.unselected(ref,playerRef,store,commandBuffer);
            }
            if (newSelectedSkill != null){
                Message notifMessage = Message.join(Message.raw("["+ newSelectedSlot +"]" +"Selected: "),newSelectedSkill.getAbilityMessage(playerRef));
                //NotificationUtil.sendNotification(playerRef.getPacketHandler(), notifMessage);
                newSelectedSkill.selected(ref,playerRef,store,commandBuffer);
            }
            otherworldData.selectedAbility = newSelectedSkill;
        }
        otherworldData.selectedSlot = newSelectedSlot;

        otherworldData.tick(deltaTime,ref,playerRef,store,commandBuffer);


        HudManager hudManager = player.getHudManager();
        CustomUIHud customHud = hudManager.getCustomHud();
        SkillList skillList = null;
        if (!(customHud instanceof SkillList)){
            skillList = new SkillList(playerRef);
            hudManager.setCustomHud(playerRef,skillList);
        }
        else {
            skillList  = (SkillList)customHud;
        }
        skillList.updateDisplay(otherworldData);

    }



    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Query.and(PlayerRef.getComponentType(),OtherworldData.getComponentType());//Integrates on all players with the OtherWorldData.Component
    }
}
