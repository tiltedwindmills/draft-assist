package org.tiltedwindmills.fantasy.processors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.model.DraftedPlayer;
import org.tiltedwindmills.fantasy.model.Tier;

@Named
public class TierProcessor extends PlayerProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(TierProcessor.class);

	public List<Tier> getTiers(final String fileName) {

		LOG.info("Processing '{}' tiers", fileName);
		final List<Tier> tiers = new ArrayList<>();

		try {
			final org.springframework.core.io.Resource resource = new ClassPathResource("data/" + fileName + ".csv");
			final InputStream resourceInputStream = resource.getInputStream();

			final CSVParser parser = new CSVParser(new InputStreamReader(resourceInputStream), CSVFormat.EXCEL);
			final List<CSVRecord> records = parser.getRecords();

			// create the initial tier
			tiers.add(new Tier());

			for (int i = 0; i < parser.getRecordNumber(); i++) {

				final CSVRecord csvRecord = records.get(i);
				final Iterator<String> recordIterator = csvRecord.iterator();

				boolean newTierMarker = true;
				while (recordIterator.hasNext()) {

					final String playerName = recordIterator.next();
					if (StringUtils.isNotBlank(playerName)) {

						// TODO : the player name logic is in at least 3 places.  Consolidate.
						final Player player = getPlayerNameToPlayerMap().get(playerName.replace(".", "").replace("'", "").toLowerCase());
						tiers.get(tiers.size() - 1).add(player);

						newTierMarker = false;
					}
				}

				// if we didn't find any players, was a tier break
				if (newTierMarker) {
					tiers.add(new Tier());
				}
			}

			// we'd added an extra.  Get rid of it.
			tiers.remove(tiers.size() - 1);

			parser.close(); // TODO : close this correctly.

		} catch (IOException e) {
			LOG.error("Failed to load rankings from file: {}", e.getMessage());

		}

		return tiers;
	}

	public void mergeDraft(List<Tier> tiers, List<DraftPick> picks) {

		// iterate the rankings lists
		for (Tier tier : tiers) {

			tier.setAllDrafted(true);

			if (tier != null && !tier.getDraftedPlayers().isEmpty()) {

				// for each player on this list...
				for (DraftedPlayer draftedPlayer : tier.getDraftedPlayers()) {

					if (draftedPlayer != null && draftedPlayer.getPlayer() != null) {

						// first, clear any previous drafted status.
						draftedPlayer.setTimesDrafted(0);

						// then, iterate the picks and increment when taken
						for (DraftPick pick : picks) {
							if (pick != null && StringUtils.equals(pick.getPlayerId(), draftedPlayer.getPlayer().getId())) {
								draftedPlayer.drafted();
							}
						}

						if (draftedPlayer.getTimesDrafted() == 0) {
							tier.setAllDrafted(false);
						}
					}
				}
			}
		}
	}
}
