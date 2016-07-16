package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.Tier;
import org.tiltedwindmills.fantasy.model.TieredPlayer;
import org.tiltedwindmills.fantasy.processors.TierProcessor;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DraftController extends AbstractController {

	@Inject
	private TierProcessor tierProcessor;

	@Resource
	private List<Tier> tiers;

	@PostConstruct
	private void postConstruct() {

		checkNotNull(tierProcessor, "tierProcessor cannot be null");
		checkNotNull(tiers, "tiers cannot be null");

		checkNotNull(players, "players list cannot be null");
	}


	@RequestMapping("/draft")
	public String draft(final Map<String, Object> model) {

		final List<DraftPick> picks = picksCache.getDraftPicks(leagueId, serverId, year);

		final Map<String, Integer> positionMap = getPositionDistributionMap(picks);

		int draftedPlayerCount = 0;
		for (Entry<String, Integer> position : positionMap.entrySet()) {
			draftedPlayerCount += position.getValue();
		}
		log.debug("Found {} completed draft picks.", draftedPlayerCount);

		model.put("draftedPlayerCount", draftedPlayerCount);
		model.put("draftedPositionMap", positionMap);

		// calculate the time remaining
		long firstPickTime = new Long(StringUtils.defaultIfBlank(picks.get(0).getTimestamp(), "0")) * 1000;
		long totalTime = new Instant().getMillis() - firstPickTime;
		long averageMsPerPick = totalTime / (draftedPlayerCount == 0 ? 1 : draftedPlayerCount);
		long estimatedRemainingMs = averageMsPerPick * (picks.size() - draftedPlayerCount);


		final Duration remainingDuration = new Duration(estimatedRemainingMs);
		final Instant estimatedEnd = new Instant().plus(remainingDuration);
		model.put("estimatedEnd", estimatedEnd.toDate());
		model.put("averageTimePerPick", averageMsPerPick);

		// get the tiered players until next pick.

		// step 1: how many picks until next?
//		int myNextPick = 0;
//		for (int i = draftedPlayerCount; i < picks.size(); i++) {
//			if (StringUtils.equals(picks.get(i).getFranchise(), myFranchise)) {
//				myNextPick = i - draftedPlayerCount;
//				break;
//			}
//		}
//		LOG.debug("My next pick is {} spots away", myNextPick);

		tierProcessor.mergeDraft(tiers, picks);

		final List<TieredPlayer> tieredPlayers = new ArrayList<>();
		for (int tierIndex = 0; tierIndex < tiers.size(); tierIndex++) {

			final Tier tier = tiers.get(tierIndex);

			// if we've found enough players to make it to my next selection.
//			if (myNextPick < 0) {
//				break;
//			}

			// otherwise, load the players from this tier into the list.
			if (tier != null && tier.getDraftedPlayers() != null) {
				for (DraftedPlayer draftedPlayer : tier.getDraftedPlayers()) {
					if (draftedPlayer.getTimesDrafted() == 0) {
						log.debug("Adding '{}' to draft list.", draftedPlayer.getPlayer());
						tieredPlayers.add(new TieredPlayer(draftedPlayer.getPlayer(), tierIndex));
	//					myNextPick--;
					}
				}
			}
		}

		model.put("tieredPlayers", tieredPlayers);
		model.put("picks", picks);
		model.put("teams", teams);
		model.put("draftRounds", draftRounds);
		model.put("myfranchise", myFranchise);
		return "draft";
	}
}
