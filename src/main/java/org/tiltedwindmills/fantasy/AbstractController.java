package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;


@Component
public class AbstractController {

	@Inject
	protected PicksCacheManager picksCache;

	@Resource
	protected List<Player> players;

	@Value("${leagueId}")
	protected int leagueId;

	@Value("${serverId}")
	protected String serverId;

	@Value("${year}")
	protected int year;

	@Value("${myfranchise}")
	protected String myFranchise;

	@Value("${draftRounds}")
	protected String draftRounds;

	@Value("${teams}")
	protected String teams;


	@Value("#{'${donotdraft.qb}'.split(',')}")
	protected List<Integer> doNotDraftQBs;

	@Value("#{'${donotdraft.wr}'.split(',')}")
	protected List<Integer> doNotDraftWRs;

	@Value("#{'${donotdraft.rb}'.split(',')}")
	protected List<Integer> doNotDraftRBs;

	@Value("#{'${donotdraft.te}'.split(',')}")
	protected List<Integer> doNotDraftTEs;

	@Value("#{'${donotdraft.lb}'.split(',')}")
	protected List<Integer> doNotDraftLBs;


	@PostConstruct
	private void postConstruct() {

		checkNotNull(players, "players list cannot be null");
		checkNotNull(picksCache, "picksCache cannot be null");
	}


	protected List<Integer> getCombinedDoNotDraftList() {

		final List<Integer> doNotDraftOverall = new ArrayList<>();
		doNotDraftOverall.addAll(doNotDraftRBs);
		doNotDraftOverall.addAll(doNotDraftWRs);
		doNotDraftOverall.addAll(doNotDraftTEs);
		doNotDraftOverall.addAll(doNotDraftLBs);
		return doNotDraftOverall;
	}

	protected Map<String, Integer> getPositionDistributionMap(final List<DraftPick> picks) {

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
}
