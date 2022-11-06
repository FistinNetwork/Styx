package fr.fistin.styx;

import fr.fistin.api.impl.common.FistinAPIProvider;
import fr.fistin.styx.config.StyxConfig;
import fr.fistin.styx.lobby.SLobbyBalancer;
import fr.fistin.styx.util.References;
import fr.fistin.styx.util.logger.StyxLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by AstFaster
 * on 05/11/2022 at 16:52
 */
public class Styx {

    private static Styx instance;

    private StyxLogger logger;
    private StyxConfig config;

    private ScheduledExecutorService executorService;

    private FistinAPIProvider fistinAPIProvider;
    private SLobbyBalancer lobbyBalancer;

    public void start() {
        instance = this;

        StyxLogger.printHeaderMessage();

        this.logger = new StyxLogger();

        System.out.println("Starting " + References.NAME + "...");

        this.config = StyxConfig.load();
        this.executorService = Executors.newScheduledThreadPool(8);
        this.fistinAPIProvider = new FistinAPIProvider();
        this.fistinAPIProvider.enable(this.config.getFistinAPI(), this.logger);
        this.lobbyBalancer = new SLobbyBalancer();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        this.fistinAPIProvider.disable();
        this.executorService.shutdown();
    }

    public static Styx get() {
        return instance;
    }

    public StyxLogger getLogger() {
        return this.logger;
    }

    public StyxConfig getConfig() {
        return this.config;
    }

    public ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }

    public SLobbyBalancer getLobbyBalancer() {
        return this.lobbyBalancer;
    }

}
