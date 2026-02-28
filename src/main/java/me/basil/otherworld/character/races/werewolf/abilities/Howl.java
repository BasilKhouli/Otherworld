package me.basil.otherworld.character.races.werewolf.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.character.races.werewolf.Werewolf;
import me.basil.otherworld.components.OtherworldData;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Howl extends Ability {
    public Howl(){
        super("Howl","Unleash a powerful roar to knockback and fear enemies","Press Walk[Left Alt] button");

    }

    @Override
    public void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    boolean wasWalking;
    @Override
    public void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent movementStatesComponent = store.getComponent(ref,MovementStatesComponent.getComponentType());
        assert movementStatesComponent != null;
        TransformComponent transformComponent = store.getComponent(ref,TransformComponent.getComponentType());
        assert transformComponent != null;
        boolean isWalking = movementStatesComponent.getMovementStates().walking;
        var position = transformComponent.getPosition().clone();

        if (isWalking) {
            if (!wasWalking) {
                if (cooldown <= 0){
                    cooldown += 15f;
                    SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(EntityModule.get().getEntitySpatialResourceType());
                    ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
                    final float range = 10;
                    spatialResource.getSpatialStructure().collect(position, range, results);

                    for (Ref<EntityStore> entityRef : results){
                        if (entityRef == ref){ // skip effecting self ofc
                            continue;
                        }
                        EffectControllerComponent ecc = store.getComponent(entityRef, EffectControllerComponent.getComponentType());
                        NPCEntity npc = store.getComponent(entityRef, Objects.requireNonNull(EntityModule.get().getComponentType(NPCEntity.class)));
                        PlayerRef targetPlayer = store.getComponent(entityRef,PlayerRef.getComponentType());

                        Vector3d entityPos = store.getComponent(entityRef, TransformComponent.getComponentType()).getPosition();

                        Vector3d posDiff = entityPos.clone().subtract(position);
                        double distance = posDiff.length();
                        Vector3d knockbackDirection = posDiff.normalize();



                        float maxKnockbackStrength = 30.0f;
                        float maxUpwardsForce = 1;
                        float maxDistance = 10.0f;

                        float distanceRatio = (float) (1f -(distance / maxDistance));
                        float knockbackStrength = maxKnockbackStrength * distanceRatio;

                        float upwardForce = maxUpwardsForce * distanceRatio;


                        Vector3d knockbackForce = knockbackDirection;
                        knockbackForce.y += upwardForce;
                        knockbackForce.normalize().setLength(knockbackStrength);


                        KnockbackComponent knockbackComponent = new KnockbackComponent();
                        knockbackComponent.setVelocity(knockbackForce);

                        commandBuffer.addComponent(entityRef, KnockbackComponent.getComponentType(), knockbackComponent);

                        if (ecc != null){
                            if (npc != null){//Is NPC
                                NPCPlugin npcPlugin = NPCPlugin.get();
                                Role role = npc.getRole();
                                int roleIndex = npc.getRoleIndex();

                                //Apply (fear somehow)
                                continue;
                            }
                            else if (targetPlayer != null){//Is player
                                OtherworldData targetOWD = store.getComponent(entityRef,OtherworldData.getComponentType());
                                if (targetOWD != null){
                                    Race race =targetOWD.getRace();
                                    if (race instanceof Werewolf){
                                        playerRef.sendMessage(Message.raw("Empower werewolf "+targetPlayer.getUsername()));
                                        //Empower fellow werewolf player
                                        continue;
                                    }
                                }
                                //Disorient non-werewolf player.
                                playerRef.sendMessage(Message.raw("Hit Player "+targetPlayer.getUsername()));

                            }
                            //Code for non player or npc entities with an ECC
                            continue;
                        }
                        //Code for all entities without ECC
                    }
                }else {
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
    }

    @Override
    public void handlePacket(AtomicBoolean stopPacket, boolean out, GamePacketHandler gpHandler, Packet packet, PlayerRef playerRef) {

    }

    @Override
    public Ability clone() {
        return new Howl();
    }
}
