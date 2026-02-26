package me.basil.otherworld;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.RaceManager;
import me.basil.otherworld.commands.OtherworldCommand;
import me.basil.otherworld.components.CleanUpComponent;
import me.basil.otherworld.components.OtherworldData;
import me.basil.otherworld.components.PlayerExclusiveEntity;
import me.basil.otherworld.systems.CleanUpSystem;
import me.basil.otherworld.systems.HiddenEntitiesSystem;
import me.basil.otherworld.systems.RaceSystem;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    //Component Types
    public static ComponentType<EntityStore,OtherworldData> OWDcomponentType;
    public static ComponentType<EntityStore, PlayerExclusiveEntity> PEEComponentType;
    public static ComponentType<EntityStore, CleanUpComponent> CUCComponentType;

    public Main(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        RaceManager.initialize();

        this.getCommandRegistry().registerCommand(new OtherworldCommand());

        ComponentRegistryProxy<EntityStore> eSR =this.getEntityStoreRegistry();

        OWDcomponentType = eSR.registerComponent(OtherworldData.class,"OtherworldData",OtherworldData.CODEC);
        PEEComponentType = eSR.registerComponent(PlayerExclusiveEntity.class,"PlayerExclusiveEntity",PlayerExclusiveEntity.CODEC);
        CUCComponentType = eSR.registerComponent(CleanUpComponent.class,"CleanUpComponent",CleanUpComponent.CODEC);

        eSR.registerSystem(new RaceSystem());
        eSR.registerSystem(new HiddenEntitiesSystem());
        eSR.registerSystem(new CleanUpSystem());

        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        PacketAdapters.registerInbound(this::onInBoundPacket);
        PacketAdapters.registerInbound(this::onOutBoundPacket);

    }
    private boolean onInBoundPacket(PacketHandler handler,Packet packet){
        return packetHandling(false,handler,packet);
    }
    private boolean onOutBoundPacket(PacketHandler handler,Packet packet){
        return packetHandling(true,handler,packet);
    }

    private boolean packetHandling(boolean out,PacketHandler handler, Packet packet){
        final AtomicBoolean stopPacket = new AtomicBoolean(false);
        if (!(handler instanceof GamePacketHandler gpHandler)) return stopPacket.get();
        PlayerRef playerRef = gpHandler.getPlayerRef();
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null) return stopPacket.get();
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();

        world.execute(()->{
            OtherworldData owd = store.getComponent(ref,OtherworldData.getComponentType());
            if (owd == null) {
                return;
            }

            owd.packetHandling(stopPacket,out,gpHandler,packet,playerRef);
        });

        return stopPacket.get();
    }

    private void onPlayerReady(PlayerReadyEvent event){
        Player player = event.getPlayer();
        World world = player.getWorld();
        assert world != null;
        world.execute(() -> {

            Ref<EntityStore> ref = event.getPlayerRef();
            Store<EntityStore> store = ref.getStore();
            store.ensureComponent(ref, OtherworldData.getComponentType());

        });
    }

}
