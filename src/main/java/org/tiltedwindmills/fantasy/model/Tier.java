package org.tiltedwindmills.fantasy.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;
import org.tiltedwindmills.fantasy.mfl.model.Player;

public class Tier extends AbstractObject {

	private static final long serialVersionUID = 1343449901249065460L;

	private Set<DraftedPlayer> draftedPlayers;

	public Set<DraftedPlayer> getDraftedPlayers() {
		if (draftedPlayers == null) {
			draftedPlayers = new LinkedHashSet<DraftedPlayer>();
		}

		return draftedPlayers;
	}

	public void setDraftedPlayers(Set<DraftedPlayer> draftedPlayers) {
		this.draftedPlayers = draftedPlayers;
	}

	public void add(Player player) {

		if (draftedPlayers == null) {
			draftedPlayers = new LinkedHashSet<DraftedPlayer>();
		}

		if (player != null) {

			final DraftedPlayer draftedPlayer = new DraftedPlayer();
			draftedPlayer.setPlayer(player);
			draftedPlayer.setTimesDrafted(0);

			draftedPlayers.add(draftedPlayer);
		}
	}
}
