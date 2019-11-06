package com.lazhu.core.support.jedis;

import redis.clients.jedis.ShardedJedis;

/**
 * @author naxj
 * 
 */
public interface Executor<K> {
	public K execute(ShardedJedis jedis);
}
