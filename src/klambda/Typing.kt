package klambda

sealed class Type

data class TyFun(val inp: Type, val out: Type) : Type() {
    override fun toString(): String = "$inp \uD83E\uDC52 $out"
}

object TyBool : Type() {
    override fun toString(): String = "Bool"
}

object TyInt : Type() {
    override fun toString(): String = "Int"
}

sealed class TyResult

data class Fail(val msg: String) : TyResult()
data class Success(val result: Type) : TyResult()

private class TypeException(val msg: String) : Exception()

fun fail(msg: String): Nothing =
    throw TypeException(msg)

fun expected(expected: Any, actual: Any): Nothing =
    throw TypeException("Expected $expected but got $actual")

fun Context.check(lambda: Context.() -> Type): TyResult {
    return try {
        val result = lambda(this)
        Success(result)
    } catch (e: TypeException) {
        Fail(e.msg)
    }
}