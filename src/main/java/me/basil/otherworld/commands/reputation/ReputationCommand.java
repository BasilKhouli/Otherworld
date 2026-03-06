package me.basil.otherworld.commands.reputation;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class ReputationCommand extends AbstractCommandCollection {
   public ReputationCommand() {
      super("reputation", "server.commands.reputation.desc");
      addAliases("rep");
      this.addSubCommand(new ReputationAddCommand());
      this.addSubCommand(new ReputationSetCommand());
      this.addSubCommand(new ReputationRankCommand());
      this.addSubCommand(new ReputationValueCommand());
   }
}
