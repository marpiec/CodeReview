package pl.mpieciukiewicz.codereview.system

import pl.mpieciukiewicz.codereview.database.{RepositoryStorage, UserStorage}
import akka.actor.Actor
import akka.actor.Actor.Receive
import pl.mpieciukiewicz.codereview.vcs.git.GitLocalRepositoryManager
import java.io.File
import pl.mpieciukiewicz.codereview.model.Repository
import org.joda.time.DateTime

object RepositoryManager {
  case class AddRepository(cloneUrl: String, repositoryName: String, projectId: Int)

  case class AddingRepositoryResponse(successful: Boolean, repositoryId: Option[Int])
}

class RepositoryManager(repositoryStorage: RepositoryStorage) extends Actor {

  import RepositoryManager._

  override def receive = {
    case msg: AddRepository => handleAddRepository(msg)
  }

  private def handleAddRepository(msg: AddRepository) {
    new GitLocalRepositoryManager(new File("c:/codeReview/first")).cloneRemoteRepository(msg.cloneUrl)
    val repository = repositoryStorage.add(Repository(None, msg.projectId, msg.repositoryName, msg.cloneUrl, "c:/codeReview/first", DateTime.now))

    sender ! AddingRepositoryResponse(true, repository.id)
  }
}
