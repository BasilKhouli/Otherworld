package me.basil.otherworld.character.races.vampire;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.character.races.vampire.abilities.DarkSightToggle;
import me.basil.otherworld.character.races.vampire.abilities.Drain;
import me.basil.otherworld.components.CleanUpComponent;
import me.basil.otherworld.components.PlayerExclusiveEntity;
import me.basil.otherworld.utils.SimpleSunlightDetector;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntPredicate;

public class Vampire extends Race {
    public Vampire( ) {

        List<Ability> raceAbilities = List.of(
                new Drain(),
                new DarkSightToggle()


        );


        super("Vampire", "A creature of the night with supernatural powers and a thirst for blood", raceAbilities,null);

    }




    @Override
    public Race clone() {
        return new Vampire();
    }





    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        World world = store.getExternalData().getWorld();

        applyPassive(ref,store);

        flightLogic(deltaTime,ref,playerRef,store,commandBuffer);

        burnInSunlight(deltaTime,ref,playerRef,store,commandBuffer);
        darkSightPassiveEntityTest(ref,playerRef,store,commandBuffer);


    }

    @Override
    public void handlePacket(AtomicBoolean stopPacket, boolean out, GamePacketHandler gpHandler, Packet packet, PlayerRef playerRef) {

    }

    public boolean hasDarkSight = false;
    Ref<EntityStore> darkSightRef = null;
    private void darkSightPassiveEntityTest(Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer){
        TransformComponent transformComponent = store.getComponent(ref,TransformComponent.getComponentType());
        assert transformComponent != null;
        Vector3d position = transformComponent.getPosition().clone();
        float headHeight = 0;
        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent != null) {
            headHeight += modelComponent.getModel().getEyeHeight(ref,store);
        }
        Vector3d headPosition = new  Vector3d(position.getX(), position.getY()+headHeight, position.getZ());

        if (!hasDarkSight){
            if (darkSightRef != null){
                commandBuffer.ensureAndGetComponent(darkSightRef,TransformComponent.getComponentType()).setPosition(headPosition);
                commandBuffer.ensureAndGetComponent(darkSightRef,CleanUpComponent.getComponentType()).secondsTillDespawn=0.5f;
                var dl = commandBuffer.ensureAndGetComponent(darkSightRef,DynamicLight.getComponentType());
                var cl = dl.getColorLight();
                if (cl.radius > 0){
                    cl.radius -= 5;
                    cl.radius =(byte) Math.max(0,cl.radius);
                    dl.setColorLight(cl);
                }
                else {
                    commandBuffer.removeEntity(darkSightRef,RemoveReason.REMOVE);
                    darkSightRef = null;
                }
            }
            return;
        }




        if (darkSightRef == null){
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(headPosition, Vector3f.ZERO));
            holder.addComponent(DynamicLight.getComponentType(), new DynamicLight(new ColorLight()));
            holder.ensureComponent(UUIDComponent.getComponentType());
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(commandBuffer.getExternalData().takeNextNetworkId()));
            holder.addComponent(PlayerExclusiveEntity.getComponentType(), new PlayerExclusiveEntity(Collections.singletonList(playerRef.getUuid())));
            holder.addComponent(CleanUpComponent.getComponentType(), new CleanUpComponent());
            darkSightRef = commandBuffer.addEntity(holder, AddReason.SPAWN);

        }else {
            if (darkSightRef.isValid()){
                commandBuffer.ensureAndGetComponent(darkSightRef,TransformComponent.getComponentType()).setPosition(headPosition);
                commandBuffer.ensureAndGetComponent(darkSightRef,CleanUpComponent.getComponentType()).secondsTillDespawn=0.5f;
                var dl = commandBuffer.ensureAndGetComponent(darkSightRef,DynamicLight.getComponentType());
                var cl = dl.getColorLight();
                if (cl.radius < 100){
                    cl.radius += 3;
                    cl.radius =(byte) Math.min(100,cl.radius);
                    dl.setColorLight(cl);
                }

            }

        }
    }

    private static final String PASSIVE_EFFECT_ID = "Vampire_Passive_Effect";

    private void applyPassive(Ref<EntityStore> ref,Store<EntityStore> store){
        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectController != null;

        addEffect("Vampire_Passive",PASSIVE_EFFECT_ID,ref,store,effectController);
    }
    private static final String SUNLIGHT_BURN_EFFECT_ID = "Vampire_Sunlight_Burn_Effect";
    private static final String SMOKING_EFFECT_ID = "Vampire_Smoking_Effect";
    private boolean isSmoking = false;
    private boolean startedBurning = false;

    private void burnInSunlight(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer){



        Player player = store.getComponent(ref,Player.getComponentType());
        assert player != null;

        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert  effectController != null;

        EntityStatMap statMap = store.getComponent(ref,EntityStatMap.getComponentType());
        assert statMap != null;


        ItemStack helmet = player.getInventory().getArmor().getItemStack((short) 0);

        boolean isExposedToSunlight = SimpleSunlightDetector.isExposedToSunlight(ref,store) && (helmet == null || helmet.isEmpty()); //SunlightDetector.isExposedToSunlight(ref, store);

        if (isExposedToSunlight) {
            EntityStatValue manaValue = statMap.get(DefaultEntityStatTypes.getMana());
            assert manaValue != null;
            float cost = 1*deltaTime;
            float regenDelay = -1;
            if  (manaValue.get()>=cost) {
                if (!isSmoking){
                    if (!startedBurning) {
                        addEffect("Smoke",SMOKING_EFFECT_ID,ref,commandBuffer,effectController);
                        isSmoking = true;
                    }


                }

                statMap.subtractStatValue(DefaultEntityStatTypes.getMana(),cost);
                int ManaRegenDelayIndex =EntityStatType.getAssetMap().getIndex("ManaRegenDelay");
                EntityStatValue mtdValue = statMap.get(ManaRegenDelayIndex);
                if  (mtdValue == null || mtdValue.get()> regenDelay) {
                    statMap.setStatValue(ManaRegenDelayIndex,regenDelay);
                }

                if (startedBurning) {
                    removeEffect("Burn",ref,commandBuffer,effectController);
                    startedBurning = false;
                }
            }
            else{ // out of stamina start burn
                if (!startedBurning){
                    addEffect("Burn",SUNLIGHT_BURN_EFFECT_ID,ref,commandBuffer,effectController);
                    startedBurning = true;
                    if (isSmoking) {
                        removeEffect("Smoke",ref,commandBuffer,effectController);
                        isSmoking = false;
                    }

                }
            }


        }
        else  {
            if (isSmoking) {
                removeEffect("Smoke",ref,commandBuffer,effectController);
                isSmoking = false;
            }
            if (startedBurning) {
                removeEffect("Burn",ref,commandBuffer,effectController);
                startedBurning = false;
            }
        }


    }

    private static final String BAT_TRANSFORMATION_EFFECT_ID = "Vampire_Bat_Transformation";
    private boolean isInFlight;
    //private Model playerModel;
    private void flightLogic(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer){
        MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
        assert movementManager != null; // if any asserts throw an exception there's prob something wrong but u can also just replace with an if (==null) {return}
        MovementStatesComponent movementStatesComponent = store.getComponent(ref, MovementStatesComponent.getComponentType());
        assert  movementStatesComponent != null;
        EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
        assert statMap != null;
        int staminaRegenDelayIndex = EntityStatType.getAssetMap().getIndex("StaminaRegenDelay");
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

        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectController != null;
        if (movementStatesComponent.getMovementStates().flying){

            if (!isInFlight){
                isInFlight = true;
                addEffect("Bat",BAT_TRANSFORMATION_EFFECT_ID,ref,commandBuffer,effectController);
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
                removeEffect("Bat",ref,commandBuffer,effectController);
            }
        }

        movementManager.update(playerRef.getPacketHandler());
    }



    @Override
    public void initialize(PlayerRef playerRef, ComponentAccessor<EntityStore> componentAccessor) {
        super.initialize(playerRef, componentAccessor);
    }

    @Override
    public void removed(PlayerRef playerRef, ComponentAccessor<EntityStore> componentAccessor) {
        super.removed(playerRef, componentAccessor);
        Ref<EntityStore> ref = playerRef.getReference();

        assert ref != null;

        //disable flight
        MovementManager movementManager = componentAccessor.getComponent(ref, MovementManager.getComponentType());
        if (movementManager != null) {
            movementManager.getSettings().canFly = false;
            movementManager.update(playerRef.getPacketHandler());
        }
        MovementStatesComponent movementStatesComponent = componentAccessor.getComponent(ref, MovementStatesComponent.getComponentType());
        if (movementStatesComponent != null) {
            movementStatesComponent.getMovementStates().flying = false;
            playerRef.getPacketHandler().writeNoCache(new SetMovementStates(new SavedMovementStates(false)));
        }

    }





}

