package pl.mpieciukiewicz.codereview.model

/**
 *
 */
case class User(name: String, password: String, salt: String, email: String, role: String)
