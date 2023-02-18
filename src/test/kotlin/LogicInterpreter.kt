import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFailsWith


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
        if(invalidRegex.containsMatchIn(name)){
            throw IllegalArgumentException("Invalid argument $name")
        }
        variables[name] = if (value) "T" else "F"
    }
}

private fun Char.toBoolean(): Boolean {
    return when (this) {
        'T' -> true
        'F' -> false
        else -> throw IllegalArgumentException("Invalid argument $this")
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
    fun `setVariable F,T or 0-9 throws exception`() {
        assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            message = "Invalid argument F"){
            interpreter.setVariable("F", false)
        }
        assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            message = "Invalid argument T"){
            interpreter.setVariable("T", false)
        }
        assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            message = "Invalid argument 6"){
            interpreter.setVariable("6", false)
        }
    }
}