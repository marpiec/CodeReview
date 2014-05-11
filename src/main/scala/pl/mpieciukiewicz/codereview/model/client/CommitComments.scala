package pl.mpieciukiewicz.codereview.model.client

import pl.mpieciukiewicz.codereview.model.persitent.{LineComment, FileComment}

case class CommitComments(commit: List[CommitComments], file: List[FileComment], line: List[LineComment])
