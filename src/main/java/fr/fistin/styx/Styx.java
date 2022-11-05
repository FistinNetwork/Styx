package fr.fistin.styx;

import fr.fistin.styx.util.References;
import fr.fistin.styx.util.logger.StyxLogger;

/**
 * Created by AstFaster
 * on 05/11/2022 at 16:52
 */
public class Styx {

    private StyxLogger logger;

    public void start() {
        StyxLogger.printHeaderMessage();

        this.logger = new StyxLogger();

        System.out.println("Starting " + References.NAME + "...");
    }

    public StyxLogger getLogger() {
        return this.logger;
    }

}
