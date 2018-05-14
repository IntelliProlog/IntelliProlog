## SDK

Our plugin uses an IntelliJ IDEA specific feature which is the idea of SDKs, in the basic version of
IntelliJ IDEA the most common SDK that will be defined is the Java SDK also called the JDK. The
reason why this is an IntelliJ IDEA specific feature is explained in the section explaining the
differences between the different types of JetBrains IDEs.//TODO reference later section

An SDK lets us define an API library that is used to build a project and in the case of multi-module
projects lets us define an SDK for each module. This functionality is very useful for projects where
the frontend and backend is in the same project but they are written in different languages, an
example being a backend using Java with [Spring](https://spring.io/) and the frontend
[Typescript](https://www.typescriptlang.org/)/[Javascript](https://www.javascript.com/)
with [AngularJS](https://angularjs.org/), for more information about the idea of SDKs in IntelliJ
visit their help page about [SDKs](https://www.jetbrains.com/help/idea/sdk.html).





