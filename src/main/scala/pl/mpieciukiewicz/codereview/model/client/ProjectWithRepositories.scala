package pl.mpieciukiewicz.codereview.model.client

import pl.mpieciukiewicz.codereview.model.{Repository, Project}

case class ProjectWithRepositories(project: Project, repositories: List[Repository])
