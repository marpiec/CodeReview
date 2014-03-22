package pl.mpieciukiewicz.codereview.model

/**
 *
 */
case class User(name: String,
                email: String,
                password: String,
                salt: String,
                id: Int = 0)
