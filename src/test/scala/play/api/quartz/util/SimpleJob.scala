package play.api.quartz.util

import org.quartz.Job
import org.quartz.JobExecutionContext

class SimpleJob extends Job {
  override def execute(args: JobExecutionContext): Unit = {
    println("This is a quartz job!")
  }
}
