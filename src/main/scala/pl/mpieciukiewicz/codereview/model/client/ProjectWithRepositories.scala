package pl.mpieciukiewicz.codereview.model.client

import pl.mpieciukiewicz.codereview.model.persitent.{Repository, Project}

case class ProjectWithRepositories(project: Project, repositories: List[Repository])
