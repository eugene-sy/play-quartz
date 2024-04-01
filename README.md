# Play-Quartz

The Play-Quartz module makes Quartz a first-class citizen of [Play](https://www.playframework.com). It consists of two features:

- Integration of Quartz into Play lifecycle;
- Simple shorthand API to the Quartz scheduler functionality.

## Current version

To use play-quartz, you need to add the following dependencies:

```scala
libraryDependencies += "io.github.eugene-sy" %% "play-quartz" % "0.0.2"
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

## Guice support

The Quartz jobs must have a parameterless constructor. 
Quartz initializes the job classes and ignores setters, fields, and annotations provided in the class definition. 

To overcome the problem the module provides the custom Quartz job factory (`InjectableJobFactory`) and the job interface (`InjectableJob`)
that indicates the need for special handling.

Note, that since Quartz uses the parameterless constructor to instantiate the job classes the constructor injection cannot be used.
The setter or field injection must be used for the dependencies.

```scala
class SimpleJob extends InjectableJob with Logging {
  
  // field injection
  @Inject()
  var serviceA: ServiceA = null
 
  var serviceB: ServiceB = null
  
  // setter injection
  @Inject()
  def setServiceB(b: ServiceB) = {
    this.serviceB = b
  }
 
  @throws[JobExecutionException]
  override def execute(arg0: JobExecutionContext): Unit = {
    logger.error("This is a quartz job!")
  }
}
```

The usage of `null` in scala code is generally not advised. It is used in example code to keep it short.
Substitute the empty state of the injectable dependencies with a different approach suitable for your situation.

## Changelog

* v0.0.3 -- Guice dependency injection support
* v0.0.2 -- Play 3.0 support
* v0.0.1 -- proof of concept, supports Play 2.9

## Copyright

License: Apache License 2.0, http://www.apache.org/licenses/LICENSE-2.0.html
