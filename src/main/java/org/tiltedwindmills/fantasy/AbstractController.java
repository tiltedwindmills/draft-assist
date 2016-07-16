package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tiltedwindmills.fantasy.mfl.model.Player;


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
}
