package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.Position;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.RankingList;
import org.tiltedwindmills.fantasy.model.Tier;
import org.tiltedwindmills.fantasy.model.TieredPlayer;
import org.tiltedwindmills.fantasy.processors.RankingsProcessor;
import org.tiltedwindmills.fantasy.processors.TierProcessor;


/**
 * The Class RankingsController.
 *
 * @author John Daniel
 */
@Controller
public class RankingsController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(RankingsController.class);


	@Inject
	private RankingsProcessor rankingsProcessor;

	@Inject
	private TierProcessor tierProcessor;

	@Resource
	private List<Tier> tiers;

	@Resource
	private List<RankingList> adpList;

	@Resource
	private List<RankingList> overallRankings;

	@Resource
	private List<RankingList> quarterbackRankings;

	@Resource
	private List<RankingList> runningBackRankings;

	@Resource
	private List<RankingList> wideReceiverRankings;

	@Resource
	private List<RankingList> tightEndRankings;

	@Resource
	private List<RankingList> defensiveLineRankings;

	@Resource
	private List<RankingList> linebackerRankings;

	@Resource
	private List<RankingList> defensiveBacksRankings;

	@Resource
	private List<RankingList> fourForFour;

	@Resource
	private List<RankingList> fantasypros;



	@PostConstruct
	private void postConstruct() {

		checkNotNull(rankingsProcessor, "rankingsProcessor cannot be null");
		checkNotNull(tierProcessor, "tierProcessor cannot be null");

		checkNotNull(quarterbackRankings, "quarterbackRankings cannot be null");
		checkNotNull(runningBackRankings, "runningBackRankings cannot be null");
		checkNotNull(wideReceiverRankings, "wideReceiverRankings cannot be null");
		checkNotNull(tightEndRankings, "tightEndRankings cannot be null");
		checkNotNull(defensiveLineRankings, "defensiveLineRankings cannot be null");
		checkNotNull(linebackerRankings, "linebackerRankings cannot be null");
		checkNotNull(defensiveBacksRankings, "defensiveBacksRankings cannot be null");

		checkNotNull(fourForFour, "fourForFour cannot be null");
		checkNotNull(fantasypros, "dodds cannot be null");

		checkNotNull(players, "players list cannot be null");
	}

	@RequestMapping("/draft")
	public String draft(final Map<String, Object> model) {

		final List<DraftPick> picks = picksCache.getDraftPicks(leagueId, serverId, year);

		final Map<String, Integer> positionMap = getPositionDistributionMap(picks);

		int draftedPlayerCount = 0;
		for (String position : positionMap.keySet()) {
			draftedPlayerCount += positionMap.get(position);
		}
		LOG.debug("Found {} completed draft picks.", draftedPlayerCount);

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
						LOG.debug("Adding '{}' to draft list.", draftedPlayer.getPlayer());
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


	private Map<String, Integer> getPositionDistributionMap(final List<DraftPick> picks) {
		final Map<String, Integer> positionMap = new HashMap<>();

		// get the number of completed picks.
		for (DraftPick pick : picks) {
			if (!StringUtils.equals(pick.getPlayerId(), "0")) {
				loadPositionMap(positionMap, pick);
			}
			else {
				break;
			}
		}
		return positionMap;
	}


	private void loadPositionMap(Map<String, Integer> positionMap, DraftPick pick) {

		// holy inefficient, batman.
		for (Player player : players) {

			if (player != null && StringUtils.equals(player.getId(), pick.getPlayerId()) && player.getPosition() != null) {

				final String playersPosition = player.getPosition().getType();
				int positionTotal = positionMap.containsKey(playersPosition) ? positionMap.get(playersPosition) + 1 : 1;
				positionMap.put(playersPosition, positionTotal);
			}
		}
	}


	@RequestMapping("/listplayers")
	public String loadPlayers(final Map<String, Object> model) throws IOException {

		model.put("players", players);
		return "playerlist";
	}


	@RequestMapping("/adp")
	public String adp(final Map<String, Object> model) {

		model.put("overall", true);
		return loadRankings(model, adpList, getCombinedDoNotDraftList());
	}

	@RequestMapping("/overall")
	public String overall(final Map<String, Object> model) {

		model.put("overall", true);
		return loadRankings(model, overallRankings, getCombinedDoNotDraftList());
	}

	@RequestMapping("/qb")
	public String quarterbacks(final Map<String, Object> model) {
		return loadRankings(model, quarterbackRankings, doNotDraftQBs, Position.QUARTERBACK);
	}

	@RequestMapping("/wr")
	public String receivers(final Map<String, Object> model) {
		return loadRankings(model, wideReceiverRankings, doNotDraftWRs, Position.WIDE_RECEIVER);
	}

	@RequestMapping("/rb")
	public String runners(final Map<String, Object> model) {
		return loadRankings(model, runningBackRankings, doNotDraftRBs, Position.RUNNING_BACK);
	}

	@RequestMapping("/te")
	public String tightEnds(final Map<String, Object> model) {
		return loadRankings(model, tightEndRankings, doNotDraftTEs, Position.TIGHT_END);
	}

	@RequestMapping("/dl")
	public String defensiveLinemen(final Map<String, Object> model) {
		return loadRankings(model, defensiveLineRankings, null, Position.DEFENSIVE_END, Position.DEFENSIVE_TACKLE);
	}

	@RequestMapping("/lb")
	public String linebackers(final Map<String, Object> model) {
		return loadRankings(model, linebackerRankings, doNotDraftLBs, Position.LINEBACKER);
	}

	@RequestMapping("/db")
	public String defensivebacks(final Map<String, Object> model) {
		return loadRankings(model, defensiveBacksRankings, null, Position.SAFETY, Position.CORNERBACK);
	}

	@RequestMapping("/4for4")
	public String fourForFour(final Map<String, Object> model) {
		return loadRankings(model, fourForFour, getCombinedDoNotDraftList());
	}

	@RequestMapping("/dodds")
	public String dodds(final Map<String, Object> model) {
		return loadRankings(model, fantasypros, getCombinedDoNotDraftList());
	}

	private String loadRankings(final Map<String, Object> model,
								final List<RankingList> rankings,
								final List<Integer> doNotDraftList) {

		return loadRankings(model, rankings, doNotDraftList, (Position) null);
	}

	private String loadRankings(final Map<String, Object> model,
								final List<RankingList> rankings,
								final List<Integer> doNotDraftList,
								final Position... positions) {

		final List<DraftPick> picks = picksCache.getDraftPicks(leagueId, serverId, year);

		// if we have a set of positions to count...
		if (positions != null && positions.length != 0) {

			// ... get the mapping of all positions...
			int positionCount =  0;
			final Map<String, Integer> positionMap = getPositionDistributionMap(picks);

			// ... then count each one in our list...
			for (Position position : positions) {
				if (position != null && positionMap.containsKey(position.getType())) {
					LOG.debug("Found {} drafted {}s", positionMap.get(position.getType()), position);
					positionCount += positionMap.get(position.getType());
				}
			}

			// finally add the result to our model.
			model.put("positionCount", positionCount);
		}

		rankingsProcessor.mergeDraft(rankings, picks, doNotDraftList);

		int recordCount = 0;
		final List<Integer> firstDraftedIndices = new ArrayList<>();

		for (RankingList ranking : rankings) {
			if (ranking != null && ranking.getRankedPlayers() != null) {

				// update the record count if necessary
				if (ranking.getRankedPlayers().size() > recordCount) {
					recordCount = ranking.getRankedPlayers().size();
				}

				firstDraftedIndices.add(ranking.getFirstUndraftedIndex());
			}
		}

		final int startRecord = Math.max(0, Collections.min(firstDraftedIndices) - 5);
		LOG.debug("Starting at record {}", startRecord);

		model.put("startRecord", startRecord);
		model.put("recordCount", recordCount);
		model.put("rankings", rankings);
		return "rankings"; // TODO : don't like that this returns the view name, even though fixing would be repetitive
	}
}