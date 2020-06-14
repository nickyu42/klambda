package klambda

typealias State = MutableMap<String, Type>

class Context {
    var st = mutableMapOf<String, Type>()
    var stateStack = mutableListOf<State>()
    val typeBuffer = StringBuilder()
    var tempBuffer = mutableListOf<MutableList<String>>()

    private fun push(st: State) =
        stateStack.add(st)

    private fun pop(): State =
        stateStack.removeLast()

    fun withSt(fn: () -> Type): Type {
        push(st)
        val ty = fn()
        pop()
        return ty
    }

    fun log(term: Any, ty: Type) {
        val msg = "Τ \u22A2 $term : $ty\n"
        if (tempBuffer.isNotEmpty()) {
            tempBuffer.last().add(msg)
        } else {
            // typeBuffer.append(msg)
        }
    }

    fun <T> parLog(fn: () -> T): T {
        tempBuffer.add(mutableListOf())

        val result = fn()

        val currentBuffer = tempBuffer.last()
        val newBuffer = StringBuilder()
        for (i in 0 until currentBuffer.size - 1) {
            newBuffer.append(currentBuffer[i])
        }
        newBuffer.append("-------------\n")
        newBuffer.append(currentBuffer[currentBuffer.size - 1])
        typeBuffer.append("$newBuffer\n")
        tempBuffer.removeLast()
        return result
    }

    fun exists(s: String): Boolean =
        st.containsKey(s)
}

fun Context.getTy(t: Term): Type = parLog {
    when (t) {
        TmTrue -> TyBool.also { log(t, it) }

        TmFalse -> TyBool.also { log(t, it) }

        is TmInt -> TyInt.also { log(t, it) }

        is TmVar -> if (this.exists(t.name)) {
            this.st[t.name]!!.also { log(t, it) }
        } else {
            fail("${t.name} does not exist in context")
        }

        is TmAbs -> withSt {
            this.st[t.name] = t.ty
            TyFun(t.ty, getTy(t.expr)).also { log(t, it) }
        }

        is TmApp -> {
            val expr = getTy(t.expr)
            val app = getTy(t.app)
            when (expr) {
                is TyFun -> if (expr.inp == app) {
                    expr.out.also { log(t, it) }
                } else {
                    expected(expr.inp, app)
                }
                else -> fail("$expr is not a function")
            }
        }

        is TmIf -> {
            val cond = getTy(t.cond)
            if (cond != TyBool) {
                expected(TyBool, cond)
            }

            val tBr = getTy(t.tBr)
            val fBr = getTy(t.fBr)

            if (tBr != fBr) {
                fail("klambda.Type of $tBr and $fBr do not match")
            }

            tBr.also { log(t, it) }
        }

        is TmAdd -> {
            val x = getTy(t.x)
            val y = getTy(t.y)
            if (x == TyInt && y == TyInt) {
                TyInt.also { log(t, it) }
            } else {
                fail("${t.x} and ${t.y} must be of type $TyInt")
            }
        }
    }
}

fun main() {
    val expr = TmApp(TmAbs("x", TyBool, TmVar("x")), TmTrue)

    println(expr)

    val ctx = Context()

    val res = ctx.check {
        getTy(expr)
    }

    print(ctx.typeBuffer.toString())
    println(res)
}