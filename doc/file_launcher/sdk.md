## SDK

Our plugin uses an IntelliJ IDEA specific feature which is the idea of SDKs, in the basic version of
IntelliJ IDEA the most common SDK that will be defined is the Java SDK also called the JDK. The
reason why this is an IntelliJ IDEA specific feature is explained in the section explaining the
differences between the different types of JetBrains IDEs.

An SDK lets us define an API library that is used to build a project and in the case of multi-module
projects lets us define an SDK for each module. This functionality is very useful for projects where
the frontend and backend is in the same project but they are written in different languages, an
example being a backend using Java with [Spring](https://spring.io/) and the frontend
[Typescript](https://www.typescriptlang.org/)/[Javascript](https://www.javascript.com/)
with [AngularJS](https://angularjs.org/), for more information about the idea of SDKs in IntelliJ
visit their help page about [SDKs](https://www.jetbrains.com/help/idea/sdk.html).

### Custom SDK

In our project we created our own custom SDK, the reason being it was one of the easiest
ways to provide persistance between launches of the IDE and the configuration is in a common
place for regular IntelliJ users.

To implement a custom SDK, we need to extend the `SdkType` abstract class provided by JetBrains.
This class has a couple of abstract methods that have to be implemented, these being:

+ `string suggestHomePath()`
+ `boolean isValidSdkHome(String path)`
+ `String suggestSdkName(String currentSdkName, String sdkHome)`
+ `void saveAdditionalData(SdkAdditionalData additionalData, Element additional)`
+ `AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)`
+ `String getPresentableName()`

There are also some other methods that can be overriden, for a full list check the source code
for the [`SdkType` class](https://upsource.jetbrains.com/idea-ce/file/idea-ce-a00d19098ca9850e1b28a9db178df5a4b3456659/platform/lang-api/src/com/intellij/openapi/projectRoots/SdkType.java?line=36). In our plugin we override a couple of these.

+ `FileChooserDescriptor getHomeChooserDescriptor()`
+ `String getVersionString(String sdkHome)`
+ `Icon getIcon()`
+ `Icon getIconForAddAction()
+ `boolean isRootTypeApplicable(OrderRootType type)`

And of course we also have some static fields as well as a constructor.

In depth explanations and examples from our plugin of these methods follow.

#### String suggestHomePath()

The `String suggestHomePath()` method is used for the file chooser when choosing the executable.
If your plugin is aimed at being crossplatform, Windows, macOS or Linux, you'll have to take
this into account in this method and return different paths for each, the path to where the main executable is normally found.

The example from our plugin, written in Kotlin, is depicted in listing \ref{code:prologsdktype}.

\begin{listing}[h]
\inputminted[firstline=127, lastline=158, linenos, fontsize=\footnotesize]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\caption{suggestHomePath method}
\label{code:prologsdktype}
\end{listing}

The `getLatestVersions(versions)`method call at line 167, is used to get the latest version of the executable if a directory was selected which contains multiple versions of the same executable, this method is not really necessary.

#### boolean isValidSdkHome(String path)

The `boolean isValidSdkHome(String path)` method is used to check that the path really points to an executable that we are looking for. This method uses some other methods, all of these are depicted in listing \ref{code:isvalidsdk}.

\begin{listing}[h]
\inputminted[firstline=160, lastline=162, linenos]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\inputminted[firstline=42, lastline=56, linenos]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\inputminted[firstline=58, lastline=58, linenos, breaklines]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\caption{isValidSdkHome method}
\label{code:isvalidsdk}
\end{listing}

#### String suggestSdkName(String currentSdkName, String sdkHome)

The `String suggestSdkName(String currentSdkName, String sdkHome)` method is used to get a
string to display the information of the selected SDK, in our case that means appending the version
of the executable to `GNU-Prolog` or returning `Unknown` if we do not have the version.

This method uses some other methods, all of these are depicted in listing \ref{code:suggestsdkname}.

\begin{listing}[h]
\inputminted[firstline=164, lastline=189, linenos, breaklines]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\inputminted[firstline=60, lastline=65, linenos, breaklines]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\caption{suggestSdkName method}
\label{code:suggestsdkname}
\end{listing}

#### void saveAdditionalData(SdkAdditionalData additionalData, Element additional)

This method can be used to save additional data, data that is not the path to the SDK (main executable),  examples are other executables for package managers or external formatters like `gofmt`, these can then be used during build time.

In our plugin does not use any additional data so we can simply override it and leave it empty.

#### AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel   sdkModel, SdkModificator sdkModificator)

This method can be used to return an instance of a class implementing the `AdditionalDataConfigurable` interface, which enables us to modify the SdkAdditionalData informations through a form, that we would need to create ourselves.

#### String getPresentableName()

The `String getPresentableName()` method is similar to `suggestSdkName(String currentSdkName, String sdkHome)` but returns a more generic name that is used in the list of available SDKs that can be defined, the method is depicted in listing \ref{code:getpresentablename}.

\begin{listing}[h]
\inputminted[firstline=202, lastline=204, linenos, breaklines]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\caption{getPresentableName method}
\label{code:getpresentablename}
\end{listing}

#### FileChooserDescriptor getHomeChooserDescriptor()

The `FileChooserDescriptor getHomeChooserDescriptor()` method defines the file chooser dialog that is shown when we click on the `New` button. We need to return a `FileChooserDescriptor`, in the file chooser descriptor we can set some defaults for the
dialog, display hidden files, and also what constitutes a valid file or directory, most often it uses the `boolean isValidSdkHome(String path)` method.

This method does not need to be overriden as the `SdkType` base class already provides an implementation, but it can be useful to override it if we have some very specific verifications we want to do, as depicted in listing \ref{code:gethomechooser}.

\begin{listing}[h]
\inputminted[firstline=93, lastline=125, linenos, breaklines]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\caption{getHomeChooserDescriptor method}
\label{code:gethomechooser}
\end{listing}

#### String getVersionString(String sdkHome)

The `String getVersionString(String sdkHome)` method is used to retrieve the version of the
executable. This is not absolutely necessary but becomes useful when your plugin supports different versions of the same executable, it can in some places replace a separate version manager like [`nvm`](https://github.com/creationix/nvm) if you want to test your project on different versions.

In our plugin we simply return `1.4.4`, as depicted in listing \ref{code:getversionstring}, since that is at the moment of writing the only version of GNU-Prolog used but in other cases we can use a Regex to extract the version number from the parent directory or even the executable depending on the language.

\begin{listing}[h]
\inputminted[firstline=179, lastline=289, linenos, breaklines]{kotlin}{code-source/ch/eif/intelliprolog/sdk/PrologSdkType.kt}
\caption{getVersionString method}
\label{code:getversionstring}
\end{listing}

#### Icon getIcon()

This method simply returns the icon we wish to display next to our SDK, in our case we simply get the icon from our `PrologIcons` class.

#### PrologSdkType(), constructor

In the constructor of our custom SDK we simply call the parent class constructor with the name of our SDK, which in our case is `GPROLOG`.

#### Static fields

In our class we define only a single static field, an instance field.

### Registering the custom SDK

The `SdkType` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is depicted in listing \ref{reg:sdktype}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize,firstline=34, lastline=34]{xml}{code-resources/META-INF/plugin.xml}
\caption{SdkType registration}
\label{reg:sdktype}
\end{listing}
