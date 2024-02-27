package dev.partyhat.warden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dev.partyhat.warden.discord.CommandManager;
import dev.partyhat.warden.discord.LeaveListener;
import dev.partyhat.warden.util.player.DefaultPlayerUtil;
import dev.partyhat.warden.util.player.IPlayerUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Warden {
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

    public static Logger LOGGER = LoggerFactory.getLogger("Warden");

    public static Gson GSON = new Gson();

    public static IPlayerUtil PLAYER_UTIL = new DefaultPlayerUtil();

    private JDA jda;
    private CommandManager commandManager;
    private final ObjectMapper om = new ObjectMapper();
    private WhitelistHandler whitelistHandler;
    private VerificationHandler verificationHandler;
    private JoinHandler joinHandler;

    public Warden(File dataFolder, IConfig config) {
        instance = this;
        this.whitelistHandler = new WhitelistHandler(dataFolder);
        this.whitelistHandler.readFromFile();
        this.verificationHandler = new VerificationHandler();

        isMigratoryPeriod = config.getObject("warden.migration", Boolean.class);
        this.jda = JDABuilder.createDefault((String) config.getPrimitive("warden.jda.token")).build();
        modRoles = Arrays.stream(config.getObject("warden.modroles", long[].class)).mapToObj(id -> jda.getRoleById(id)).collect(Collectors.toSet());
        this.commandManager = new CommandManager();
        this.commandManager.registerCommands(this.jda);
        this.jda.addEventListener(this.commandManager, new LeaveListener());
        this.joinHandler = new JoinHandler();
    }

    public void onShutdown() {
        this.whitelistHandler.saveToFile();
        this.jda.shutdown();
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

    public JoinHandler getJoinHandler() {
        return joinHandler;
    }
}
