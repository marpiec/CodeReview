package pl.mpieciukiewicz.codereview.vcs.git

import pl.mpieciukiewicz.codereview.vcs._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.{ByteArrayOutputStream, File}
import collection.JavaConverters._
import org.eclipse.jgit.revwalk.{RevWalk, RevCommit}
import org.eclipse.jgit.lib._
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.diff._
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm
import org.joda.time.DateTime
import java.util.Date
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentCopy
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentAdd
import pl.mpieciukiewicz.codereview.vcs.FileAdd
import pl.mpieciukiewicz.codereview.vcs.FileRename
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentRename
import pl.mpieciukiewicz.codereview.vcs.FileModify
import pl.mpieciukiewicz.codereview.vcs.FileDelete
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentModify
import pl.mpieciukiewicz.codereview.vcs.VcsFileContentDelete
import pl.mpieciukiewicz.codereview.vcs.VcsFileDiff
import pl.mpieciukiewicz.codereview.vcs.FileCopy

/**
 *
 */
class GitReader(val repoDir: String) {

  val builder = new FileRepositoryBuilder()
  val repository = builder.findGitDir(new File(repoDir)).build()
  val git = new Git(repository)

  def readAllCommits():List[GitCommit] = {
    readAllGitCommits.map(convertJGitCommitToCommit).toList
  }

  def readCommits(from: Int, count: Int):List[GitCommit] = {
    readLastGitCommitsFromTo(from, count).map(convertJGitCommitToCommit).toList
  }

  private def readAllGitCommits() = {
    new Git(repository).log().all().call().asScala
  }

  private def readLastGitCommitsFromTo(skip: Int, count: Int) = {
    git.log().setSkip(skip).setMaxCount(count).all().call().asScala
  }


  private def convertJGitCommitToCommit(commit: RevCommit):GitCommit = {
    //commit shortMessage

    GitCommit(id = commit.getId.name.trim,
           author = commit.getAuthorIdent.getName.trim,
           authorEmail = commit.getAuthorIdent.getEmailAddress.trim,
           commiter = commit.getCommitterIdent.getName.trim,
           commiterEmail = commit.getCommitterIdent.getEmailAddress.trim,
           message = commit.getFullMessage.trim,
           time = new DateTime(commit.getCommitTime.toLong * 1000))
  }


  private def findBranch(commit: RevCommit):String = {
    val walk = new RevWalk(repository)

    val branches = git.branchList().call().asScala
    branches.foreach { branch =>

      val branchName = branch.getName

      System.out.println("Commits of branch: " + branch.getName)
      System.out.println("-------------------------------------")

      val commits = git.log().all().call().asScala

      for (commit <- commits) {
        var foundInThisBranch = false

        val targetCommit = walk.parseCommit(repository.resolve(commit.getName))
        for ((key, value) <- repository.getAllRefs.asScala) {
          if (key.startsWith(Constants.R_HEADS)) {
            if (walk.isMergedInto(targetCommit, walk.parseCommit(
              value.getObjectId))) {
              val foundInBranch = value.getName
              if (branchName.equals(foundInBranch)) {
                foundInThisBranch = true
              }
            }
          }
        }

        if (foundInThisBranch) {
          println(commit.getName)
          println(commit.getAuthorIdent.getName)
          println(new Date(commit.getCommitTime))
          println(commit.getFullMessage)
          return branchName
        }
      }
    }
    "not found"
  }


  def readFilesFromCommit(commitHash: String): List[FileChange] = {
    val commit = getCommitById(commitHash)
    getFilePathsFromCommit(commit)
  }

  private def getCommitById(commitHash: String): RevCommit = {
    git.log().add(ObjectId.fromString(commitHash)).setMaxCount(1).call().iterator().next()
  }

  private def getFilePathsFromCommit(commit: RevCommit): List[FileChange] = {
    val out = new ByteArrayOutputStream()
    val df = new DiffFormatter(out)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    if(commit.getParentCount > 0) {
      val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList

      diffs.map(convertDiffToFileChange)
    } else {

      //val revWalk = new RevWalk(repository)
      val tree = commit.getTree

      val treeWalk = new TreeWalk(repository)
      treeWalk.addTree(tree)
      treeWalk.setRecursive(true)

      var diffs = List[FileChange]()
      while (treeWalk.next()) {
        diffs ::=  FileAdd(treeWalk.getPathString)
      }
      diffs.reverse
    }
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


  def readFilesContentFromCommit(commitHash: String): List[(VcsFileContent, VcsFileDiff)] = {
    val commit = getCommitById(commitHash)

    val fileChanges = if(commit.getParentCount > 0) {
      val out = new ByteArrayOutputStream()
      val df = new DiffFormatter(out)
      df.setRepository(repository)
      df.setDiffComparator(RawTextComparator.DEFAULT)
      df.setDetectRenames(true)

      val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList
      diffs.map(convertDiffToFileChange)

    } else {
      val tree = commit.getTree

      val treeWalk = new TreeWalk(repository)
      treeWalk.addTree(tree)
      treeWalk.setRecursive(true)

      var fileChanges = List[FileChange]()
      while (treeWalk.next()) {
        fileChanges ::=  FileAdd(treeWalk.getPathString)
      }
      fileChanges.reverse
    }

    val content = fileChanges.map(readFileContent(commit))
    val fileDiffs = content.map(createDiffFromContents)
    content.zip(fileDiffs)

  }

  private def readFileContent(commit: RevCommit)(fileChange: FileChange): VcsFileContent = {

    fileChange match {
      case change: FileAdd => VcsFileContentAdd(readFileContentAfterCommit(commit, change.newPath), change.newPath)
      case change: FileModify => VcsFileContentModify(readFileContentAfterCommit(commit.getParent(0), change.path), change.path, readFileContentAfterCommit(commit, change.path))
      case change: FileDelete => VcsFileContentDelete(readFileContentAfterCommit(commit.getParent(0), change.oldPath), change.oldPath)
      case change: FileRename => VcsFileContentRename(readFileContentAfterCommit(commit.getParent(0), change.oldPath), change.oldPath, readFileContentAfterCommit(commit, change.newPath), change.newPath)
      case change: FileCopy => VcsFileContentCopy(readFileContentAfterCommit(commit.getParent(0), change.oldPath), change.oldPath, readFileContentAfterCommit(commit, change.newPath), change.newPath)
    }

  }

  private def readFileContentAfterCommit(commit: RevCommit, filePath: String): String = {
    val treeWalk = new TreeWalk(repository)
    treeWalk.addTree(commit.getTree)
    treeWalk.setRecursive(true)
    treeWalk.setFilter(PathFilter.create(filePath))

    if (!treeWalk.next()) {
      throw new IllegalStateException(s"Did not find expected file $filePath")
    }

    val objectId = treeWalk.getObjectId(0)
    val loader = repository.open(objectId)
    val bytes = loader.getBytes

    if(bytes.take(8000).exists(_ == 0)) { //http://stackoverflow.com/a/6134127
      "Binary"
    } else {
      new String(loader.getBytes)
    }

  }

  def readFilesDiffFromCommit(commitHash: String): List[VcsFileDiff] = {
    val fileContents = readFilesContentFromCommit(commitHash)

    fileContents.map(_._2)
  }



  private def createDiffFromContents(fileContent: VcsFileContent): VcsFileDiff = {

    val diffText = fileContent match {
      case content: VcsFileContentAdd => readFileDiffFromCommit("", content.content)
      case content: VcsFileContentModify => readFileDiffFromCommit(content.fromContent, content.toContent)
      case content: VcsFileContentDelete => readFileDiffFromCommit(content.oldContent, "")
      case content: VcsFileContentRename => readFileDiffFromCommit(content.fromContent, content.toContent)
      case content: VcsFileContentCopy => readFileDiffFromCommit(content.fromContent, content.toContent)
    }

    try {
      new GitDiffParser().parse(diffText.split("\n").toIterator)
    } catch {
      case e: Exception =>
      println("Error parsing -----------------------\n"+diffText+"\n---------------------------")
      throw(e)
    }

  }

  private def readFileDiffFromCommit(oldContent: String, newContent: String): String = {

    val out = new ByteArrayOutputStream()
    val df = new DiffFormatter(out)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    val diffAlgorithm = DiffAlgorithm.getAlgorithm(repository.getConfig.getEnum(ConfigConstants.CONFIG_DIFF_SECTION, null, ConfigConstants.CONFIG_KEY_ALGORITHM, SupportedAlgorithm.HISTOGRAM))

    val editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(oldContent.getBytes), new RawText(newContent.getBytes))
    df.format(editList, new RawText(oldContent.getBytes), new RawText(newContent.getBytes))
   //val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList


   // df.format(diffs.tail.head)

   // println(diffs)
   // println()
    out.toString()
  }

}
