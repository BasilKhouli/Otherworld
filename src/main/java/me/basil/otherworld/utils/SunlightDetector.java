package me.basil.otherworld.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class SunlightDetector {
    public static final byte DEFAULT_MIN_SKY_LIGHT = 14;
    public static final double DEFAULT_MIN_SUNLIGHT_FACTOR = 0.5;
    public static final byte DEFAULT_MIN_SUNLIGHT_LEVEL = 10;

    public static boolean isExposedToSunlight(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        return isExposedToSunlight(entityRef, store, DEFAULT_MIN_SKY_LIGHT, DEFAULT_MIN_SUNLIGHT_FACTOR, DEFAULT_MIN_SUNLIGHT_LEVEL);
    }

    public static boolean isExposedToSunlight(Ref<EntityStore> entityRef, Store<EntityStore> store, byte minSkyLight, double minSunlightFactor, byte minSunlightLevel) {
        SunlightData data = getSunlightData(entityRef, store);
        if (data == null) {
            return false;
        }

        return data.skyLight >= minSkyLight
                && data.sunlightFactor > minSunlightFactor
                && data.sunlightLevel >= minSunlightLevel;
    }

    @Nullable
    public static SunlightData getSunlightData(Ref<EntityStore> entityRef, Store<EntityStore> store) {
        TransformComponent transform = store.getComponent(entityRef, TransformComponent.getComponentType());
        if (transform == null) {
            return null;
        }

        Vector3d position = transform.getPosition();
        int x = MathUtil.floor(position.getX());
        int y = MathUtil.floor(position.getY());
        int z = MathUtil.floor(position.getZ());

        World world = store.getExternalData().getWorld();
        if (world == null) {
            return null;
        }

        WorldChunk chunk = world.getChunkIfInMemory((ChunkUtil.indexChunkFromBlock(x, z)));
        if (chunk == null) {
            return null;
        }

        BlockChunk blockChunk = chunk.getBlockChunk();
        if (blockChunk == null) {
            return null;
        }

        byte skyLight = blockChunk.getSkyLight(x, y, z);

        WorldTimeResource worldTime = store.getResource(WorldTimeResource.getResourceType());
        if (worldTime == null) {
            return null;
        }

        double sunlightFactor = worldTime.getSunlightFactor();
        byte sunlightLevel = (byte) (skyLight * sunlightFactor);
        byte blockLightIntensity = blockChunk.getBlockLightIntensity(x, y, z);

        return new SunlightData(x, y, z, skyLight, sunlightFactor, sunlightLevel, blockLightIntensity);

    }

    public static boolean isDaytime(Store<EntityStore> store) {
        WorldTimeResource worldTime = store.getResource(WorldTimeResource.getResourceType());
        if (worldTime == null) {
            return false;
        }
        return worldTime.getSunlightFactor() > 0.5;
    }

    public static double getSunlightFactor(Store<EntityStore> store) {
        WorldTimeResource worldTime = store.getResource(WorldTimeResource.getResourceType());
        if (worldTime == null) {
            return 0;
        }
        return worldTime.getSunlightFactor();
    }

    public static class SunlightData {
        public final int x, y, z;
        public final byte skyLight;
        public final double sunlightFactor;
        public final byte sunlightLevel;
        public final byte blockLightIntensity;

        public SunlightData(int x, int y, int z, byte skyLight, double sunlightFactor, byte sunlightLevel, byte blockLightIntensity) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.skyLight = skyLight;
            this.sunlightFactor = sunlightFactor;
            this.sunlightLevel = sunlightLevel;
            this.blockLightIntensity = blockLightIntensity;
        }

        public boolean hasClearSky() {
            return skyLight >= 15;
        }

        public boolean isInShade() {
            return skyLight < 10;
        }

        public boolean isIndoors() {
            return skyLight < 5;
        }

        public byte getTotalLight() {
            return (byte) Math.max(sunlightLevel, blockLightIntensity);
        }

        @Override
        public String toString() {
            return String.format("SunlightData[pos=(%d,%d,%d), skyLight=%d, factor=%.2f, sunlight=%d, blockLight=%d]",
                    x, y, z, skyLight, sunlightFactor, sunlightLevel, blockLightIntensity);
        }

    }

}
