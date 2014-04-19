package pl.mpieciukiewicz.codereview.database

trait SequenceManager {
  def nextId(sequenceName: String):Int
}
