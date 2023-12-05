# Play-Quartz

The Play-Quartz module makes Quartz a first-class citizen of [Play](https://www.playframework.com). It consists of two features:

- Integration of Quartz into Play lifecycle;
- Simple shorthand API to the Quartz scheduler functionality.

## Current version

To use play-quartz, you need to add the following dependencies:

```scala
libraryDependencies += "io.github.eugenesy" %% "play-quartz" % "0.1.0"
```

## Getting started

Inject api dependency:

```scala
class Application @Inject()(quartzApi: QuartzSchedulerApi)
```

`QuartzSchedulerApi` provides helper methods for job scheduling.

```scala
class SimpleJob extends Job with Logging {
  @throws[JobExecutionException]
  override def execute(arg0: JobExecutionContext): Unit = {
    logger.error("This is a quartz job!")
  }
}

class Application @Inject()(quartzApi: QuartzSchedulerApi) {
  quartzApi.scheduleWithCron(job, 1.minute)
  quartzApi.scheduleWithCron(job, new CronExpression("* * 12 * * ?"))
  quartzApi.scheduleWithCron(job, "* * 12 * * ?")
}
```

In cases when more sophisticated functionality is required, the scheduler can be accessed directly:

```scala
class Application @Inject()(quartzApi: QuartzSchedulerApi) {
  quartzApi.scheduler
}
```

Or, otherwise, scheduler factory can be used to create additional schedulers:

```scala
class Application @Inject()(quartzApi: QuartzSchedulerApi) {
  val factory = quartzApi.schedulerFactory()
  val named = factory.getScheduler("named")
  factory.getAllSchedulers()
}
```

Note, that additional schedulers must be added to Play lifecycle or closed manually. 

## Copyright

License: Apache License 2.0, http://www.apache.org/licenses/LICENSE-2.0.html
