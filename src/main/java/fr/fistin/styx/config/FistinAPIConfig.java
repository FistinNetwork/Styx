package fr.fistin.styx.config;

import fr.fistin.api.configuration.FistinAPIConfiguration;
import fr.fistin.hydra.api.protocol.data.RedisData;

/**
 * Created by AstFaster
 * on 06/11/2022 at 09:36
 */
public class FistinAPIConfig implements FistinAPIConfiguration {

    private Leveling leveling;
    private RedisData redis;

    private FistinAPIConfig() {}

    public FistinAPIConfig(Leveling leveling, RedisData redis) {
        this.leveling = leveling;
        this.redis = redis;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String getLevelingUser() {
        return this.leveling.getUsername();
    }

    @Override
    public String getLevelingPass() {
        return this.leveling.getPassword();
    }

    @Override
    public String getLevelingHost() {
        return this.leveling.getHostname();
    }

    @Override
    public String getLevelingDbName() {
        return this.leveling.getDatabase();
    }

    @Override
    public int getLevelingPort() {
        return this.leveling.getPort();
    }

    @Override
    public RedisData getRedis() {
        return this.redis;
    }

    @Override
    public boolean isHydraEnabled() {
        return true;
    }

    public static class Leveling {

        private String username;
        private String password;
        private String hostname;
        private String database;
        private int port;

        private Leveling() {}

        public Leveling(String username, String password, String hostname, String database, int port) {
            this.username = username;
            this.password = password;
            this.hostname = hostname;
            this.database = database;
            this.port = port;
        }

        public String getUsername() {
            return this.username;
        }

        public String getPassword() {
            return this.password;
        }

        public String getHostname() {
            return this.hostname;
        }

        public String getDatabase() {
            return this.database;
        }

        public int getPort() {
            return this.port;
        }

    }

}
