# lambda-interp
========================

A simple Lambda Calculus Interpreter implemented by Java

## Description
`lambda-interp` is a read-evaluate-print loop for the evaluation of lambda calculus expressions, which is implemented by Java 8.

The evaluation order of `lambda-interp` is `LAZY EVALUATION`(call by need).

## Requires
Java 8


## Building and Running

```
$ javac LambdaInterp.java
$ java  LambdaInterp
```

## Usage

```
> %x.x
Result: %x.x

> %x.x %y.y
Result: %x.(x %y.y)
```

`lambda` can take any number of parameters. It's syntactic sugar, and in the `core language` there is only one parameter:

```
> %x y z.x y z
Result: %x.%y.%z.((x y) z)
```

Or `lambda` can take none parameter:

```
> %.%x.x
Result: %.%x.x
```

Reduce:

```
> (%s.s s) %x.x %y.y
Result: %y.y
```


### Definition

Use KEYWORD `define` to define funtions:

```
> define a = %x.x
> a
Result: %x.x
```

There are some definitions in `PreDefinition.java`:

```
// boolean
definitions.add("define true = %x y.x");
definitions.add("define false = %x y.y");
definitions.add("define if = %p x y. p x y");
...
```

By default, these definitions can be used as follow:

```
> if true (%x.x) (%y.y)
Result: %x.x

> if false (%x.x) (%y.y)
Result: %y.y
```


And numbers:

```
> 0
Result: (%f.(%x.x))

> 1
Result: (%f.(%x.(f x)))

> 2
Result: (%f.(%x.(f (f x))))

> 6
Result: (%f.(%x.(f (f (f (f (f (f x))))))))
```


Elementary arithmetic operators can be used directly:

```
> add 1 2
Result: (%f.(%x.(f (f (f x)))))

> sub 3 3
Result: (%f.(%x.x))

> mult 2 3
Result: (%f.(%x.(f (f (f (f (f (f x))))))))

> fact 1
Result: (%f.(%x.(f x)))

> fact 3
Result: (%f.(%x.(f (f (f (f (f (f x))))))))
```


### Advanced Features

1.
`lambda-interp` can simplify final result:

```
> ((%x.(%f.x)) (%y.(%z.((%m n.(m n)) y z))))
Result: (%f.(%y.(%z.(y z)))) # simplest form of `(%f.(%y.(%z.((%m n.(m n)) y z))))`
```

2.
Because of `lazy evaluation`, the following expressions is valid, and no infinite loop:

```
> if true (%x.x) ((%x.x x) %x.x x)
Result: %x.x
```

3.
`lambda-interp` can detect infinite loop. If a express is a inifinite loop, `lambda-interp` will not run infinitely.

```
> ((%x.x x) %x.x x)
Warning: infinite loop happens in expression `((%x.(x x)) (%x.(x x)))`
Result: ((%x.(x x)) (%x.(x x)))

> if false (%x.x) ((%x.x x) %x.x x)
Warning: infinite loop happens in expression `((%x.(x x)) (%x.(x x)))`
Result: ((%x.(x x)) (%x.(x x)))
```

By this feature, based on Y Combinator, Factorial is defined in `PreDefinition.java`:

```
// recursion
definitions.add("define Y = %f.(%x.f (x x)) (%x.f (x x))");
definitions.add("define fact = Y (%g n. if (iszero n) 1 (mult n (g (pre n))))");


> fact 1
Result: (%f.(%x.(f x)))

> fact 2
Result: (%f.(%x.(f (f x))))

> fact 3
Result: (%f.(%x.(f (f (f (f (f (f x))))))))
```
