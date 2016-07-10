package org.tiltedwindmills.fantasy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tiltedwindmills.fantasy.mfl.model.draft.DraftPick;
import org.tiltedwindmills.fantasy.mfl.services.LeagueService;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache manager for MFL draft pick lookups.
 *
 * @author John Daniel
 */
@Component
public class PicksCacheManager {

	private static final Logger LOG = LoggerFactory.getLogger(PicksCacheManager.class);

	@Inject
	private LeagueService leagueService;

	private LoadingCache<LeagueCacheKey, List<DraftPick>> cache;

	@PostConstruct
	private void postConstruct() {

		CacheLoader<LeagueCacheKey, List<DraftPick>> loader = new CacheLoader<LeagueCacheKey, List<DraftPick>>() {

			@Override
			public List<DraftPick> load(final LeagueCacheKey key) {

				LOG.debug("Loading draft picks from MFL service with key '{}'.", key);
				List<DraftPick> draftPicks = leagueService.getDraftPicks(key.leagueId, key.serverId, key.year);
				return draftPicks;
			}
		};

		cache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build(loader);
	}


	/**
	 * Gets the draft picks.  Will return an empty List if the lookup fails.
	 *
	 * @param leagueId the league id
	 * @param serverId the server id
	 * @param year the year
	 * @return the draft picks
	 */
	public final List<DraftPick> getDraftPicks(final int leagueId, final String serverId, final int year) {

		try {
			return cache.get(new LeagueCacheKey(leagueId, serverId, year));

		} catch (ExecutionException e) {

			LOG.warn("Failed to load draft picks from cache.", e);
			return new ArrayList<>();
		}
	}


	public final void clearDraftPicks(final int leagueId, final String serverId, final int year) {

		LOG.debug("Clearing cache for {} league '{}'", year, leagueId);
		cache.invalidate(new LeagueCacheKey(leagueId, serverId, year));
	}



	/**
	 * Hackish little beast for using multiple values in the cache key.
	 */
	private class LeagueCacheKey {

		public LeagueCacheKey(final int leagueId, final String serverId, final int year) {
			this.leagueId = leagueId;
			this.serverId = serverId;
			this.year = year;
		}

		int leagueId;
		String serverId;
		int year;

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
