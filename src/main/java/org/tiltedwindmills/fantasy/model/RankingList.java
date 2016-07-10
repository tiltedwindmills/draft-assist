package org.tiltedwindmills.fantasy.model;

import java.util.ArrayList;
import java.util.List;

import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;
import org.tiltedwindmills.fantasy.mfl.model.Player;

public class RankingList extends AbstractObject {

	private String name;

	private List<DraftedPlayer> rankedPlayers;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DraftedPlayer> getRankedPlayers() {
		return rankedPlayers;
	}

	public void setRankedPlayers(List<DraftedPlayer> rankedPlayers) {
		this.rankedPlayers = rankedPlayers;
	}

	public int getFirstUndraftedIndex() {

		if (rankedPlayers != null) {
			for (int i = 0; i < rankedPlayers.size(); i++) {
				if (rankedPlayers.get(i).getTimesDrafted() < 2 && !rankedPlayers.get(i).isDoNotDraft()) {
					return i;
				}
			}
		}

		return 0;
	}

	public boolean addPlayer(final Player player) {
		if (rankedPlayers == null) {
			rankedPlayers = new ArrayList<DraftedPlayer>();
		}

		final DraftedPlayer draftedPlayer = new DraftedPlayer();
		draftedPlayer.setPlayer(player);
		draftedPlayer.setTimesDrafted(0);
		return rankedPlayers.add(draftedPlayer);
	}
}
