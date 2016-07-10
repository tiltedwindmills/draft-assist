package org.tiltedwindmills.fantasy.model;

import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;
import org.tiltedwindmills.fantasy.mfl.model.Position;

public class Projection extends AbstractObject {

	private static final long serialVersionUID = -7374139400718405335L;

	private DraftedPlayer player;

	private double sacks;

	private double forcedFumbles;

	private double fumblesRecovered;

	private double interceptions;

	private double passesDefensed;

	private double tackles;

	private double assists;

	private double touchdowns;

	public DraftedPlayer getPlayer() {
		return player;
	}

	public void setPlayer(DraftedPlayer player) {
		this.player = player;
	}

	public double getSacks() {
		return sacks;
	}

	public void setSacks(double sacks) {
		this.sacks = sacks;
	}

	public double getForcedFumbles() {
		return forcedFumbles;
	}

	public void setForcedFumbles(double forcedFumbles) {
		this.forcedFumbles = forcedFumbles;
	}

	public double getFumblesRecovered() {
		return fumblesRecovered;
	}

	public void setFumblesRecovered(double fumblesRecovered) {
		this.fumblesRecovered = fumblesRecovered;
	}

	public double getInterceptions() {
		return interceptions;
	}

	public void setInterceptions(double interceptions) {
		this.interceptions = interceptions;
	}

	public double getPassesDefensed() {
		return passesDefensed;
	}

	public void setPassesDefensed(double passesDefensed) {
		this.passesDefensed = passesDefensed;
	}

	public double getTackles() {
		return tackles;
	}

	public void setTackles(double tackles) {
		this.tackles = tackles;
	}

	public double getAssists() {
		return assists;
	}

	public void setAssists(double assists) {
		this.assists = assists;
	}

	public double getTouchdowns() {
		return touchdowns;
	}

	public void setTouchdowns(double touchdowns) {
		this.touchdowns = touchdowns;
	}

	public double getPoints() {

		return
			interceptions * 4 +
			forcedFumbles * 2 +
			fumblesRecovered * 2 +
			passesDefensed * 1 +
			tackles * (player.getPlayer().getPosition() == Position.LINEBACKER ? 1.5 : 2) +
			assists * (player.getPlayer().getPosition() == Position.LINEBACKER ? .75 : 1) +
			sacks * 3 +
			touchdowns * 6;
	}
}
