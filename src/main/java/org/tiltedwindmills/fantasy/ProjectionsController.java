package org.tiltedwindmills.fantasy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.tiltedwindmills.fantasy.mfl.model.Position;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.Projection;
import org.tiltedwindmills.fantasy.processors.ProjectionsProcessor;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;


@Controller
public class ProjectionsController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectionsController.class);


	@Inject
	private ProjectionsProcessor projectionsProcessor;

//	@Resource
//	private List<Projection> sharkProjections;

//	@Resource
//	private List<Projection> nortonProjections;

//	@Resource
//	private List<Projection> pffProjections;

	@PostConstruct
	private void postConstruct() {

		checkNotNull(projectionsProcessor, "projectionsProcessor cannot be null");

//		checkNotNull(sharkProjections, "sharkProjections cannot be null");
//		checkNotNull(nortonProjections, "nortonProjections cannot be null");
//		checkNotNull(pffProjections, "pffProjections cannot be null");
	}

	/*
	@RequestMapping("/sharks")
	public String sharksProjections(
			final Map<String, Object> model,
			@RequestParam(value="position", required=false) final List<Position> positions,
			@RequestParam(value="hideDrafted", defaultValue = "false") final boolean hideDrafted) {

		return loadProjections(model, sharkProjections, getCombinedDoNotDraftList(), positions, hideDrafted);
	}

	@RequestMapping("/norton")
	public String nortonProjections(
			final Map<String, Object> model,
			@RequestParam(value="position", required=false) final List<Position> positions,
			@RequestParam(value="hideDrafted", defaultValue = "false") final boolean hideDrafted) {

		return loadProjections(model, nortonProjections, getCombinedDoNotDraftList(), positions, hideDrafted);
	}

	@RequestMapping("/pff")
	public String pffProjections(
			final Map<String, Object> model,
			@RequestParam(value="position", required=false) final List<Position> positions,
			@RequestParam(value="hideDrafted", defaultValue = "false") final boolean hideDrafted) {

		return loadProjections(model, pffProjections, getCombinedDoNotDraftList(), positions, hideDrafted);
	}
*/


	private String loadProjections(final Map<String, Object> model,
			final List<Projection> projections,
			final List<Integer> doNotDraftList,
			final List<Position> positions,
			final boolean hideDrafted) {

		final List<DraftPick> picks = picksCache.getDraftPicks(leagueId, serverId, year);
		projectionsProcessor.mergeDraft(projections, picks, doNotDraftList);

		List<Projection> filteredResults = Lists.newArrayList(projections);
		LOG.debug("Found {} projections", filteredResults.size());

		if (positions != null) {
			Predicate<Projection> hidePosition = new Predicate<Projection>() {
				@Override
				public boolean apply(Projection projection) {
					return projection != null &&
						projection.getPlayer() != null &&
						projection.getPlayer().getPlayer() != null &&
						positions.contains(projection.getPlayer().getPlayer().getPosition());
				}
			};

			filteredResults = Lists.newArrayList(Collections2.filter(filteredResults, hidePosition));
			LOG.debug("After filtering for position {}: Found {} projections", positions, filteredResults.size());
		}

		if (hideDrafted) {
			Predicate<Projection> hideDraftedGuys = new Predicate<Projection>() {
				@Override
				public boolean apply(Projection projection) {
					return projection != null && projection.getPlayer() != null && projection.getPlayer().getTimesDrafted() < 2;
				}
			};

			filteredResults = Lists.newArrayList(Collections2.filter(filteredResults, hideDraftedGuys));
			LOG.debug("After filtering for non-drafted: Found {} projections", filteredResults.size());
		}

		final class OrderingByScore extends Ordering<Projection> {

			@Override
			public int compare(Projection s1, Projection s2) {
				return Doubles.compare(s2.getPoints(), s1.getPoints());
			}
		}

		Collections.sort(filteredResults, new OrderingByScore());
		model.put("projections", filteredResults);
		return "projections";
	}
}
