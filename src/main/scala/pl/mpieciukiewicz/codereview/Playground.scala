package pl.mpieciukiewicz.codereview

import org.eclipse.jgit.api.Git
import java.io.{ByteArrayOutputStream, File}
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import collection.JavaConverters._
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.diff.{DiffFormatter, RawTextComparator}

/**
 *
 */
object Playground {

  def start() {

    val repoDir = new File("c:/TmpRepo/")

    repoDir.mkdir()

    val builder = new FileRepositoryBuilder()
    implicit val repository = builder.findGitDir(repoDir).build()

    val commits = new Git(repository).log().all().call().asScala.toList

    commits.map(_.getFullMessage).foreach(println)

    val filesInCommits = commits.map(getFilePathsFromCommit)
    println(filesInCommits)

  val commit = commits.head

    val out = new ByteArrayOutputStream()
    val df = new DiffFormatter(out)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList


    df.format(diffs.head)

    println(diffs)
    println(out.toString("UTF-8"))

  }

  def getFilePathsFromCommit(commit: RevCommit)(implicit repository: Repository): List[String] = {
    val treeWalk = new TreeWalk(repository)
    treeWalk.addTree(commit.getTree)
    treeWalk.setRecursive(true)
    var files = List[String]()
    while (treeWalk.next()) {
      files ::= treeWalk.getPathString
    }
    files
  }

  def cloneRepo(directory: File) {
    Git.cloneRepository().
      setURI("https://github.com/marpiec/AngularJSCalculator.git").
      setDirectory(directory).
      call().
      close()
  }
}
