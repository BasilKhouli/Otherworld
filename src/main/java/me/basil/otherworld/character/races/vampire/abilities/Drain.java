package me.basil.otherworld.character.races.vampire.abilities;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.basil.otherworld.character.races.Ability;

public class Drain extends Ability {
    public Drain() {
        super("Drain","Drain health and stamina from a target","Crouch while looking at a target to drain them of health and stamina while regaining your own at the cost of mana.");
    }

    MovementStatesComponent movementStatesComponent;

    @Override
    public void selected(Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        movementStatesComponent = store.getComponent(ref,MovementStatesComponent.getComponentType());
    }

    float cooldown = 0;
    boolean wasCrouched = false;
    @Override
    public void selectedTick(float deltaTime, Ref<EntityStore> ref, PlayerRef playerRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        final float attackCooldown = 0.1f;

        boolean currentCrouched = movementStatesComponent.getMovementStates().crouching;

        if (cooldown>0){// if tick lags multiple runs of the tick logic may take place but cooldown still rests at 0 if not in active use
            cooldown -= deltaTime;
        } else if (cooldown<0) {
            cooldown = 0;
        }

        final float costPerTick = 0.5f;
        final float manaRegenDelay = -0.3f;
        final float appliedStaminaRegenDelay = -0.5f;
        final float tickDamage = 0.5f;
        final float staminaDrain = 1f;

        EntityStatMap statMap = commandBuffer.getComponent(ref, EntityStatMap.getComponentType());
        assert statMap != null;
        EntityStatValue currentMana = statMap.get(DefaultEntityStatTypes.getMana());
        assert currentMana != null;


        final int attacksAvailable = (int) Math.max(1, -cooldown / attackCooldown + 1);
        final int maxAffordableAttacks = (int) (currentMana.get() / costPerTick);
        final int attacksToExecute = Math.min(attacksAvailable, maxAffordableAttacks);

        if (currentCrouched && cooldown<=0){

            for (int i = 0;i< attacksToExecute;i++){



                Ref<EntityStore> targetRef = TargetUtil.getTargetEntity(ref, commandBuffer);
                if (targetRef != null && targetRef.isValid()) {
                    // Create a source identifying the attacker (the player using Drain)
                    Damage.EntitySource source = new Damage.EntitySource(ref);
                    DamageCause cause = DamageCause.getAssetMap().getAsset("Command");

                    if  (cause != null) {
                        Damage damage = new Damage(source, cause, tickDamage);
                        DamageSystems.executeDamage(targetRef, commandBuffer, damage);
                        EntityStatMap targetStatMap = store.getComponent(targetRef,EntityStatMap.getComponentType());
                        if (targetStatMap != null) {
                            int staminaIndex = DefaultEntityStatTypes.getStamina();
                            targetStatMap.subtractStatValue(staminaIndex, staminaDrain);
                            int staminaRDIndex = EntityStatType.getAssetMap().getIndex("StaminaRegenDelay");
                            EntityStatValue stamRDValue = targetStatMap.get(staminaRDIndex);
                            if (stamRDValue==null|| stamRDValue.get() > appliedStaminaRegenDelay)
                            targetStatMap.setStatValue(staminaRDIndex, appliedStaminaRegenDelay);
                        }

                        float damageDone = damage.getAmount();
                        if (damageDone > 0){
                            float heal = damageDone * 0.40f;


                            int healthIndex = DefaultEntityStatTypes.getHealth();
                            statMap.addStatValue(healthIndex, heal);
                            int staminaIndex = DefaultEntityStatTypes.getStamina();
                            statMap.addStatValue(staminaIndex, staminaDrain*0.75f);


                        }



                    }
                    statMap.subtractStatValue(DefaultEntityStatTypes.getMana(), costPerTick);
                    int ManaRegenDelayIndex =EntityStatType.getAssetMap().getIndex("ManaRegenDelay");
                    EntityStatValue mtdValue = statMap.get(ManaRegenDelayIndex);
                    if  (mtdValue == null || mtdValue.get() > manaRegenDelay) {
                        statMap.setStatValue(ManaRegenDelayIndex,manaRegenDelay);
                    }
                    cooldown += attackCooldown;
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
        if (cooldown>0){
            cooldown -= deltaTime;
        } else if (cooldown<0) {
            cooldown = 0;
        }
    }

    @Override
    public Ability clone() {
        return new Drain();
    }
}
