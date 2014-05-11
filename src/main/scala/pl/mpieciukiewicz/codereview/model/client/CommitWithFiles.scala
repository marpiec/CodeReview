package pl.mpieciukiewicz.codereview.model.client

import pl.mpieciukiewicz.codereview.vcs.FileChange
import pl.mpieciukiewicz.codereview.model.persitent.Commit

case class CommitWithFiles(info: Commit, files: List[FileChange])
