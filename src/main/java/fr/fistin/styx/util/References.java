package fr.fistin.styx.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by AstFaster
 * on 05/11/2022 at 16:55
 */
public class References {

    public static final String NAME = "Styx";

    public static final Path LOG_FOLDER = Paths.get("logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "latest.log");

}
