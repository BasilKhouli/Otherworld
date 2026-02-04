package me.basil.otherworld.character.races.vampire;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

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

    private boolean isInFlight;
    private Model playerModel;
    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {


        //Flight Logic
        MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
        assert movementManager != null; // if any asserts throw an exception there's prob something wrong but u can also just replace with an if (==null) {return}
        MovementStatesComponent movementStatesComponent = store.getComponent(ref, MovementStatesComponent.getComponentType());
        assert  movementStatesComponent != null;

        movementManager.getSettings().canFly = true;


        if (movementStatesComponent.getMovementStates().flying){
            if (!isInFlight){
                isInFlight = true;
                ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
                assert modelComponent != null;

                ModelAsset batAsset = ModelAsset.getAssetMap().getAsset("Bat");
                if (batAsset != null){
                    playerModel = modelComponent.getModel();
                    Model batModel = Model.createScaledModel(batAsset,1);
                    ModelComponent newModelComp = new ModelComponent(batModel);
                    commandBuffer.replaceComponent(ref,ModelComponent.getComponentType(),newModelComp);
                    //playerRef.sendMessage(Message.raw(modelComponent.getModel().toString()));
                }




            }

        }
        else {
            if (isInFlight){
                isInFlight = false;
                if (playerModel != null){
                    commandBuffer.replaceComponent(ref,ModelComponent.getComponentType(),new ModelComponent(playerModel));
                    PlayerSkinComponent playerSkinComponent = store.getComponent(ref, PlayerSkinComponent.getComponentType());
                    if (playerSkinComponent != null) {
                        playerSkinComponent.setNetworkOutdated();
                    }
                    playerModel = null;
                }
            }
        }


        //TODO: add darkSight maybe also separate these passives into their own methods which this calls







        movementManager.update(playerRef.getPacketHandler());
    }
}
