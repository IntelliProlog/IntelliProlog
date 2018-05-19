# Conclusion

We now have a plugin that can be used in IntelliJ IDEA for developing Prolog programs, more specifically
GNU Prolog if we wish to launch the files directly in the interpreter. Hopefully this will make
the lives of the Logic Programming course students who have less experience using the terminal easier.

This plugin still has a lot of features that could be added or improved, but the main feature we wanted
to provide, the ability to launch a file in the interpreter directly is functional and usable but like
the rest of the plugin can always be improved.

## Improvement ideas

This plugin has a couple of ideas that can be improved and new features that can be added. The areas of
improvement are:

+ Lexing and Parsing, this is probability the area of the plugin that could be improved the most and most
  likely will never be perfect. The lexer could be more fine-grained, which would then help improve
  the parser and detecting errors. The parser needs to be more precise and provide an easier PSI tree
  for some of the features that are very dependant on it.
+ Error highlighting, this can be improved mostly by improving the parser and displaying the parser
  errors as well as the lexer errors.
+ Code completion, is a feature that is always welcome, specially when learning a new language and we
  do not know the syntax or all the built-in functions/predicates available. It can also improve
  efficiency by decreasing the amount of code that needs to be written manually, notably for functors
  that are used for often.
+ Refactoring and finding usages, are features that have become more and more important the larger
  software projects become. With more and more software having more than 1 million lines of code,
  according to the site 'information is beautiful' [@million],
  even the XBox HD DVD Player software had just over 5 million lines of code. At sizes like that it
  would be impossible for a human to find all call and rename correctly a function that is spread all
  over the project, that is where refactoring and finding the usages comes in handy.

There are a lots of other features that can be added to this plugin, for a more complete list of
features that can be implemented in IntelliJ IDEs, check out the Custom Language Support [@csp]
page of the IntelliJ Platform SDK DevGuide.

## Goals achieved

When we started this project, we came up with a list of features that we would like to implement,
unfortunately we were not able to implement all of these. The features that were not implemented are
the following:

+ Code completion, the main reason being the difficulty knowing what to suggest.
  Some ideas were to suggest all the Prolog built-in predicates and/or the predicates in the current file.
+ Code formatting, mainly because we were not sure what good code formatting in Prolog should look like, everybody has their own preferences.
+ Refactoring, to be able to provide robust refactoring the parser and lexer also have to be robust and not have any ambiguities.

## Personal opinion

This project was very interesting but at the same time very frustrating, mainly because of the poor
documentation for the complicated aspects of the plugin development process for the IntelliJ Platform.

The only way to understand how something needs to be implemented relied on reading the implementations
of other plugins, and most plugins implement features very differently. It seems the more complicated
features have several different ways that they can be implemented and there is no clear indication what
the proper or best way is.

There is one benefit of this, I learnt to read a lot of source code and understand it. It also gave me
a new appreciation of good documentation and the necessity of it.

In the end, I am pleased with the result, since half way through I sort of lost hope in being able to
implement the main objective of the plugin, launching the current file. It was only after reading through
a dozen different plugins that I found a solution that I could adapt to our needs.


