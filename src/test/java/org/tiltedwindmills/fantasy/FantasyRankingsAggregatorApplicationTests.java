package org.tiltedwindmills.fantasy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tiltedwindmills.fantasy.FantasyRankingsAggregatorApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FantasyRankingsAggregatorApplication.class)
@WebAppConfiguration
public class FantasyRankingsAggregatorApplicationTests {

	@Test
	public void contextLoads() {
	}

}
