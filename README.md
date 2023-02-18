# LogicInterpreter
This is a simple Boolean logic interpreter that can evaluate expressions involving variables and the logical operators AND, OR, and NOT.

## Installation
To install the project, simply clone this repository to your local machine:

git clone https://github.com/KiarieLinus/LogicInterpreter.git

Run it on an IDE that supports Kotlin

## Usage
To use the Boolean logic interpreter, run the test file [LogicInterpreter.kt](https://github.com/KiarieLinus/LogicInterpreter/blob/master/src/test/kotlin/LogicInterpreter.kt) that already has some pre-written tests.

The syntax for expressions is as follows:

## Variables
Variables can be defined using the setVariable function

Example: interpreter.setVariable("X", false)

Where "interpreter" is a LogicInterpreter object

## NOT operator
The NOT operator (¬) negates the value of the expression following it.

Example: ¬T evaluates to F

## AND operator
The AND operator (∧) returns T if both the expressions on either side of it are T, and F otherwise.

Example: T ∧ F evaluates to F

## OR operator
The OR operator (∨) returns T if either of the expressions on either side of it are T, and F otherwise.

Example: T ∨ F evaluates to T

## Parentheses
Parentheses can be used to specify the order of operations. Expressions inside parentheses are evaluated first.

Example: (T ∧ F) ∨ T evaluates to T

## Precedence

| Operator | Order |
|:--------:|:-----:|
|    ()    |   1   |
|    ¬     |   2   |
|    ∧     |   3   |
|    ∨     |   4   |

