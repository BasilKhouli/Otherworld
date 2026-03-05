package me.basil.otherworld.ui.huds;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.character.races.Race;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class SkillList extends CustomUIHud {
    public SkillList(@NonNull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNull UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Otherworld/Huds/SkillList.ui");
    }

    public void updateDisplay(OtherworldData data) {
        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        Race race = data.getRace();
        String raceName = race != null ? race.getName() : "Human";
        uiCommandBuilder.set("#RaceName.TextSpans", Message.raw(raceName));

        /*
        Message[] abilityListMessages = new Message[9];
        for (int i = 0; i<9; i++){

            Ability ability = data.getAbility(i);


            if (ability == null){
                abilityListMessages[i] = Message.raw("Empty");
                continue;
            }
            abilityListMessages[i] = ability.getAbilityMessage(getPlayerRef());

        }
        for (int i = 0; i<9; i++){
            Message message = abilityListMessages[i];
            if (data.selectedSlot == i){
                message = Message.join(Message.raw("> ").color(Color.yellow),message);
            }
            String uIElementName = "#Ability" + (i+1);

            uiCommandBuilder.set(uIElementName+".TextSpans",message);

        }

         */

        for (int i = 0; i < 9; i++) {
            Ability ability = data.getAbility(i);
            Message message = ability != null ?
                    ability.getAbilityMessage(getPlayerRef()) :
                    Message.raw("Empty");

            if (data.selectedSlot == i) {
                message = Message.join(Message.raw("> ").color(Color.yellow), message);
            }

            uiCommandBuilder.set("#Ability" + (i + 1) + ".TextSpans", message);
        }


        update(false, uiCommandBuilder);
    }
}
