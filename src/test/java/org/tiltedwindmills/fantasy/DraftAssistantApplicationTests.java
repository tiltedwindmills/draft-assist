package org.tiltedwindmills.fantasy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tiltedwindmills.fantasy.DraftAssistantApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DraftAssistantApplication.class)
@WebAppConfiguration
public class DraftAssistantApplicationTests {

	@Test
	public void contextLoads() {
	}

}
