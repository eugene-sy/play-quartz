package io.github.eugenesy.play.api.quartz.injectable

import org.quartz.Job

/**
 * Quartz job interface indicating the requirement to inject dependencies by Guice
 */
trait InjectableJob extends Job
