Simply typed lambda calculus in Kotlin

Currently only has type checker that prints stepwise typing rules

E.g. Boolean identity application with True
```
(λx : Bool. x) True

-------------
Τ ⊢ x : Bool

-------------
Τ ⊢ λx : Bool. x : Bool 🡒 Bool

-------------
Τ ⊢ True : Bool

-------------
Τ ⊢ (λx : Bool. x) True : Bool

Success(result=Bool)
```