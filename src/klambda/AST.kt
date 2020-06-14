package klambda

sealed class Term(private val repr: Any) {
    override fun toString(): String = repr.toString()
}

object TmTrue: Term("True")
object TmFalse: Term("True")
class TmInt(n: Int): Term(n)
class TmVar(val name: String): Term(name)
class TmAbs(val name: String, val ty: Type, val expr: Term): Term("\u03BB$name : $ty. $expr")
class TmApp(val expr: Term, val app: Term): Term(if (expr is TmAbs) "($expr) $app" else "$expr $app")
class TmIf(val cond: Term, val tBr: Term, val fBr: Term): Term("If $cond then $tBr else $fBr")
class TmAdd(val x: Term, val y: Term): Term("$x + $y")
