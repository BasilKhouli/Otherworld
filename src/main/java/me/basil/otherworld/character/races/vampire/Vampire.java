package me.basil.otherworld.character.races.vampire;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.player.SetMovementStates;
import com.hypixel.hytale.protocol.packets.world.SetChunk;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.character.races.vampire.abilities.DarkSightToggle;
import me.basil.otherworld.character.races.vampire.abilities.Drain;
import me.basil.otherworld.utils.SimpleSunlightDetector;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

public class Vampire extends Race {
    public Vampire( ) {

        List<Ability> raceAbilities = List.of(
                new Drain(),
                new DarkSightToggle()

                //TODO

        );

        Map<String, Modifier> raceModifiers = Map.of(
                //TODO
        );


        super("Vampire", raceAbilities, raceModifiers,null);

    }




    @Override
    public Race clone() {
        return new Vampire();
    }





    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
        flightLogic(deltaTime,ref,playerRef,store,commandBuffer);
        burnInSunlight(deltaTime,ref,playerRef,store,commandBuffer);
        applyPassive(ref,store);




    }

    private static final String PASSIVE_EFFECT_ID = "Vampire_Passive_Effect";

    private void applyPassive(Ref<EntityStore> ref,Store<EntityStore> store){
        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectController != null;
        addEffect(PASSIVE_EFFECT_ID,ref,store,effectController);
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
                        addEffect(SMOKING_EFFECT_ID,ref,commandBuffer,effectController);
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
                    removeEffect(SUNLIGHT_BURN_EFFECT_ID,ref,commandBuffer,effectController);
                    startedBurning = false;
                }
            }
            else{ // out of stamina start burn
                if (!startedBurning){
                    addEffect(SUNLIGHT_BURN_EFFECT_ID,ref,commandBuffer,effectController);
                    startedBurning = true;
                    if (isSmoking) {
                        removeEffect(SMOKING_EFFECT_ID,ref,commandBuffer,effectController);
                        isSmoking = false;
                    }

                }
            }


        }
        else  {
            if (isSmoking) {
                removeEffect(SMOKING_EFFECT_ID,ref,commandBuffer,effectController);
                isSmoking = false;
            }
            if (startedBurning) {
                removeEffect(SUNLIGHT_BURN_EFFECT_ID,ref,commandBuffer,effectController);
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
                addEffect(BAT_TRANSFORMATION_EFFECT_ID,ref,commandBuffer,effectController);
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
                removeEffect(BAT_TRANSFORMATION_EFFECT_ID,ref,commandBuffer,effectController);
            }
        }

        movementManager.update(playerRef.getPacketHandler());
    }

    public boolean hasDarkVision = false;

    @Override
    public void initialize(PlayerRef playerRef, ComponentAccessor<EntityStore> componentAccessor) {
        super.initialize(playerRef, componentAccessor);
        reloadChunks(playerRef);
        DarkVisionPacketSetUp(playerRef);

    }

    @Override
    public void removed(PlayerRef playerRef, ComponentAccessor<EntityStore> componentAccessor) {
        super.removed(playerRef, componentAccessor);
        Ref<EntityStore> ref = playerRef.getReference();

        assert ref != null;

        //clear passive

        /*
        EffectControllerComponent effectController = store.getComponent(ref, EffectControllerComponent.getComponentType());
        assert effectController != null;
        int passiveIndex = EntityEffect.getAssetMap().getIndex(PASSIVE_EFFECT_ID);
        if (passiveIndex != Integer.MIN_VALUE) {
            effectController.removeEffect(ref, passiveIndex, store);
        }

        //clear flight effects
        int batTransformIndex = EntityEffect.getAssetMap().getIndex(BAT_TRANSFORMATION_EFFECT_ID);
        if (batTransformIndex != Integer.MIN_VALUE) {
            effectController.removeEffect(ref, batTransformIndex, store);
        }

        //clear burn
        int burnIndex = EntityEffect.getAssetMap().getIndex(SUNLIGHT_BURN_EFFECT_ID);
        if (burnIndex != Integer.MIN_VALUE) {
            effectController.removeEffect(ref, burnIndex, store);
        }
        int smokeIndex = EntityEffect.getAssetMap().getIndex(SMOKING_EFFECT_ID);
        if (smokeIndex != Integer.MIN_VALUE) {
            effectController.removeEffect(ref, smokeIndex, store);
        }
         */
        
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

        //clear full bright
        playerRef.getPacketHandler().getChannel().pipeline().remove("Vampire_FullBright");
        reloadChunks(playerRef);

    }

    //region Brightness Packet stuff
    private void DarkVisionPacketSetUp(PlayerRef playerRef){
        final ChannelOutboundHandlerAdapter fullBrightHandler = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, io.netty.channel.ChannelPromise promise) throws Exception {
                if (!hasDarkVision){
                    super.write(ctx, msg, promise);
                    return;
                }
                if (msg instanceof Packet packet){
                    msg = packetChecker(packet,playerRef);
                }
                else if (msg instanceof Packet[] packets){
                    for (int i = 0; i < packets.length; i++){
                        packets[i] = packetChecker(packets[i],playerRef);
                    }
                    msg = packets;

                }
                super.write(ctx, msg, promise);
            }
        };

        playerRef.getPacketHandler().getChannel().pipeline().addLast("Vampire_FullBright",fullBrightHandler);


    }

    private Packet packetChecker(Packet originalPacket, PlayerRef playerRef){
        if (originalPacket instanceof SetChunk setChunk){
            return modifyChunkPacket(setChunk,playerRef);
        }
        else if (originalPacket instanceof CachedPacket<?> cachedPacket && cachedPacket.getId() == 131){
            SetChunk unpackedSetChunk = unpackSetChunk(cachedPacket);
            if (unpackedSetChunk != null){
                return modifyChunkPacket(unpackedSetChunk,playerRef);
            }
        }
        return originalPacket;
    }

    public int brightnessLevel = 10;
    private SetChunk modifyChunkPacket(SetChunk originalPacket, PlayerRef playerRef){
        SetChunk modifiedPacket = new SetChunk(originalPacket);
        final byte[] lightData = generateFlatLightData();


        modifiedPacket.localLight = lightData;
        modifiedPacket.globalLight = lightData;

        //playerRef.sendMessage(Message.raw(String.valueOf(originalPacket.localLight.length)));


        return modifiedPacket;
    }

    private byte[] generateFlatLightData() {
        ByteBuf buf = Unpooled.buffer(18);
        buf.writeBoolean(true);
        buf.writeByte(0);
        short packed = (short)((brightnessLevel & 15) * 4369);

        for(int i = 0; i < 8; ++i) {
            buf.writeShortLE(packed);
        }

        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();

        return data;
    }

    private SetChunk unpackSetChunk(CachedPacket<?> cached) { // I HAVE NO IDEA HOW THIS WORKS STOLEN FROM FULL BRIGHT MOD :D

        Field cachedBytesField = null;
        try {
            cachedBytesField = CachedPacket.class.getDeclaredField("cachedBytes");
            cachedBytesField.setAccessible(true);
        } catch (NoSuchFieldException var2) {
            //((HytaleLogger.Api)LOGGER.atWarning()).log("Could not access CachedPacket.cachedBytes - some features may not work");
        }

        if (cachedBytesField == null) {
            return null;
        } else {
            try {
                ByteBuf buf = (ByteBuf)cachedBytesField.get(cached);
                if (buf != null) {
                    return SetChunk.deserialize(buf.duplicate(), 0);
                }
            } catch (Exception var3) {
                //((HytaleLogger.Api)LOGGER.atFine()).log("Failed to unpack CachedPacket");
            }

            return null;
        }
    }

    public void reloadChunks(PlayerRef playerRef) {
        try {
            Ref<?> ref = playerRef.getReference();
            if (ref != null) {
                World world = ((EntityStore)ref.getStore().getExternalData()).getWorld();
                world.execute(() -> playerRef.getChunkTracker().unloadAll(playerRef));
            }
        } catch (Exception e) {
            //((HytaleLogger.Api)LOGGER.atWarning()).log("Failed to reload chunks: " + e.getMessage());
        }

    }
    //endregion


}

