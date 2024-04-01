package io.github.eugenesy.play.api.quartz.injectable

import org.quartz.Job

/**
 * Quartz job interface indicating the requirement to inject dependencies by Guice. The job must have a constructor
 * without parameters. Injection must be done via setter- or field-injection.
 */
trait InjectableJob extends Job
