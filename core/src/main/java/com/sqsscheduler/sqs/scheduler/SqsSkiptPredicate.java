package com.sqsscheduler.sqs.scheduler;

import com.sqsscheduler.sqs.verticle.SqsVerticle;
import io.quarkus.arc.All;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SqsSkiptPredicate implements Scheduled.SkipPredicate {

    @All
    @Inject
    List<SqsVerticle> sqsVerticles;

    @Override
    public boolean test(ScheduledExecution execution) {
        return sqsVerticles.isEmpty();
    }
}
