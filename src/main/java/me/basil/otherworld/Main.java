package me.basil.otherworld;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.basil.otherworld.character.races.RaceManager;
import me.basil.otherworld.commands.OtherworldCommand;
import me.basil.otherworld.components.OtherworldData;
import me.basil.otherworld.systems.RaceSystem;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static Map<Class<? extends Component<EntityStore>>,ComponentType<EntityStore,? extends Component<EntityStore>>> componentTypeMap = new HashMap<>();

    //Componenty Types
    public static ComponentType<EntityStore,OtherworldData> OWDcomponentType;

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

        eSR.registerSystem(new RaceSystem());



        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, (event) -> {
            Player player = event.getPlayer();
            World world = player.getWorld();
            world.execute(() -> {

                Ref<EntityStore> ref = event.getPlayerRef();
                Store<EntityStore> store = ref.getStore();
                PlayerRef playerRef = store.getComponent(ref,PlayerRef.getComponentType());
                assert playerRef != null;
                OtherworldData owData = store.ensureAndGetComponent(ref, OtherworldData.getComponentType());
                //only temp till we add way to choose
                owData.chooseRace("Vampire",playerRef);

            });

        });

    }

}
