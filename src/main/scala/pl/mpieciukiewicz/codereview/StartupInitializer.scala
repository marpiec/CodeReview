package pl.mpieciukiewicz.codereview

import pl.mpieciukiewicz.codereview.system.RepositoryManager

class StartupInitializer(repositoryManager: RepositoryManager) {

  def apply() {
    repositoryManager.updateAllRepositories()
  }

}
