import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class LogicInterpreter() {
    private val variables: MutableMap<String, Boolean> = mutableMapOf()

    fun evaluate(expression: String): Boolean {
        // Remove all whitespace from the expression
        val exp = expression.replace("\\s".toRegex(), "")

        // First, evaluate any expressions inside parentheses
        val result = exp.replace("\\(([^)]*)\\)".toRegex()) { match ->
            addLogic(match.groups[1]!!.value).toString()
        }

        println(result)

        // Check if result is a variable
        if (result.matches("[^T|F|\\d]".toRegex())) {
            // check if the variable is already defined
            if (variables.containsKey(result)) {
                return variables[result]!!
            }
            throw IllegalArgumentException("Unrecognized variable: $result")
        }

        // Return the final result
        return addLogic(result).toBoolean()
    }

    private fun addLogic(exp:String):Char{
        // Next, evaluate NOT operators
        var result = exp.replace("¬[TF]".toRegex()) { match ->
            (!match.value[1].toBoolean()).toString()
        }

        // Then, evaluate AND operators
        result = result.replace("[TF]∧[TF]".toRegex()) { match ->
            (match.value[0].toBoolean() && match.value[2].toBoolean()).toString()
        }

        // Finally, evaluate OR operators
        result = result.replace("[TF]∨[TF]".toRegex()) { match ->
            (match.value[0].toBoolean() || match.value[2].toBoolean()).toString()
        }

        return if(result.toBoolean()) 'T' else 'F'
    }

    fun setVariable(name: String, value: Boolean) {
        variables[name] = value
    }
}

private fun Char.toBoolean(): Boolean {
    return when (this) {
        'T' -> true
        'F' -> false
        else -> false
    }
}

private class Test {
    lateinit var interpreter: LogicInterpreter

    @BeforeEach
    fun setUp() {
        interpreter = LogicInterpreter()
    }

    @Test
    fun `¬F returns T`() {
        assertTrue(interpreter.evaluate("¬F"))
    }

    @Test
    fun `¬T returns F`() {
        assertFalse(interpreter.evaluate("¬T"))
    }

    @Test
    fun `T ∨ F to true`() {
        assertTrue(interpreter.evaluate("T ∨ F"))
    }

    @Test
    fun `T ∧ F to false`() {
        assertFalse(interpreter.evaluate("T ∧ F"))
    }
    @Test
    fun `(T ∧ F) to false`() {
        assertFalse(interpreter.evaluate("(T ∧ F)"))
    }

    @Test
    fun `(T ∧ F) ∨ T evaluates to true`() {
        assertTrue(interpreter.evaluate("(T ∧ F) ∨ T"))
    }
}