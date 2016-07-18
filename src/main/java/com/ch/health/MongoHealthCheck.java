package com.ch.health;

import com.ch.helpers.MongoHelper;
import com.codahale.metrics.health.HealthCheck;
import com.mongodb.Mongo;

/**
 * Created by elliott.jenkins on 30/06/2016.
 */
public class MongoHealthCheck extends HealthCheck {

  private final Mongo mongo = MongoHelper.getInstance().getMongoClient();

  @Override
  protected Result check() throws Exception {
    if (mongo.getConnectPoint() == null) {
      return HealthCheck.Result.unhealthy("Cannot connect to "
        + mongo.getUsedDatabases());
    } else {
      return Result.healthy();
    }
  }
}


