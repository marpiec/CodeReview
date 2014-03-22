package pl.mpieciukiewicz.codereview.model

/**
 *
 */
case class User(id: Int,
                name: String,
                email: String,
                password: String,
                salt: String)
