package me.basil.otherworld.character.races.werewolf;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;

public class ActionPathAwayFrom extends ActionBase {
    private final Vector3d threatPosition;
    private final double distance;

    public ActionPathAwayFrom(Vector3d threatPosition, double distance) {
        super(new BuilderActionEmpty(false));
        this.threatPosition = threatPosition;
        this.distance = distance;
    }

    @Override
    public boolean execute(Ref<EntityStore> ref, Role role, InfoProvider sensorInfo, double dt, Store<EntityStore> store) {
        //TODO figure out how to make work if can at all...
        
        return false;
    }

    private static Vector3d calculateFleePosition(Vector3d npcPos, Vector3d threatPos, double distance) {
        // Calculate direction from threat to NPC (this points away from threat)
        Vector3d direction = npcPos.subtract(threatPos).normalize();

        // Calculate flee position (NPC position + direction * distance)
        return npcPos.add(direction.setLength(distance));
    }
}
