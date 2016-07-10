package org.tiltedwindmills.fantasy.model;

import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;
import org.tiltedwindmills.fantasy.mfl.model.Player;


public class TieredPlayer extends AbstractObject {

	private Player player;

	private int tierNumber;

	private int rankInTier;

	public TieredPlayer(final Player player, final int tierNumber) {
		this.player = player;
		this.tierNumber = tierNumber;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTierNumber() {
		return tierNumber;
	}

	public void setTierNumber(int tierNumber) {
		this.tierNumber = tierNumber;
	}

	public int getRankInTier() {
		return rankInTier;
	}

	public void setRankInTier(int rankInTier) {
		this.rankInTier = rankInTier;
	}
}
