# lambda-interp
========================

A simple Lambda Calculus Interpreter implemented by Java

## Description
`lambda-interp` is a read-evaluate-print loop for the evaluation of lambda calculus expressions, which is implemented by Java 8.

The evaluation order of `lambda-interp` is `LAZY EVALUATION`(call by need).

## Requires
Java 8


## Building and Running

    $javac LambdaInterp.java
    $java  LambdaInterp

## Usage

    > %x.x
    Result: %x.x

    >%x.x %y.y
    Result: %x.(x %y.y)

`lambda` can take any number of parameters. It's syntactic sugar, and in the `core language` there is only one parameter:

    >%x y z.x y z
    Result: %x.%y.%z.((x y) z)

Or `lambda` can take none parameter:

    >%.%x.x
    Result: %.%x.x

### Define Function

Use KEYWORD `define` to define funtions:

    >define a = %x.x
    >a
    Result: %x.x

There are some definitions in `PreDefinition.java`:

    // boolean
    definitions.add("define true = %x y.x");
    definitions.add("define false = %x y.y");
    definitions.add("define if = %p x y. p x y");
    ...

By default, you can use function `if` as follow:

    >if true (%x.x) (%y.y)
    Result: %x.x

    >if false (%x.x) (%y.y)
    Result: %y.y

Because of `lazy evaluation`, the following expressions is valid, and no infinite loop:

    >if true (%x.x) ((%x.x x) %x.x x)
    Result: %x.x
