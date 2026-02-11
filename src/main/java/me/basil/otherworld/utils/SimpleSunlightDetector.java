package me.basil.otherworld.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import com.hypixel.hytale.math.block.BlockConeUtil;
import com.hypixel.hytale.math.block.BlockCubeUtil;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.ShaderType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleSunlightDetector {

    private static final double MIN_SUNLIGHT_FACTOR = 0.1;
    private static final int CHECK_HEIGHT = 32;

    public static boolean isExposedToSunlight(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        TransformComponent transform = store.getComponent(entityRef, TransformComponent.getComponentType());
        if (transform == null) {
            return false;
        }

        World world = store.getExternalData().getWorld();

        if (!isDaytime(store)) {
            return false;
        }

        return hasDirectSkyAccess(transform.getPosition(), world);
    }

    public static boolean isDaytime(Store<EntityStore> store) {
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());


        double sunlightFactor = timeResource.getSunlightFactor();//TODO this should check the time of day instead of sunlightFactor
        return sunlightFactor > MIN_SUNLIGHT_FACTOR;
    }

    private static boolean hasDirectSkyAccess(Vector3d position, World world) {
        int x = MathUtil.floor(position.getX());
        int startY = MathUtil.floor(position.getY()) + 1;
        int z = MathUtil.floor(position.getZ());

        AtomicBoolean isAllAir = new AtomicBoolean(true);
        TriIntObjPredicate<World> isAllAirPredicate = (bx, by, bz, w) -> {
            WorldChunk chunk = w.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(bx, bz));
            if (chunk == null) {
                return false;
            }

            BlockChunk blockChunk = chunk.getBlockChunk();
            if (blockChunk == null) {
                return false;
            }

            int blockId = blockChunk.getBlock(bx & 31, by, bz & 31);

            if (blockId != 0) {
                isAllAir.set(false);
                return false;
            }

            return true;
        };

        BlockConeUtil.forEachBlock(
                x,
                startY,
                z,
                2,
                319,
                2,
                world,
                isAllAirPredicate
        );


        //isAllAir= BlockCubeUtil.forEachBlock(new Vector3i(x,startY,z), new Vector3i(x, Math.min(startY + CHECK_HEIGHT - 1, 319), z), world, isAllAir);
        return isAllAir.get();
    }

    public static double getSunlightFactor(Store<EntityStore> store) {
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
        return timeResource.getSunlightFactor();
    }
}