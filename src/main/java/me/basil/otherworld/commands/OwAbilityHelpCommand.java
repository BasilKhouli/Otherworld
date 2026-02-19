package me.basil.otherworld.commands;

import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandUtil;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
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

public class OwAbilityHelpCommand extends AbstractPlayerCommand {
    private final RequiredArg<String> abilityArg;
    private final OptionalArg<Race> raceArg;
    public OwAbilityHelpCommand() {
        super("help","Get help for how to use a specific ability");
        setPermissionGroup(GameMode.Adventure);
        abilityArg = withRequiredArg("ability","The ability to show the usage info of", ArgTypes.STRING);
        raceArg = withOptionalArg("race","The race which the ability belongs to (use to get info on the abilities of other races)", new RaceArgumentType());
    }


    @Override
    protected void execute(@NonNull CommandContext context, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {

        Race race = raceArg.get(context);

        if (race == null) {
            var owd = store.getComponent(ref, OtherworldData.getComponentType());
            if (owd == null || owd.getRace() == null) {
                context.sendMessage(Message.raw("Humans have no abilities specify a race with \"--race <racename>\" or choose a race"));
                return;
            }
            else {
                race = owd.getRace();
            }
        }


        String abilityName = abilityArg.get(context);
        String lowerCaseAbilityName = abilityName.toLowerCase();
        Ability ability = race.getAbility(lowerCaseAbilityName);

        if (ability == null){
            List<String> validOptions = StringUtil.sortByFuzzyDistance(lowerCaseAbilityName,race.getAbilities().keySet(), CommandUtil.RECOMMEND_COUNT);

            context.sendMessage(Message.raw(abilityName +" is not a valid ability! Did your mean: "+String.join(", ", validOptions)+"?" ));
            return;
        }

        context.sendMessage(Message.raw("------"+ability.name + "------\n" + ability.description + "\n" + ability.usageGuide));

    }
}
