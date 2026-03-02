package me.basil.otherworld.character.races.werewolf;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import org.jspecify.annotations.Nullable;

public class BuilderActionEmpty extends BuilderActionBase {
    public BuilderActionEmpty(boolean once){
        this.once = once;
    }
    @Override
    public @Nullable String getShortDescription() {
        return "";
    }

    @Override
    public @Nullable String getLongDescription() {
        return "";
    }

    @Override
    public @Nullable Action build(BuilderSupport var1) {
        return null;
    }

    @Override
    public @Nullable BuilderDescriptorState getBuilderDescriptorState() {
        return null;
    }
}
