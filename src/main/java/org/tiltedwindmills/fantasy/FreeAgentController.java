package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.services.LeagueService;
import org.tiltedwindmills.fantasy.mfl.services.PlayerService;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.RankingList;
import org.tiltedwindmills.fantasy.model.Tier;
import org.tiltedwindmills.fantasy.processors.RankingsProcessor;
import org.tiltedwindmills.fantasy.processors.TierProcessor;


/**
 * The Class FreeAgentController.
 *
 * @author John Daniel
 */
@Controller
@RequestMapping("/fa")
public class FreeAgentController {

	private static final Logger LOG = LoggerFactory.getLogger(FreeAgentController.class);

	private static final int LEAGUE_ID = 20428;
	private static final String SERVER_ID = "3";

	@Inject
	private PlayerService playerService;

	@Inject
	private LeagueService leagueService;

	@Inject
	private RankingsProcessor rankingsProcessor;

	@Inject
	private TierProcessor tierProcessor;

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
	private List<Tier> tiers;


	@Resource
	private List<Player> players;

	@Value("#{'${donotdraft.qb}'.split(',')}")
	private List<Integer> doNotDraftQBs;

	@Value("#{'${donotdraft.wr}'.split(',')}")
	private List<Integer> doNotDraftWRs;

	@Value("#{'${donotdraft.rb}'.split(',')}")
	private List<Integer> doNotDraftRBs;

	@Value("#{'${donotdraft.te}'.split(',')}")
	private List<Integer> doNotDraftTEs;

	@Value("#{'${donotdraft.lb}'.split(',')}")
	private List<Integer> doNotDraftLBs;


	@Value("${myfranchise}")
	private String myFranchise;

	@PostConstruct
	private void postConstruct() {

		checkNotNull(leagueService, "leagueService cannot be null");
		checkNotNull(playerService, "playerService cannot be null");

		checkNotNull(rankingsProcessor, "rankingsProcessor cannot be null");
		checkNotNull(tierProcessor, "tierProcessor cannot be null");

		checkNotNull(quarterbackRankings, "quarterbackRankings cannot be null");
		checkNotNull(runningBackRankings, "runningBackRankings cannot be null");
		checkNotNull(wideReceiverRankings, "wideReceiverRankings cannot be null");
		checkNotNull(tightEndRankings, "tightEndRankings cannot be null");
		checkNotNull(defensiveLineRankings, "defensiveLineRankings cannot be null");
		checkNotNull(linebackerRankings, "linebackerRankings cannot be null");
		checkNotNull(defensiveBacksRankings, "defensiveBacksRankings cannot be null");

		checkNotNull(players, "players list cannot be null");
	}


	@RequestMapping("/adp")
	public String adp(final Map<String, Object> model) {

		final List<Integer> doNotDraftOverall = new ArrayList<Integer>();
		doNotDraftOverall.addAll(doNotDraftRBs);
		doNotDraftOverall.addAll(doNotDraftWRs);
		doNotDraftOverall.addAll(doNotDraftTEs);
		doNotDraftOverall.addAll(doNotDraftLBs);

		model.put("overall", true);
		return loadRankings(model, adpList, doNotDraftOverall);
	}


	@RequestMapping("/overall")
	public String overall(final Map<String, Object> model) {

		final List<Integer> doNotDraftOverall = new ArrayList<Integer>();
		doNotDraftOverall.addAll(doNotDraftRBs);
		doNotDraftOverall.addAll(doNotDraftWRs);
		doNotDraftOverall.addAll(doNotDraftTEs);
		doNotDraftOverall.addAll(doNotDraftLBs);

		model.put("overall", true);
		return loadRankings(model, overallRankings, doNotDraftOverall);
	}

	@RequestMapping("/qb")
	public String quarterbacks(final Map<String, Object> model) {
		return loadRankings(model, quarterbackRankings, doNotDraftQBs);
	}

	@RequestMapping("/wr")
	public String receivers(final Map<String, Object> model) {
		return loadRankings(model, wideReceiverRankings, doNotDraftWRs);
	}

	@RequestMapping("/rb")
	public String runners(final Map<String, Object> model) {
		return loadRankings(model, runningBackRankings, doNotDraftRBs);
	}

	@RequestMapping("/te")
	public String tightEnds(final Map<String, Object> model) {
		return loadRankings(model, tightEndRankings, doNotDraftTEs);
	}

	@RequestMapping("/dl")
	public String defensiveLinemen(final Map<String, Object> model) {
		return loadRankings(model, defensiveLineRankings, null);
	}

	@RequestMapping("/lb")
	public String linebackers(final Map<String, Object> model) {
		return loadRankings(model, linebackerRankings, doNotDraftLBs);
	}

	@RequestMapping("/db")
	public String defensivebacks(final Map<String, Object> model) {
		return loadRankings(model, defensiveBacksRankings, null);
	}

	private String loadRankings(final Map<String, Object> model,
								final List<RankingList> rankings,
								final List<Integer> doNotDraftList) {

		// NPEs all over.
		Set<Integer> playerIds = new LinkedHashSet<>();
		for (RankingList ranking : rankings) {
			for (DraftedPlayer draftedPlayer : ranking.getRankedPlayers()) {
				playerIds.add(Integer.parseInt(draftedPlayer.getPlayer().getId()));
			}
		}

		final Map<Integer, String> playerToStatusMap = playerService.getPlayerAvailability(LEAGUE_ID, playerIds, SERVER_ID, 2015);
		rankingsProcessor.mergePlayers(rankings, playerToStatusMap, doNotDraftList);

		int recordCount = 0;
		final List<Integer> firstDraftedIndices = new ArrayList<Integer>();

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
		return "rankings";
	}
}