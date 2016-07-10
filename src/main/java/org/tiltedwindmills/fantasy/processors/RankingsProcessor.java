package org.tiltedwindmills.fantasy.processors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.RankingList;

@Named
public class RankingsProcessor extends PlayerProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(RankingsProcessor.class);


	public void mergeDraft(final List<RankingList> rankings, final List<DraftPick> picks,
			final List<Integer> doNotDraftList) {

		// iterate the rankings lists
		for (RankingList ranking : rankings) {
			if (ranking != null) {

				if (CollectionUtils.isEmpty(ranking.getRankedPlayers())) {
					LOG.warn("Ranking {} contains no players.  Skipping.", ranking.getName());
					//continue;
				}

				// for each player on this list...
				for (DraftedPlayer draftedPlayer : ranking.getRankedPlayers()) {

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
	}


	public List<RankingList> getRankings(final String fileName) {

		LOG.info("Processing '{}' rankings", fileName);
		final List<RankingList> rankings = new ArrayList<>();

		try {
			final org.springframework.core.io.Resource resource = new ClassPathResource("data/" + fileName + ".csv");
			final InputStream resourceInputStream = resource.getInputStream();

			CSVParser parser = new CSVParser(new InputStreamReader(resourceInputStream), CSVFormat.EXCEL);
			List<CSVRecord> records = parser.getRecords();

			// create the list of rankings we'll have
			for (int i = 0; i < records.get(0).size(); i++) {

				final RankingList list = new RankingList();
				list.setName(records.get(0).get(i));
				rankings.add(list);
			}

			for (int i = 1; i < parser.getRecordNumber(); i++) {

				// little hacky way to remove non-data rows ( "[, ,  ,,]", etc. ) from the input.
				// we go until we find an non-empty cell, then shoot off the processor.
				final Iterator<String> csvIterator = records.get(i).iterator();
				while (csvIterator.hasNext()) {
					if (StringUtils.isNotBlank(csvIterator.next())) {
						processRecord(rankings, records.get(i));
						break;
					}
				}
			}

			parser.close(); // TODO : close this correctly.

		} catch (IOException e) {
			LOG.error("Failed to load rankings from file: {}", e.getMessage());

		}

		// remove empty guys.  Sometimes trailing commas get left on the CSV files.  Clear those out.
		final Iterator<RankingList> i = rankings.iterator();
		while (i.hasNext()) {

			final RankingList list = i.next();

			if (CollectionUtils.isEmpty(list.getRankedPlayers())) {
				i.remove();
			}
		}

		return rankings;
	}

	private void processRecord(List<RankingList> rankings, CSVRecord csvRecord) {

		if (csvRecord.size() != rankings.size()) {
			LOG.warn("Found Unmatched rankings row @ record {}", csvRecord.getRecordNumber());
		}

		for (int i = 0; i < rankings.size(); i++) {

			final String playerName = csvRecord.get(i).replace(".", "").replace("'", "").toLowerCase();
			final Player player = getPlayerNameToPlayerMap().get(playerName.replace(".", ""));

			if (player != null) {
				rankings.get(i).addPlayer(player);
				continue;
			}

			if (StringUtils.isNotBlank(playerName)) {
				LOG.warn("Could not find matching player for name '{}' in ranking {}, row {}", playerName, i+1, csvRecord.getRecordNumber());
			}
		}
	}


	public void mergePlayers(List<RankingList> rankings, Map<Integer, String> playerToStatusMap, List<Integer> doNotDraftList) {

		// iterate the rankings lists
		for (RankingList ranking : rankings) {
			if (ranking != null) {

				// for each player on this list...
				for (DraftedPlayer draftedPlayer : ranking.getRankedPlayers()) {

					if (draftedPlayer != null && draftedPlayer.getPlayer() != null) {

						// first, clear any previous drafted status.
						draftedPlayer.setTimesDrafted(0);

						// then, iterate the picks and increment when taken
						for (Integer playerId : playerToStatusMap.keySet()) {
							if (playerId != null &&
								playerId == Integer.parseInt(draftedPlayer.getPlayer().getId()) &&
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
