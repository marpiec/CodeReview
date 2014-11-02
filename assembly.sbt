import AssemblyKeys._ // put this at the top of the file

assemblySettings

jarName in assembly := "CodeReview.jar"

mainClass in assembly := Some("pl.mpieciukiewicz.codereview.Main")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
  case entry => {
    val strategy = mergeStrategy(entry)
    if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
    else strategy
  }
}}