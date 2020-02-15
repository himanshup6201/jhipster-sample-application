package com.mycompany.myapp.repository.search;

import com.mycompany.myapp.domain.AppUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link AppUser} entity.
 */
public interface AppUserSearchRepository extends ElasticsearchRepository<AppUser, Long> {
}
