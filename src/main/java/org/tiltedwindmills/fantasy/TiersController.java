package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.Tier;
import org.tiltedwindmills.fantasy.processors.TierProcessor;

@Controller
public class TiersController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(TiersController.class);

	@Inject
	private TierProcessor tierProcessor;

	@Resource
	private List<Tier> tiers;

	@PostConstruct
	private void postConstruct() {

		checkNotNull(tierProcessor, "tierProcessor cannot be null");

		checkNotNull(players, "players list cannot be null");
		checkNotNull(tiers, "tiers list cannot be null");
	}


	@RequestMapping("/")
	public String tiers(final Map<String, Object> model) {

		final List<DraftPick> picks = picksCache.getDraftPicks(LEAGUE_ID, "65", 2016);
		tierProcessor.mergeDraft(tiers, picks);

		LOG.debug("Found {} tiers", tiers.size());
		model.put("tiers", tiers);
		return "tiers";
	}
}
