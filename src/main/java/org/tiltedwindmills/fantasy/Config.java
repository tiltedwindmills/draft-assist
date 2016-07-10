package org.tiltedwindmills.fantasy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.tiltedwindmills.fantasy.mfl.model.Player;
import org.tiltedwindmills.fantasy.mfl.model.players.PlayerResponse;
import org.tiltedwindmills.fantasy.model.RankingList;
import org.tiltedwindmills.fantasy.model.Tier;
import org.tiltedwindmills.fantasy.processors.RankingsProcessor;
import org.tiltedwindmills.fantasy.processors.TierProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class Config {

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	@Bean
	public List<Player> players() {

		PlayerResponse playerResponse = null;
		try {
			final Resource resource = new ClassPathResource("data/mfl_players.json");
			final InputStream resourceInputStream = resource.getInputStream();

			final ObjectMapper mapper = new ObjectMapper();
			playerResponse = mapper.readValue(resourceInputStream, PlayerResponse.class);


		} catch (IOException e) {
			LOG.error("Failed to load MFL players from file: {}", e.getMessage());
		}

		if (playerResponse != null &&
			playerResponse.getWrapper() != null &&
			!CollectionUtils.isEmpty(playerResponse.getWrapper().getPlayers())) {

			LOG.debug("Found {} players", playerResponse.getWrapper().getPlayers().size());
			return playerResponse.getWrapper().getPlayers();
			//return switchOLBs(playerResponse.getWrapper().getPlayers());

		}

		LOG.debug("No players found.");
		return new ArrayList<>();
	}

	/*
	private List<Player> switchOLBs(final List<Player> players) {

		final List<String> linebackersToSwitch = new ArrayList<>();
		final Resource resource = new ClassPathResource("data/olbs.txt");

		try (
				InputStream resourceInputStream = resource.getInputStream();
				BufferedReader reader =  new BufferedReader(new InputStreamReader(resourceInputStream))) {

			String line = null;
			while ((line = reader.readLine()) != null) {
				linebackersToSwitch.add(line);
			}

		} catch (IOException x) {
			LOG.error("IOException: %s%n", x);
		}

		for (Player player : players) {

			if (linebackersToSwitch.contains(player.getName() + " " + player.getTeam())) {
				player.setPosition(Position.DEFENSIVE_END);
				LOG.debug("Switched {} to DL", player);
			}
		}

		return players;
	}
	*/

	@Bean
	public List<Tier> tiers(TierProcessor processor) {
		return processor.getTiers("tiers");
	}

	@Bean
	public List<RankingList> adpList(RankingsProcessor processor) {
		return processor.getRankings("adp");
	}

	@Bean
	public List<RankingList> overallRankings(RankingsProcessor processor) {
		return processor.getRankings("overall");
	}

	@Bean
	public List<RankingList> quarterbackRankings(RankingsProcessor processor) {
		return processor.getRankings("quarterbacks");
	}

	@Bean
	public List<RankingList> wideReceiverRankings(RankingsProcessor processor) {
		return processor.getRankings("wide-receivers");
	}

	@Bean
	public List<RankingList> runningBackRankings(RankingsProcessor processor) {
		return processor.getRankings("running-backs");
	}

	@Bean
	public List<RankingList> tightEndRankings(RankingsProcessor processor) {
		return processor.getRankings("tight-ends");
	}

	@Bean
	public List<RankingList> defensiveLineRankings(RankingsProcessor processor) {
		return processor.getRankings("defensive-line");
	}

	@Bean
	public List<RankingList> linebackerRankings(RankingsProcessor processor) {
		return processor.getRankings("linebackers");
	}

	@Bean
	public List<RankingList> defensiveBacksRankings(RankingsProcessor processor) {
		return processor.getRankings("defensive-back");
	}

	@Bean
	public List<RankingList> fourForFour(RankingsProcessor processor) {
		return processor.getRankings("4for4");
	}

	@Bean
	public List<RankingList> fantasypros(RankingsProcessor processor) {
		return processor.getRankings("fantasypros");
	}

//	@Bean
//	public List<Projection> sharkProjections(ProjectionsProcessor processor) {
//		return processor.getProjections("fantasy-sharks__20150608");
//	}

//	@Bean
//	public List<Projection> nortonProjections(ProjectionsProcessor processor) {
//		return processor.getProjections("norton__20150609");
//	}

//	@Bean
//	public List<Projection> pffProjections(ProjectionsProcessor processor) {
//		return processor.getProjections("pff__20150622");
//	}

}
