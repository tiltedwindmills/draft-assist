package org.tiltedwindmills.fantasy.processors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.Projection;
import org.tiltedwindmills.fantasy.model.RankingList;

@Named
public class ProjectionsProcessor extends PlayerProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectionsProcessor.class);


	public void mergeDraft(final List<Projection> projections, final List<DraftPick> picks,
			final List<Integer> doNotDraftList) {

		// iterate the rankings lists
		for (Projection projection : projections) {
			if (projection != null) {

				final DraftedPlayer draftedPlayer = projection.getPlayer();
				if (draftedPlayer != null && draftedPlayer.getPlayer() != null) {

					// first, clear any previous drafted status.
					draftedPlayer.setTimesDrafted(0);

					// then, iterate the picks and increment when taken
					for (DraftPick pick : picks) {
						if (pick != null && pick.getPlayerId() == draftedPlayer.getPlayer().getId())
							draftedPlayer.drafted();
					}

					if (doNotDraftList != null && doNotDraftList.contains(draftedPlayer.getPlayer().getId())) {
						draftedPlayer.setDoNotDraft(true);
						continue;
					}
				}
			}
		}
	}


	public List<Projection> getProjections(final String fileName) {

		LOG.info("Processing '{}' projections", fileName);
		final List<Projection> projections = new ArrayList<Projection>();

		try {
			final org.springframework.core.io.Resource resource = new ClassPathResource("data/" + fileName + ".csv");
			final InputStream resourceInputStream = resource.getInputStream();

			CSVParser parser = new CSVParser(new InputStreamReader(resourceInputStream), CSVFormat.EXCEL);
			List<CSVRecord> records = parser.getRecords();

			// create the list of rankings we'll have, skip the headers row
			for (CSVRecord record : records) {

				final Projection projection = new Projection();

				final Player player = getPlayerNameToPlayerMap().get(record.get(0));
				if (player == null) {
					LOG.warn("Could not find player for record {}", record);
					continue;
				}

				if (player != null) {
					final DraftedPlayer draftedPlayer = new DraftedPlayer();
					draftedPlayer.setPlayer(player);
					draftedPlayer.setTimesDrafted(0);
					projection.setPlayer(draftedPlayer);
				}

				projection.setSacks(NumberUtils.toDouble(record.get(3)));
				projection.setForcedFumbles(NumberUtils.toDouble(record.get(4)));
				projection.setFumblesRecovered(NumberUtils.toDouble(record.get(5)));
				projection.setInterceptions(NumberUtils.toDouble(record.get(6)));
				projection.setPassesDefensed(NumberUtils.toDouble(record.get(7)));
				projection.setTackles(NumberUtils.toDouble(record.get(8)));
				projection.setAssists(NumberUtils.toDouble(record.get(9)));
				projection.setTouchdowns(NumberUtils.toDouble(record.get(10)));

				projections.add(projection);
			}

			parser.close(); // TODO : close this correctly.

		} catch (IOException e) {
			LOG.error("Failed to load rankings from file: {}", e.getMessage());

		}

		return projections;
	}


	public void mergePlayers(List<RankingList> rankings, Map<String, String> playerToStatusMap, List<Integer> doNotDraftList) {

		// iterate the rankings lists
		for (RankingList ranking : rankings) {
			if (ranking != null) {

				// for each player on this list...
				for (DraftedPlayer draftedPlayer : ranking.getRankedPlayers()) {

					if (draftedPlayer != null && draftedPlayer.getPlayer() != null) {

						// first, clear any previous drafted status.
						draftedPlayer.setTimesDrafted(0);

						// then, iterate the picks and increment when taken
						for (String playerId : playerToStatusMap.keySet()) {
							if (playerId != null &&
								playerId == draftedPlayer.getPlayer().getId() &&
								!"Free Agent".equals(playerToStatusMap.get(playerId))) {

								draftedPlayer.drafted();
							}
						}

						if (doNotDraftList != null && doNotDraftList.contains(draftedPlayer.getPlayer().getId())) {
							draftedPlayer.setDoNotDraft(true);
							continue;
						}
					}
				}
			}
		}
	}
}
