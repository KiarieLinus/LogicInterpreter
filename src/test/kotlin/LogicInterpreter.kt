import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class LogicInterpreter() {
    private val variables: MutableMap<String, String> = mutableMapOf()

    fun evaluate(expression: String): Boolean {
        // Remove all whitespace from the expression
        val exp = expression.replace("\\s".toRegex(), "")

        // First, evaluate any expressions inside parentheses
        val result = exp.replace("\\(([^)]*)\\)".toRegex()) { match ->
            addLogic(match.groups[1]!!.value).toString()
        }

        // Return the final result
        return addLogic(result).toBoolean()
    }

    private fun addLogic(exp: String): Char {
        var expression = exp
        // 1. Check if result is a variable
        if (variables.isNotEmpty()) {
            variables.keys.forEach {
                expression = exp.replace(it.first(), variables[it]?.first()!!)
            }
        }

        // 2. evaluate NOT operators
        var result = expression.replace("¬[TF]".toRegex()) { match ->
            if (!match.value[1].toBoolean()) "T" else "F"
        }

        // 3. evaluate AND operators
        result = result.replace("[TF]∧[TF]".toRegex()) { match ->
            if (match.value[0].toBoolean() && match.value[2].toBoolean()) "T" else "F"
        }

        // 4. evaluate OR operators
        result = result.replace("[TF]∨[TF]".toRegex()) { match ->
            if (match.value[0].toBoolean() || match.value[2].toBoolean()) "T" else "F"
        }
        return result.first()
    }

    fun setVariable(name: String, value: Boolean) {
        val invalidRegex = Regex("[TF0-9]")
        if (invalidRegex.containsMatchIn(name)) {
            throw IllegalArgumentException("Invalid argument $name cannot be declared as a variable")
        }
        variables[name] = if (value) "T" else "F"
    }
}

private fun Char.toBoolean(): Boolean {
    return when (this) {
        'T' -> true
        'F' -> false
        else -> throw IllegalArgumentException(
            "Invalid argument $this use fun interpreter.setVariable(String,Boolean) to add a variable"
        )
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

    @Test
    fun `X ∧ ¬X evaluates to false`() {
        interpreter.setVariable("X", false)
        assertFalse(interpreter.evaluate("X ∧ ¬X"))
    }

    @Test
    fun `X ∧ ¬X throws exception when setVariable is unused`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            interpreter.evaluate("X ∧ ¬X")
        }
        assertEquals(
            "Invalid argument X use fun interpreter.setVariable(String,Boolean) to add a variable", exception.message)
    }

    @Test
    fun `setVariable F,T or 0-9 throws exception`() {
        //Test for number
        var exception = assertThrows(IllegalArgumentException::class.java) {
            interpreter.setVariable("6", false)
        }
        assertEquals("Invalid argument 6 cannot be declared as a variable", exception.message)

        //Test for "F"
        exception = assertThrows(IllegalArgumentException::class.java) {
            interpreter.setVariable("F", false)
        }
        assertEquals("Invalid argument F cannot be declared as a variable", exception.message)

        //Test for "T"
        exception = assertThrows(IllegalArgumentException::class.java) {
            interpreter.setVariable("T", false)
        }
        assertEquals("Invalid argument T cannot be declared as a variable", exception.message)
    }
}