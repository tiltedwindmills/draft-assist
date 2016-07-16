package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tiltedwindmills.fantasy.mfl.model.Franchise;
import org.tiltedwindmills.fantasy.mfl.model.League;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.mfl.services.LeagueService;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.PickStats;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;


/**
 * The Class StatsController.
 *
 * @author John Daniel
 */
@Controller
public class StatsController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(StatsController.class);

	@Inject
	private LeagueService leagueService;

	@Resource
	private List<Player> players;

	protected Map<String, Player> playerIdToPlayerMap;

	@PostConstruct
	private void postConstruct() {

		checkNotNull(players, "players list cannot be null");

		// load the players into a name-keyed map for later use.
		playerIdToPlayerMap = new HashMap<>();
		for (Player player : players) {

			if (player == null) {
				continue;
			}

			playerIdToPlayerMap.put(player.getId(), player);
		}
	}


	@RequestMapping("/predraft")
	public String predraft(final Map<String, Object> model) {

		final List<DraftPick> picks = picksCache.getDraftPicks(leagueId, serverId, year);

		League league = leagueService.getLeague(leagueId, serverId);

		Map<String, PickStats> franchiseNameToStatsMap = new HashMap<>();
		for (Franchise franchise : league.getFranchiseList().getFranchises()) {

			final PickStats pickStats = new PickStats();
			pickStats.setFranchiseId(franchise.getId());
			pickStats.setFranchiseName(franchise.getName());
			pickStats.setPredrafts(0);

			// seed the count
			franchiseNameToStatsMap.put(franchise.getId(), pickStats);
		}

		int preDraftCount = 0;

		for (int i = 0; i < picks.size(); i++) {

			final DraftPick pick = picks.get(i);

			if (pick != null && pick.getPlayerId() != "0") {

				final PickStats stats = franchiseNameToStatsMap.get(pick.getFranchise());


				if (StringUtils.defaultString(pick.getComments()).contains("Pick made based on Pre-Draft List")) {

					LOG.debug("Incrememnting {} pre-draft for '{}'", stats.getFranchiseName(), pick.getComments());
					stats.setPredrafts(stats.getPredrafts() + 1);

					preDraftCount++;
				}

				if (i != 0) {
					DateTime pickTime = new DateTime(Long.parseLong(pick.getTimestamp()) * 1000);
					DateTime previousPickTime = new DateTime(Long.parseLong(picks.get(i - 1).getTimestamp()) * 1000);

					int diff = Minutes.minutesBetween(previousPickTime, pickTime).getMinutes();

					stats.getMinutesForPicks().add(diff);
				}
				else {
					stats.getMinutesForPicks().add(0);
				}
			}
		}

		LOG.info("Found {} predrafts", preDraftCount);


		List<PickStats>franchiseStats = Lists.newArrayList(franchiseNameToStatsMap.values());

		final class StatsOrdering extends Ordering<PickStats> {

			@Override
			public int compare(PickStats s1, PickStats s2) {

				return s2.getPredrafts() - s1.getPredrafts();
			}
		}

		Collections.sort(franchiseStats, new StatsOrdering());



		// see docs for java.util.Formatter
		System.out.format("%-28s%-8s%-12s%20s\n", "Team", "Picks", "Predrafts", "Average Time");
		for (PickStats stats : franchiseStats) {
			System.out.format("%-28s%-8d%-12d%-20s\n",
					stats.getFranchiseName(),
					stats.getPicksCount(),
					stats.getPredrafts(),
					StringUtils.trim(stats.getAverageTime()));
		}


		model.put("franchiseStats", franchiseStats);
		return "predrafts";
	}


	@RequestMapping("/diff")
	public String diff(final Map<String, Object> model) {

		final List<DraftPick> picks = picksCache.getDraftPicks(leagueId, serverId, year);

		List<DraftedPlayer> playersDrafted = new ArrayList<>();


		for (final DraftPick pick : picks) {

			if (pick.getPlayerId() == "0") {
				continue;
			}

			Predicate<DraftedPlayer> playerMatch = new Predicate<DraftedPlayer>() {
				@Override
				public boolean apply(DraftedPlayer playerToMatch) {
					return playerToMatch != null &&
							playerToMatch.getPlayer() != null &&
							playerToMatch.getPlayer().getId() == pick.getPlayerId();
				}
			};

			List<DraftedPlayer> filteredPlayers = Lists.newArrayList(Collections2.filter(playersDrafted, playerMatch));

			if (filteredPlayers.isEmpty()) {
				DraftedPlayer draftedPlayer = new DraftedPlayer();
				draftedPlayer.setPlayer(playerIdToPlayerMap.get(pick.getPlayerId()));
				draftedPlayer.getPicks().add(pick);
				draftedPlayer.setTimesDrafted(1);

				playersDrafted.add(draftedPlayer);
			}
			else {
				filteredPlayers.get(0).getPicks().add(pick);
				filteredPlayers.get(0).drafted();
			}
		}


		Predicate<DraftedPlayer> twoDraftMatch = new Predicate<DraftedPlayer>() {
			@Override
			public boolean apply(DraftedPlayer playerToMatch) {
				return playerToMatch != null &&
						playerToMatch.getPicks().size() < 2;
			}
		};


		playersDrafted = Lists.newArrayList(Collections2.filter(playersDrafted, twoDraftMatch));

		final class OrderingByPick extends Ordering<DraftedPlayer> {

			@Override
			public int compare(DraftedPlayer s1, DraftedPlayer s2) {

				String round1 = s1.getPicks().get(0).getRound();
				String round2 = s2.getPicks().get(0).getRound();

				int roundCompare = Integer.valueOf(round1).compareTo(Integer.valueOf(round2));

				if (roundCompare != 0) {
					return roundCompare;
				}

				return Integer.valueOf(s1.getPicks().get(0).getPick()).compareTo(Integer.valueOf(s2.getPicks().get(0).getPick()));
			}
		}

		Collections.sort(playersDrafted, new OrderingByPick());
		model.put("onePicks", playersDrafted);
		return "diff";
	}


	class DraftPickWithPlayer {

		private DraftPick draftPick;

		private Player player;

		public DraftPick getDraftPick() {
			return draftPick;
		}

		public void setDraftPick(DraftPick draftPick) {
			this.draftPick = draftPick;
		}

		public Player getPlayer() {
			return player;
		}

		public void setPlayer(Player player) {
			this.player = player;
		}
	}

}
