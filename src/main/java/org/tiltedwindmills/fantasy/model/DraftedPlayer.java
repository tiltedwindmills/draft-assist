package org.tiltedwindmills.fantasy.model;

import java.util.ArrayList;
import java.util.List;

import org.tiltedwindmills.fantasy.mfl.model.AbstractObject;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;


public class DraftedPlayer extends AbstractObject {

	private static final long serialVersionUID = -2382487464376917025L;

	private Player player;

	private int timesDrafted;

	private List<DraftPick> picks;

	private boolean doNotDraft;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTimesDrafted() {
		return timesDrafted;
	}

	public void setTimesDrafted(int timesDrafted) {
		this.timesDrafted = timesDrafted;
	}

	public boolean isDoNotDraft() {
		return doNotDraft;
	}

	public void setDoNotDraft(boolean doNotDraft) {
		this.doNotDraft = doNotDraft;
	}

	public void drafted() {
		timesDrafted++;
	}

	public List<DraftPick> getPicks() {
		if (picks == null) {
			picks = new ArrayList<>();
		}
		return picks;
	}

	public void setPicks(List<DraftPick> picks) {
		this.picks = picks;
	}
}
