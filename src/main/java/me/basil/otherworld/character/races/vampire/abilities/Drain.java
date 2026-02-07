package me.basil.otherworld.character.races.vampire.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.basil.otherworld.character.races.Ability;

public class Drain extends Ability {
    public Drain() {
        super("Drain");
    }

    MovementStatesComponent movementStatesComponent;

    @Override
    public void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        movementStatesComponent = store.getComponent(ref,MovementStatesComponent.getComponentType());
    }

    boolean wasCrouched = false;
    @Override
    public void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        boolean currentCrouched = movementStatesComponent.getMovementStates().crouching;
        if (currentCrouched && !wasCrouched) {
            Ref<EntityStore> targetRef = TargetUtil.getTargetEntity(ref, commandBuffer);
            if (targetRef != null && targetRef.isValid()) {
                // Create a source identifying the attacker (the player using Drain)
                Damage.EntitySource source = new Damage.EntitySource(ref);
                DamageCause cause = DamageCause.getAssetMap().getAsset("Command");

                if  (cause != null) {
                    Damage damage = new Damage(source, cause, 5.0f);


                    DamageSystems.executeDamage(targetRef, commandBuffer, damage);

                    float damageDone = damage.getAmount();
                    if (damageDone > 0){
                        float heal = Math.max(damageDone * 0.40f,0.5f);

                        EntityStatMap statMap = commandBuffer.getComponent(ref, EntityStatMap.getComponentType());
                        if (statMap != null) {
                            int healthIndex = DefaultEntityStatTypes.getHealth();
                            statMap.addStatValue(healthIndex, heal);
                            int staminaIndex = DefaultEntityStatTypes.getStamina();
                            statMap.addStatValue(staminaIndex, heal*2);
                        }

                    }

                }

            }
        }
        wasCrouched = currentCrouched;
    }


    @Override
    public void unselected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void passiveTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public Ability clone() {
        return new Drain();
    }
}
