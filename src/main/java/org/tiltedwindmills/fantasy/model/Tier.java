package org.tiltedwindmills.fantasy.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;
import org.tiltedwindmills.fantasy.mfl.model.Player;

public class Tier extends AbstractObject {

	private static final long serialVersionUID = 1343449901249065460L;

	private Set<DraftedPlayer> draftedPlayers;

	private boolean allDrafted;

	public Set<DraftedPlayer> getDraftedPlayers() {
		if (draftedPlayers == null) {
			draftedPlayers = new LinkedHashSet<>();
		}

		return draftedPlayers;
	}

	public void setDraftedPlayers(Set<DraftedPlayer> draftedPlayers) {
		this.draftedPlayers = draftedPlayers;
	}

	public void add(Player player) {

		if (draftedPlayers == null) {
			draftedPlayers = new LinkedHashSet<>();
		}

		if (player != null) {

			final DraftedPlayer draftedPlayer = new DraftedPlayer();
			draftedPlayer.setPlayer(player);
			draftedPlayer.setTimesDrafted(0);

			draftedPlayers.add(draftedPlayer);
		}
	}

	public final boolean isAllDrafted() {
		return allDrafted;
	}

	public final void setAllDrafted(final boolean allDrafted) {
		this.allDrafted = allDrafted;
	}
}
