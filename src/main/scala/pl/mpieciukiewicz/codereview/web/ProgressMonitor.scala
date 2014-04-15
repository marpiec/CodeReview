package pl.mpieciukiewicz.codereview.web

class TaskProgressMonitor {

  // No public readers because this object will be serialized to json and parsed by client

  private var totalWork:Int = 1 // so no divide by zero exception will occur
  private var completed:Int = 0
  private var percentDone: Int = 0
  private var taskName: String = ""

  def update(totalWork:Int, completed:Int, percentDone: Int, taskName: String) {
    this.totalWork = totalWork
    this.completed = completed
    this.percentDone = percentDone
    this.taskName = taskName
  }

}

case class TaskMonitorWithId(monitorId: String, monitor: TaskProgressMonitor)


class ProgressMonitor {

  var counter = 0

  private var tasks: Map[String, TaskProgressMonitor] = Map()

  def createTaskProgressMonitor():TaskMonitorWithId = synchronized {
    val monitor = new TaskProgressMonitor
    val mapping = counter.toString -> monitor
    tasks += mapping
    counter += 1
    TaskMonitorWithId(mapping._1, mapping._2)
  }

  def getTaskMonitor(id: String):Option[TaskProgressMonitor] = synchronized {
    tasks.get(id)
  }

  def removeTaskMonitor(id: String):Unit = synchronized {
    tasks -= id
  }

}
