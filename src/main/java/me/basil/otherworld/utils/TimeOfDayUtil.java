package me.basil.otherworld.utils;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class TimeOfDayUtil {
    public static boolean isDayTime(Store<EntityStore> store){
        WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());

        var dayProgress = timeResource.getDayProgress();

        return dayProgress > 0.20 && dayProgress < 0.79;
    }


}
