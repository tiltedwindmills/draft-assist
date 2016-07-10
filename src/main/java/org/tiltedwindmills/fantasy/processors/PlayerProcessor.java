package org.tiltedwindmills.fantasy.processors;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.tiltedwindmills.fantasy.mfl.model.Player;


public class PlayerProcessor {

	@Resource
	private List<Player> players;

	protected Map<String, Player> playerNameToPlayerMap;

	@PostConstruct
	private void postConstruct() {

		checkNotNull(players, "players list cannot be null");

		// load the players into a name-keyed map for later use.
		playerNameToPlayerMap = new HashMap<>();
		for (Player player : players) {

			if (player == null) {
				continue;
			}

			// the dots are a pain since DLF doesn't use them.  Kill them off.  Also move everything lower to cover
			// the "DeVante"'s of the world.  Oh, and kill apostrophe's for Le'Veon.
			final String playerKey = player.getName().replace(".", "").replace("'", "").toLowerCase();
			playerNameToPlayerMap.put(playerKey, player);
		}
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Map<String, Player> getPlayerNameToPlayerMap() {
		if (playerNameToPlayerMap == null) {
			playerNameToPlayerMap = new HashMap<>();
		}
		return playerNameToPlayerMap;
	}

	public void setPlayerNameToPlayerMap(Map<String, Player> playerNameToPlayerMap) {
		this.playerNameToPlayerMap = playerNameToPlayerMap;
	}
}
