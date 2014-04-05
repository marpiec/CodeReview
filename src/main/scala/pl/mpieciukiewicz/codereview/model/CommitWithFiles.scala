package pl.mpieciukiewicz.codereview.model

import pl.mpieciukiewicz.codereview.vcs.FileChange

case class CommitWithFiles(info: Commit, files: List[FileChange])
