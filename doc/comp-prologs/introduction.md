# Comparison of the different Prolog implementations

[Prolog](https://en.wikipedia.org/wiki/Prolog) is a general-purpose logic programming language,
initially conceived in the 1970s by [Alain Colmerauer](https://en.wikipedia.org/wiki/Alain_Colmerauer).

## Introduction

In this section we will compare 2 implementations and an extension for these implementations,
[GNU Prolog](http://www.gprolog.org/) the language we have focused on in this project,
[SWI-Prolog](http://swi-prolog.org/) and the [Logtalk](http://logtalk.org/) extension that adds
object-oriented elements to Prolog.

We will compare these implementations across the following features and tools:

+ Compiled code
+ Unicode support
+ Object-orientated support
+ Interfacing with other programming languages
+ Availability of an interactive interpreter

But first Prolog is divided in two dialects ISO Prolog and Edinburgh Prolog, Edinburg Prolog being
the standard upon which ISO Prolog is based. The main difference between them is mostly cosmetic and
the built-in predicates for input and output.
