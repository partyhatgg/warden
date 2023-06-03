package live.mcparty.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.mcparty.warden.discord.CommandManager;
import live.mcparty.warden.discord.LeaveListener;
import live.mcparty.warden.paper.JoinListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class Warden extends JavaPlugin {
    private static Warden instance;

    public static Warden getInstance() {
        return instance;
    }

    public static boolean isMigratoryPeriod;

    private JDA jda;
    private CommandManager commandManager;
    private final ObjectMapper om = new ObjectMapper();
    private WhitelistHandler whitelistHandler;
    private VerificationHandler verificationHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.jda = JDABuilder.createDefault(getConfig().getString("warden.jda.token")).build();
        this.commandManager = new CommandManager();
        this.commandManager.registerCommands(this.jda);
        this.jda.addEventListener(this.commandManager, new LeaveListener());
        this.whitelistHandler = new WhitelistHandler(getDataFolder());
        this.whitelistHandler.readFromFile();
        this.verificationHandler = new VerificationHandler();
        isMigratoryPeriod = getConfig().getBoolean("warden.migration");
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
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
