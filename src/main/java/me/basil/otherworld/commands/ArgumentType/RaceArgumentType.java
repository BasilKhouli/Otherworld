package me.basil.otherworld.commands.ArgumentType;

import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import me.basil.otherworld.character.races.Race;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.CommandBuffer;
import me.basil.otherworld.character.races.RaceManager;
import org.jspecify.annotations.NonNull;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RaceArgumentType extends SingleArgumentType<Race> {
    @Nonnull
    private static final Object2ObjectMap<String, Race> RACE_MAP = new Object2ObjectOpenHashMap<>();

    public RaceArgumentType() {
        super("Race", "A race within Otherworld", "Vampire", "Werewolf", "Spiritborn", "Merefolk", "Fiend");
    }

    @Nullable
    public Race parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
        String inputLowerCase = input.toLowerCase();
        Race race = RACE_MAP.get(inputLowerCase);
        if (race != null) {
            return race;
        } else {
            List<String> validRaces = StringUtil.sortByFuzzyDistance(inputLowerCase, RACE_MAP.keySet(), CommandUtil.RECOMMEND_COUNT);
            parseResult.fail(
                Message.raw(input +" is not a valid race. Did you mean: "+String.join(", ", validRaces)+"?")
            );
            return null;
        }
    }

    static {
        for  (Race race : RaceManager.getRaces()) {
            RACE_MAP.put(race.getName().toLowerCase(), race);
        }
    }

}
