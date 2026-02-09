package me.basil.otherworld.commands;

import au.ellie.hyui.builders.ItemGridBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.events.DroppedEventData;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.ui.ItemGridSlot;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.Ability;
import me.basil.otherworld.components.OtherworldData;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class OtherWorldDataCommand extends AbstractPlayerCommand {


    public OtherWorldDataCommand() {
        super("otherworld", "prints info about otherworldData");

    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        OtherworldData owData = store.getComponent(ref, OtherworldData.getComponentType());
        if (owData == null || owData.getRace() == null){
            playerRef.sendMessage(Message.raw("Hytalian (No Race Selected)"));
            return;
        }


        playerRef.sendMessage(Message.raw(owData.getRace().getName()));
        PageBuilder page = PageBuilder.pageForPlayer(playerRef)
                .loadHtml("Pages/OtherworldData.html");

        List<Ability> raceAbilities = owData.getRace().getAbilities().values().stream().toList();
        page.getById("race-abilities-grid", ItemGridBuilder.class).ifPresent(abilityGrid -> {

            var size = raceAbilities.size();
            for (Ability ability : raceAbilities) {
                abilityGrid.addSlot(new ItemGridSlot(ability.repItem).setName(ability.name).setDescription(ability.description));
            }
            final int slotsToAdd = ((int) Math.ceil(size / 10.0) * 10) - size;

            for (int i = 0; i < slotsToAdd; i++) {
                abilityGrid.addSlot(new ItemGridSlot());
            }
        });

        page.getById("equipped-abilities-grid",ItemGridBuilder.class).ifPresent(abilityGrid -> {
            for (int i = 0; i < 9 ; i++){
                Ability ability = owData.getAbility(i);
                ItemGridSlot itemGridSlot = new ItemGridSlot();
                if (ability != null) {
                    itemGridSlot.setItemStack(ability.repItem).setName(ability.name).setDescription(ability.description);
                }
                abilityGrid.addSlot(itemGridSlot);
            }

        });

        page.addEventListener("equipped-abilities-grid", CustomUIEventBindingType.Dropped,( eventData , ctx)->{
            DroppedEventData  data = (DroppedEventData) eventData;
            int newSlotIndex = data.getSlotIndex();
            int sourceGridIndex = data.getSourceInventorySectionId();
            int sourceSlotIndex = data.getSourceSlotId();

            ctx.getById("equipped-abilities-grid",ItemGridBuilder.class).ifPresent((grid)->{


                if (sourceGridIndex == 20){
                    //modify equipped abilities
                    if (sourceSlotIndex == newSlotIndex){

                    }else { //swap ability
                        owData.swapAbilities(newSlotIndex,sourceSlotIndex);
                        //swap visuals
                        ItemGridSlot draggedItemSlot = grid.getSlot(sourceSlotIndex);
                        ItemGridSlot dropSlot = grid.getSlot(newSlotIndex);
                        grid.updateSlot(dropSlot,sourceSlotIndex);
                        grid.updateSlot(draggedItemSlot,newSlotIndex);
                    }


                }
                else {
                    //set ability
                    ctx.getById("race-abilities-grid",ItemGridBuilder.class).ifPresent(abilityGrid -> {
                        Ability draggedAbility = raceAbilities.get(sourceSlotIndex);
                        owData.addAbility(draggedAbility.name,newSlotIndex);
                        //set visuals
                        ItemGridSlot draggedItemSlot = abilityGrid.getSlot(sourceSlotIndex);
                        grid.updateSlot(draggedItemSlot,newSlotIndex);
                    });

                }


            });

            ctx.updatePage(true);

        });
        page.addEventListener("unequip-grid",CustomUIEventBindingType.Dropped,(eventData,ctx)->{
            DroppedEventData  data = (DroppedEventData) eventData;
            if (data.getSourceInventorySectionId() != 30){//removes abilities
                return;
            }
            ctx.getById("equipped-abilities-grid",ItemGridBuilder.class).ifPresent(grid->{
                owData.addAbility(null,data.getSourceSlotId());
                grid.updateSlot(new ItemGridSlot(),data.getSourceSlotId());
            });




        });

        page.open(store);

    }


}