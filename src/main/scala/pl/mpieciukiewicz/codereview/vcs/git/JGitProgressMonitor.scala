package pl.mpieciukiewicz.codereview.vcs.git

import pl.mpieciukiewicz.codereview.web.TaskProgressMonitor

class JGitProgressMonitor(taskMonitor: TaskProgressMonitor) extends org.eclipse.jgit.lib.BatchingProgressMonitor {


  override def onEndTask(taskName: String, workCurr: Int, workTotal: Int, percentDone: Int): Unit = {
    taskMonitor.update(workTotal, workCurr, percentDone, taskName)
  }

  override def onUpdate(taskName: String, workCurr: Int, workTotal: Int, percentDone: Int): Unit = {
    taskMonitor.update(workTotal, workCurr, percentDone, taskName)
  }

  override def onEndTask(taskName: String, workCurr: Int): Unit = {
    taskMonitor.update(workCurr, workCurr, 0, taskName)
  }

  override def onUpdate(taskName: String, workCurr: Int): Unit = {
    taskMonitor.update(workCurr, workCurr, 0, taskName)
  }
}
