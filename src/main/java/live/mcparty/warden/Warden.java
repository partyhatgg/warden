package live.mcparty.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.mcparty.warden.discord.CommandManager;
import live.mcparty.warden.discord.LeaveListener;
import live.mcparty.warden.paper.JoinListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Warden extends JavaPlugin {
    private static Warden instance;

    public static Warden getInstance() {
        return instance;
    }

    public static boolean isMigratoryPeriod;

    public static Set<Role> modRoles;

    public static final Set<Long> SUPERUSERS = new HashSet<>() {{
        add(237697797745278976L); // Sychic
        add(194861788926443520L); // DeJay
    }};

    private JDA jda;
    private CommandManager commandManager;
    private final ObjectMapper om = new ObjectMapper();
    private WhitelistHandler whitelistHandler;
    private VerificationHandler verificationHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.whitelistHandler = new WhitelistHandler(getDataFolder());
        this.whitelistHandler.readFromFile();
        this.verificationHandler = new VerificationHandler();
        isMigratoryPeriod = getConfig().getBoolean("warden.migration");
        modRoles = getConfig().getLongList("warden.modroles").stream().map(id -> jda.getRoleById(id)).collect(Collectors.toSet());
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        this.jda = JDABuilder.createDefault(getConfig().getString("warden.jda.token")).build();
        this.commandManager = new CommandManager();
        this.commandManager.registerCommands(this.jda);
        this.jda.addEventListener(this.commandManager, new LeaveListener());
    }

    @Override
    public void onDisable() {
        this.whitelistHandler.saveToFile();
    }

    public ObjectMapper getObjectMapper() {
        return om;
    }

    public WhitelistHandler getWhitelistHandler() {
        return whitelistHandler;
    }

    public VerificationHandler getVerificationHandler() {
        return verificationHandler;
    }
}
