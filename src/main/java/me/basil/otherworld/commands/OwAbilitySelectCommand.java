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
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.List;

public class OwAbilitySelectCommand extends AbstractPlayerCommand{
    private final RequiredArg<String> abilityArg;
    private final OptionalArg<Integer> slotArg;

    public OwAbilitySelectCommand() {
        super("ability", "Equip an ability");
        setPermissionGroup(GameMode.Adventure);
        addAliases("a");
        abilityArg = withRequiredArg("Ability", "The name of the ability to equip", ArgTypes.STRING);
        slotArg = withOptionalArg("slot", "The slot to equip to",ArgTypes.INTEGER);
        addSubCommand(new OwAbilityListCommand());
        addSubCommand(new OwAbilityHelpCommand());

    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        OtherworldData owd = store.getComponent(ref,OtherworldData.getComponentType());
        if (owd == null ||owd.getRace() == null) {
            commandContext.sendMessage(Message.raw("Humans have no abilities"));
            return;
        }

        Integer slot = slotArg.get(commandContext);
        if (slot == null){
            Player player = store.getComponent(ref,Player.getComponentType());
            assert player != null;
            slot = (int) player.getInventory().getActiveHotbarSlot();
        }

        String abilityName = abilityArg.get(commandContext);
        String lowerCaseAbilityName = abilityName.toLowerCase();
        Race race = owd.getRace();

        Ability ability = race.getAbility(lowerCaseAbilityName);

        if (ability == null){
            List<String> validOptions = StringUtil.sortByFuzzyDistance(lowerCaseAbilityName,race.getAbilities().keySet(), CommandUtil.RECOMMEND_COUNT);

            commandContext.sendMessage(Message.raw(abilityName +" is not a valid ability! Did your mean: "+String.join(", ", validOptions)+"?" ));
            return;
        }

        owd.addAbility(ability.name,slot);
        commandContext.sendMessage(Message.raw("Ability"+ abilityName +" was added to slot "+ slot));
    }
}
