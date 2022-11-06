package fr.fistin.styx.lobby;

import fr.fistin.api.IFistinAPIProvider;
import fr.fistin.api.lobby.LobbyBalancer;
import fr.fistin.api.packet.DefaultChannels;
import fr.fistin.api.packet.model.PlayerSendPacket;
import fr.fistin.api.utils.FistinAPIException;
import fr.fistin.hydra.api.server.HydraServer;
import fr.fistin.hydra.api.server.HydraServerCreationInfo;
import fr.fistin.styx.Styx;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by AstFaster
 * on 06/11/2022 at 09:43
 */
public class SLobbyBalancer {

    public static final int LOBBY_SLOTS = 75;

    public SLobbyBalancer() {
        final ScheduledExecutorService executorService = Styx.get().getExecutorService();

        executorService.scheduleAtFixedRate(this::balance, 1, 1, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(this::manageLobbies, 5, 5, TimeUnit.SECONDS);
    }

    private void balance() {
        IFistinAPIProvider.fistinAPI().redis().process(jedis -> {
            for (HydraServer lobby : this.lobbies()) {
                jedis.zadd(LobbyBalancer.REDIS_KEY, lobby.getPlayers().size(), lobby.getName());
            }
        });
    }

    private void manageLobbies() {
        final List<HydraServer> lobbies = this.lobbies();
        final int players = this.lobbyPlayers();

        if (lobbies.size() == 0) {
            this.newLobby();
            return;
        }

        final int currentLobbies = lobbies.size();
        final int neededLobbies = (int) Math.ceil((double) players * 1.5 / LOBBY_SLOTS); // The "perfect" amount of lobbies needed

        if (currentLobbies > neededLobbies) {
            lobbies.sort(Comparator.comparingLong(HydraServer::getStartedTime)); // Compare server by their time started time: young servers are prioritized.

            for (int i = 0; i < currentLobbies - neededLobbies; i++) {
                final HydraServer lobby = lobbies.get(i);

                this.evacuateLobby(lobby); // Evacuate the lobby

                try { // Wait 1s for proxies to evacuate players
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    throw new FistinAPIException(e);
                }

                // Finally, stop the server
                IFistinAPIProvider.fistinAPI()
                        .hydra()
                        .getServersService()
                        .stopServer(lobby.getName());
            }
        } else if (currentLobbies < neededLobbies) {
            for (int i = 0; i < neededLobbies - currentLobbies; i++) {
                this.newLobby();
            }
        }
    }

    private void evacuateLobby(HydraServer lobby) {
        final List<HydraServer> lobbies = this.lobbies();
        final Queue<UUID> players = new LinkedBlockingQueue<>(lobby.getPlayers()); // Create a queue of players to evacuate

        lobbies.sort(Comparator.comparingInt(o -> o.getPlayers().size()));  // Sort lobbies by lower to greater amount of players

        for (HydraServer server : lobbies) {
            if (server.getName().equals(lobby.getName())) {
                continue;
            }

            for (int i = 0; i < server.getSlots() - server.getPlayers().size(); i++) { // For-each the available slots of the lobby
                if (players.size() == 0) { // No more players to evacuate
                    return;
                }

                final UUID player = players.poll(); // Remove a player from the queue (declared as evacuated)

                // Finally, evacuate the player
                IFistinAPIProvider.fistinAPI()
                        .packetManager()
                        .sendPacket(DefaultChannels.PLAYERS, new PlayerSendPacket(player, server.getName()));
            }
        }
    }

    private void newLobby() {
        final HydraServerCreationInfo lobbyInfo = new HydraServerCreationInfo()
                .withType("lobby")
                .withAccessibility(HydraServer.Accessibility.PUBLIC)
                .withProcess(HydraServer.Process.PERMANENT)
                .withSlots(LOBBY_SLOTS);

        IFistinAPIProvider.fistinAPI()
                .hydra()
                .getServersService()
                .createServer(lobbyInfo, null);
    }

    private int lobbyPlayers() {
        return IFistinAPIProvider.fistinAPI()
                .network()
                .counter()
                .getCategory("lobby")
                .getPlayers();
    }

    private List<HydraServer> lobbies() {
        return IFistinAPIProvider.fistinAPI()
                .hydra()
                .getServersService()
                .getServers("lobby")
                .stream()
                .filter(server -> server.getState() == HydraServer.State.READY)
                .toList();
    }

}
