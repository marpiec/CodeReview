package pl.mpieciukiewicz.codereview.git

import pl.mpieciukiewicz.codereview.vcs._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.{ByteArrayOutputStream, File}
import collection.JavaConverters._
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.lib.{ConfigConstants, ObjectId}
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.diff._
import pl.mpieciukiewicz.codereview.vcs._
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.patch.FileHeader
import pl.mpieciukiewicz.codereview.vcs.FileRename
import pl.mpieciukiewicz.codereview.vcs.FileModify
import pl.mpieciukiewicz.codereview.vcs.FileDelete
import pl.mpieciukiewicz.codereview.vcs.FileContentAdd
import pl.mpieciukiewicz.codereview.vcs.FileContentCopy
import pl.mpieciukiewicz.codereview.vcs.Commit
import pl.mpieciukiewicz.codereview.vcs.FileContentRename
import pl.mpieciukiewicz.codereview.vcs.FileContentModify
import pl.mpieciukiewicz.codereview.vcs.FileContentDelete
import pl.mpieciukiewicz.codereview.vcs.FileAdd
import pl.mpieciukiewicz.codereview.vcs.FileCopy
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm

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
    val commit = getCommitById(commitId)
    getFilePathsFromCommit(commit)
  }

  private def getCommitById(commitId: String): RevCommit = {
    new Git(repository).log().add(ObjectId.fromString(commitId)).setMaxCount(1).call().iterator().next()
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


  def readFilesContentFromCommit(commitId: String): List[FileContent] = {
    val commit = getCommitById(commitId)

    val out = new ByteArrayOutputStream()
    val df = new DiffFormatter(out)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList

    val fileChanges = diffs.map(convertDiffToFileChange)

    fileChanges.map(readFileContent(commit))

  }

  private def readFileContent(commit: RevCommit)(fileChange: FileChange): FileContent = {

    fileChange match {
      case change: FileAdd => FileContentAdd(readFileContentAfterCommit(commit, change.newPath))
      case change: FileModify => FileContentModify(readFileContentAfterCommit(commit.getParent(0), change.path), readFileContentAfterCommit(commit, change.path))
      case change: FileDelete => FileContentDelete(readFileContentAfterCommit(commit.getParent(0), change.oldPath))
      case change: FileRename => FileContentRename(readFileContentAfterCommit(commit.getParent(0), change.oldPath), readFileContentAfterCommit(commit, change.newPath))
      case change: FileCopy => FileContentCopy(readFileContentAfterCommit(commit.getParent(0), change.oldPath), readFileContentAfterCommit(commit, change.newPath))
    }

  }

  private def readFileContentAfterCommit(commit: RevCommit, filePath: String): Array[Byte] = {
    val treeWalk = new TreeWalk(repository)
    treeWalk.addTree(commit.getTree)
    treeWalk.setRecursive(true)
    treeWalk.setFilter(PathFilter.create(filePath))

    if (!treeWalk.next()) {
      throw new IllegalStateException(s"Did not find expected file $filePath")
    }

    val objectId = treeWalk.getObjectId(0)
    val loader = repository.open(objectId)

    loader.getBytes
  }

  def readFilesDiffFromCommit(commitId: String): List[FileDiff] = {
    val fileContents = readFilesContentFromCommit(commitId)

    fileContents.map(createDiffFromContents)
  }



  private def createDiffFromContents(fileContent: FileContent): FileDiff = {

    val diffText = fileContent match {
      case content: FileContentAdd => readFileDiffFromCommit(Array[Byte](), content.content)
      case content: FileContentModify => readFileDiffFromCommit(content.fromContent, content.toContent)
      case content: FileContentDelete => readFileDiffFromCommit(content.oldContent, Array[Byte]())
      case content: FileContentRename => readFileDiffFromCommit(content.fromContent, content.toContent)
      case content: FileContentCopy => readFileDiffFromCommit(content.fromContent, content.toContent)
    }

    new GitDiffParser().parse(diffText.split("\n").toIterator)

  }

  private def readFileDiffFromCommit(oldContent: Array[Byte], newContent: Array[Byte]): String = {

    val out = new ByteArrayOutputStream()
    val df = new DiffFormatter(out)
    df.setRepository(repository)
    df.setDiffComparator(RawTextComparator.DEFAULT)
    df.setDetectRenames(true)

    val diffAlgorithm = DiffAlgorithm.getAlgorithm(repository.getConfig.getEnum(ConfigConstants.CONFIG_DIFF_SECTION, null, ConfigConstants.CONFIG_KEY_ALGORITHM, SupportedAlgorithm.HISTOGRAM))

    val editList = diffAlgorithm.diff(RawTextComparator.DEFAULT, new RawText(oldContent), new RawText(newContent))
    df.format(editList, new RawText(oldContent), new RawText(newContent))
   //val diffs = df.scan(commit.getParent(0).getId, commit.getTree).asScala.toList


   // df.format(diffs.tail.head)

   // println(diffs)
   // println()
    out.toString()
  }

}
