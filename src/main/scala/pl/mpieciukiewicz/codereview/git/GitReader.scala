package pl.mpieciukiewicz.codereview.git

import pl.mpieciukiewicz.codereview.vcs._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.{ByteArrayOutputStream, File}
import collection.JavaConverters._
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.lib.{ObjectId, Repository}
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.diff.{DiffEntry, RawTextComparator, DiffFormatter}
import pl.mpieciukiewicz.codereview.vcs.FileDelete
import pl.mpieciukiewicz.codereview.vcs.Commit
import pl.mpieciukiewicz.codereview.vcs.FileAdd

/**
 *
 */
class GitReader(val repoDir: String) {

  val builder = new FileRepositoryBuilder()
  val repository = builder.findGitDir(new File(repoDir)).build()

  def readCommits(count: Int):List[Commit] = {

    val jgitCommits = new Git(repository).log().all().call().asScala.take(count)

    jgitCommits.map(convertJGitCommitToCommit).toList
  }

  private def convertJGitCommitToCommit(commit: RevCommit):Commit = {
    Commit(id = commit.getId.name.trim,
           author = commit.getAuthorIdent.getName.trim,
           commiter = commit.getCommitterIdent.getName.trim,
           message = commit.getFullMessage.trim,
           time = commit.getCommitTime)
  }


  def readFilesFromCommit(commitId: String): List[FileChange] = {
    val commit = new Git(repository).log().add(ObjectId.fromString(commitId)).setMaxCount(1).call().iterator().next()
    getFilePathsFromCommit(commit)
  }

  private def getFilePathsFromCommit(commit: RevCommit): List[FileChange] = {
    val out = new ByteArrayOutputStream()
    val df = new DiffFormatter(out)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList

    diffs.map(convertDiffToFileChange)
  }

  private def convertDiffToFileChange(diff: DiffEntry): FileChange = {
    diff.getChangeType match {
      case DiffEntry.ChangeType.ADD => FileAdd(diff.getNewPath)
      case DiffEntry.ChangeType.COPY => FileCopy(diff.getOldPath, diff.getNewPath)
      case DiffEntry.ChangeType.DELETE => FileDelete(diff.getOldPath)
      case DiffEntry.ChangeType.MODIFY => FileModify(diff.getOldPath)
      case DiffEntry.ChangeType.RENAME => FileRename(diff.getOldPath, diff.getNewPath)
    }
  }

}
