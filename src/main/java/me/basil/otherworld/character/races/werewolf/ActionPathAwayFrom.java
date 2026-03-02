package me.basil.otherworld.character.races.werewolf;

import com.hypixel.hytale.builtin.path.path.TransientPathDefinition;
import com.hypixel.hytale.builtin.path.waypoint.RelativeWaypointDefinition;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.path.IPath;
import com.hypixel.hytale.server.core.universe.world.path.SimplePathWaypoint;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;

import java.util.List;

public class ActionPathAwayFrom extends ActionBase {
    private final Vector3d npcPosition;
    private final Vector3d threatPosition;
    private final double distance;

    public ActionPathAwayFrom(Vector3d npcPosition, Vector3d threatPosition, double distance) {
        super(new BuilderActionEmpty(false)); // You'll need a proper builder
        this.npcPosition = npcPosition;
        this.threatPosition = threatPosition;
        this.distance = distance;
    }

    @Override
    public boolean execute(Ref<EntityStore> ref, Role role, InfoProvider sensorInfo, double dt, Store<EntityStore> store) {
        // Calculate flee position
        Vector3d fleePosition = calculateFleePosition(npcPosition, threatPosition, distance);

        // Get NPC components
        NPCEntity npc = store.getComponent(ref, NPCEntity.getComponentType());
        TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());

        // Calculate direction and distance to flee position
        Vector3d direction = fleePosition.subtract(transform.getPosition());
        double distanceToTarget = direction.length();

        // Calculate rotation needed to face the flee position
        float rotation = (float) Math.atan2(direction.getX(), direction.getZ());

        // Create a single waypoint that points to the flee position
        List<RelativeWaypointDefinition> waypoints = List.of(
                new RelativeWaypointDefinition(rotation, distanceToTarget)
        );
        World world = store.getExternalData().getWorld();
        world.sendMessage(Message.raw("Path away from"));


        // Create the path definition
        TransientPathDefinition pathDefinition = new TransientPathDefinition(waypoints, 1.0);

        // Build the path and set it on the NPC
        IPath<SimplePathWaypoint> path = pathDefinition.buildPath(transform.getPosition(), headRotation.getRotation());

        npc.getPathManager().setTransientPath(path);
        role.getWorldSupport().overrideAttitude(ref, Attitude.HOSTILE, 5.0);
        role.getWorldSupport().requestNewPath();




        return true;
    }

    private static Vector3d calculateFleePosition(Vector3d npcPos, Vector3d threatPos, double distance) {
        // Calculate direction from threat to NPC (this points away from threat)
        Vector3d direction = npcPos.subtract(threatPos).normalize();

        // Calculate flee position (NPC position + direction * distance)
        return npcPos.add(direction.setLength(distance));
    }
}
