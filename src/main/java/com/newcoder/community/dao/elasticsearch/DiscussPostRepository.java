package com.newcoder.community.dao.elasticsearch;

import com.newcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chenmin
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {
}
