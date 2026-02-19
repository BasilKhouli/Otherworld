package me.basil.otherworld.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.commands.ArgumentType.RaceArgumentType;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class OwAbilityListCommand extends AbstractPlayerCommand {

    private final OptionalArg<Race> raceArg;
    public OwAbilityListCommand() {
        super("list", "List all available abilities for a race");
        setPermissionGroup(GameMode.Adventure);
        addAliases("l");
        raceArg = withOptionalArg("race","The race to list abilities for (defaults to your own race)", new RaceArgumentType());

    }

    @Override
    protected void execute(@NonNull CommandContext context, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        Race race = raceArg.get(context);

        if (race == null) {
            var owd = store.getComponent(ref, OtherworldData.getComponentType());
            if (owd == null || owd.getRace() == null) {
                context.sendMessage(Message.raw("Humans have no abilities"));
                return;
            }
            else {
                race = owd.getRace();
            }
        }

        StringBuilder abilityList = new StringBuilder();
        List<Ability> abilities = race.getAbilities().values().stream().toList();

        if (abilities.isEmpty()) {
            abilityList.append(race.getName()).append(" has no abilities");
        }
        else {
            abilityList.append("Abilities for ").append(race.getName()).append("\n");
            for (int i = 0; i <abilities.size(); i++) {
                Ability ability = abilities.get(i);
                abilityList.append(ability.name).append(" : ").append(ability.description);
                if (i+1 < abilities.size()) {
                    abilityList.append("\n");
                }
            }
        }

        context.sendMessage(Message.raw(abilityList.toString()));
    }
}
