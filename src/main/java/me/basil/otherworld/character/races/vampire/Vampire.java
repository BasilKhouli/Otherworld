package me.basil.otherworld.character.races.vampire;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.stamina.StaminaModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import io.netty.channel.Channel;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.utils.SimpleSunlightDetector;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public class Vampire extends Race {
    public Vampire( ) {

        List<Ability> raceAbilities = List.of(

                //TODO

        );

        Map<String, Modifier> raceModifiers = Map.of(
                //TODO
        );


        super("Vampire", raceAbilities, raceModifiers);
    }




    @Override
    public Race clone() {
        return new Vampire();
    }





    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {



        flightLogic(deltaTime,ref,playerRef,store,commandBuffer);
        burnInSunlight(deltaTime,ref,playerRef,store,commandBuffer);
        darkVisionLogic(deltaTime,ref,playerRef,store,commandBuffer);
        //playerRef.sendMessage(Message.raw(SimpleSunlightDetector.isExposedToSunlight(ref,store) ? "In Sunlight" : "Not in Sunlight"));

    }

    private static final String SUNLIGHT_BURN_EFFECT_ID = "Sunlight_Burn";
    private boolean wasInSunlightLastTick = false;

    private void burnInSunlight(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer){



        Player player = store.getComponent(ref,Player.getComponentType());
        assert player != null;

        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert  effectController != null;


        ItemStack helmet = player.getInventory().getArmor().getItemStack((short) 0);

        boolean isExposedToSunlight = SimpleSunlightDetector.isExposedToSunlight(ref,store) && SimpleSunlightDetector.isDaytime(store) && (helmet == null || helmet.isEmpty()); //SunlightDetector.isExposedToSunlight(ref, store);
        if (isExposedToSunlight) {

            if (!wasInSunlightLastTick) {
                EntityEffect burnEffect = EntityEffect.getAssetMap().getAsset(SUNLIGHT_BURN_EFFECT_ID);

                if (burnEffect != null) {
                    effectController.addEffect(ref, burnEffect, commandBuffer);
                }

                wasInSunlightLastTick = true;
            }

        } else {
            if (wasInSunlightLastTick) {
                int effectIndex = EntityEffect.getAssetMap().getIndex(SUNLIGHT_BURN_EFFECT_ID);

                if (effectIndex != Integer.MIN_VALUE) {
                    effectController.removeEffect(ref, effectIndex, commandBuffer);
                }

                wasInSunlightLastTick = false;
            }
        }

    }

    private boolean isInFlight;
    //private Model playerModel;
    private void flightLogic(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer){
        MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
        assert movementManager != null; // if any asserts throw an exception there's prob something wrong but u can also just replace with an if (==null) {return}
        MovementStatesComponent movementStatesComponent = store.getComponent(ref, MovementStatesComponent.getComponentType());
        assert  movementStatesComponent != null;
        EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
        assert statMap != null;
        int staminaRegenDelayIndex = store.getResource(StaminaModule.get().getSprintRegenDelayResourceType()).getIndex();
        EntityStatValue currentStamina = statMap.get(DefaultEntityStatTypes.getStamina());
        assert currentStamina != null;
        final float staminaDrainRate = 2f; //stamina drained per second while flying
        final float staminaRegenDisableTime = 1f; //time after flying stops before regen can start
        final float thisFramesStaminaDrain = staminaDrainRate * deltaTime;



        if (currentStamina.get() < thisFramesStaminaDrain){
            //force stop flying
            playerRef.getPacketHandler().writeNoCache(new SetMovementStates(new SavedMovementStates(false))); //sends packet to client to stop flying
            movementStatesComponent.getMovementStates().flying = false;
            movementManager.getSettings().canFly = false;
        }
        else {
            movementManager.getSettings().canFly = true;
        }


        if (movementStatesComponent.getMovementStates().flying){

            if (!isInFlight){
                isInFlight = true;
                EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
                assert effectController != null;
                EntityEffect transformEffect = EntityEffect.getAssetMap().getAsset("Vampire_Bat_Transformation");
                if (transformEffect != null){
                    effectController.addEffect(ref, transformEffect, commandBuffer);
                }


                /*
                ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
                assert modelComponent != null;

                ModelAsset batAsset = ModelAsset.getAssetMap().getAsset("Vampire_Bat");
                if (batAsset != null){
                    playerModel = modelComponent.getModel();
                    Model batModel = Model.createScaledModel(batAsset,batAsset.getMinScale());
                    ModelComponent newModelComp = new ModelComponent(batModel);
                    commandBuffer.replaceComponent(ref,ModelComponent.getComponentType(),newModelComp);
                    //playerRef.sendMessage(Message.raw(modelComponent.getModel().toString()));
                }
                */
            }
            //Drains Stamina

            IntPredicate isNotAir = (blockType) -> blockType != 0; //assuming 0 is air block type
            TransformComponent playerTransform = store.getComponent(ref, TransformComponent.getComponentType());
            assert playerTransform != null;
            Velocity playerVelocity = store.getComponent(ref, Velocity.getComponentType());
            assert playerVelocity != null;

            Vector3d playerPosition = playerTransform.getPosition();

            Vector3d groundLocation = TargetUtil.getTargetLocation(store.getExternalData().getWorld(),isNotAir,playerPosition.x,playerPosition.y,playerPosition.z,0d,-1d,0d ,5);

            if (!(groundLocation != null && playerPosition.y - groundLocation.y < 0.5d && playerVelocity.getSpeed() < 0.1d)){ //if player is still and on ground don't take stamina
                statMap.setStatValue(staminaRegenDelayIndex,-staminaRegenDisableTime); //prevents stamina regeneration
                float newStamina = currentStamina.get() - thisFramesStaminaDrain;
                statMap.setStatValue(DefaultEntityStatTypes.getStamina(),newStamina);
            }





        }
        else {
            if (isInFlight){
                isInFlight = false;
                EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
                assert effectController != null;

                int effectIndex = EntityEffect.getAssetMap().getIndex("Vampire_Bat_Transformation");

                if (effectIndex != Integer.MIN_VALUE) {
                    effectController.removeEffect(ref, effectIndex, commandBuffer);
                }
                /*
                if (playerModel != null){
                    commandBuffer.replaceComponent(ref,ModelComponent.getComponentType(),new ModelComponent(playerModel));
                    PlayerSkinComponent playerSkinComponent = store.getComponent(ref, PlayerSkinComponent.getComponentType());
                    if (playerSkinComponent != null) {
                        playerSkinComponent.setNetworkOutdated();
                    }
                    playerModel = null;
                }
                */
            }
        }

        movementManager.update(playerRef.getPacketHandler());
    }

    private void darkVisionLogic(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer){
        //TODO Implement Dark Sight
        PacketHandler handler = playerRef.getPacketHandler();
        Channel channel = handler.getChannel();
        //TODO Add a ChannelOutboundHandlerAdapter to modify outgoing packets to give full bright effect;


    }
}

