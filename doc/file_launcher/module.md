## Module

A module in IntelliJ IDEA is a discrete unit encompassing different functionalities as well as source files, run configurations, etc. Each module is independent of the the others contained within the same project and can even have completely different SDKs defined.

//TODO Add more info about modules in IntelliJ

IntelliJ IDEA has some common module types available, mostly for the Java Programming Language, a short
non-exhaustive list of them is:

+ Java
+ Java Enterprise
+ Spring
+ IntelliJ Platform Plugin

Since we are creating a plugin for a language that has no direct connection to Java we will need to
implement our own module type.

The basic building blocks for a module type are:

+ Extending the `ModuleType` class provided by JetBrains
+ Extending the `ModuleBuilder` class provided by JetBrains

### ModuleType

Our module is very simple for our plugin mainly because we don't need to define a lot for the GNU-Prolog language and for that reason we will only implement the abstract methods from the
`ModuleType` class, these methods are as follows:

+ abstract T createModuleBuilder()
+ abstract String getName()
+ abstract String getDescription()
+ abstract Icon getNodeicon(boolean isOpened)

We will also override the `Icon getIcon()` method. We also have a static instance field to this class.

#### T createModuleBuilder()

This method creates our `ModuleBuilder` instance and since we are creating our own module builder,
the return type will be `PrologModuleBuilder`. The only thing we do in this method is return a new
`PrologModuleBuilder` instance, as depicted in listing \ref{code:createmodulebuilder}.

\begin{listing}[h]
\inputminted[firstline=10, lastline=12, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleType.kt}
\caption{createModuleBuilder method}
\label{code:createmodulebuilder}
\end{listing}

#### String getName() and String getDescription()

The `getName()` method simply returns the name of our module, in our case that is `Prolog Module`, as depicted in listing \ref{code:getname-module}.

\begin{listing}[h]
\inputminted[firstline=14, lastline=16, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleType.kt}
\caption{getName method}
\label{code:getname-module}
\end{listing}

The `getDescription()` method simply returns the description of our module, in our case that is `Prolog Module`, as depicted in listing \ref{code:getdescription-module}.

\begin{listing}[h]
\inputminted[firstline=18, lastline=20, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleType.kt}
\caption{getDescription method}
\label{code:getdescription-module}
\end{listing}

#### Icon getIcon() and Icon getNodeIcon(boolean isOpened)

The `getIcon()` method returns our desired icon for the module, in our case it comes from our `PrologIcons`
class, as depicted in listing \ref{code:geticon-module}.

\begin{listing}[h]
\inputminted[firstline=22, lastline=24, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleType.kt}
\caption{getIcon method}
\label{code:geticon-module}
\end{listing}

The `getNodeIcon(boolean isOpened)` method returns our desired icon for the module, in our case it comes from our `PrologIcons`
class, as depicted in listing \ref{code:getnodeicon-module}.

\begin{listing}[h]
\inputminted[firstline=26, lastline=28, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleType.kt}
\caption{getNodeIcon method}
\label{code:getnodeicon-module}
\end{listing}

### ModuleBuilder

The module builder is used for the project wizard when we create a new project with our module type
, since we are developing a plugin for `GNU-Prolog` which does not have a very complex system
surrounding it we will implement a very simple module builder based on the Java module builder. This
prevents us from having to create everything ourselves. The `JavaModuleBuilder` provides wizard steps to specify the root directory and name our project.

The methods that we'll implement or override are the following:

+ String getBuilderId()
+ ModuleWizardStep modifySettingsStep(SettingsStep settingsStep)
+ String getGroupName()
+ String getPresentableName()
+ ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, ModulesProvider modulesProvider)
+ abstract ModuleType getModuleType()
+ abstract void setupRootModel(ModifiableRootModel modifiableRootModel)
+ boolean isSuitableSdktype(SdkTypeId sdktype)

In depth explanations and examples taken from our plugin follow.

#### String getBuidlerid()

This method is used to specify a unique name/id for our module builder so as not to clash with any other module builders defined through plugins added to IntelliJ, as depicted in listing \ref{code:getbuilderid}.

\begin{listing}[h]
\inputminted[firstline=18, lastline=18, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{getBuilderId method}
\label{code:getbuilderid}
\end{listing}

#### ModuleWizardStep modifySettingsStep(SettingsStep settingsStep)

This method is used to modify a settings step when the context changes, in particluar after changing
from the first step to the second step, at least as far as I understand it.

In our case we are using the the standard Java `modifySettingsStep(SettingsStep settingsStep, ModuleBuilder moduleBuilder)`, as depicted in listing \ref{code:modifysettingsstep}.

\begin{listing}[h]
\inputminted[firstline=20, lastline=21, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{modifySettingsStep method}
\label{code:getmodifysettingsstep}
\end{listing}

#### String getGroupName() and String getPresentableName()

This method returns the name of the group we want our module to appear in during the project wizard.
We are simply going to call the group `Prolog`, as depicted in listing \ref{code:getgroupname-modulebuilder}.

\begin{listing}[h]
\inputminted[firstline=24, lastline=24, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{getGroupName method}
\label{code:getgroupname-modulebuilder}
\end{listing}

This method returns the name to be displayed for the module type to appear during the project wizard.
We are simply going to return `Prolog`, as depicted in listing \ref{code:getpresentablename-modulebuilder}.

\begin{listing}[h]
\inputminted[firstline=26, lastline=26, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{getPresentableName method}
\label{code:getpresentablename-modulebuilder}
\end{listing}

#### ModuleWizardStep[] createWizardSteps(WizardContext wizardContext, ModulesProvider modulesProvider)

This method returns all the `ModuleWizardSteps` to be displayed for this module, it's in this method
that we can create and return our own custom steps for our module.

In our case we simply return the default module steps, as depicted in listing \ref{code:createwizardsteps}, since that is all we need for our plugin.

\begin{listing}[h]
\inputminted[firstline=28, lastline=29, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{createWizardSteps method}
\label{code:createwizardsteps}
\end{listing}

#### ModuleType getModuleType()

This method simply returns our custom module type, `PrologModuleType`, more specifically our static
instance field, as depicted in listing \ref{code:getmoduletype}.

\begin{listing}[h]
\inputminted[firstline=31, lastline=33, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{getModuleType method}
\label{code:getmoduletype}
\end{listing}

#### void setupRootModel(ModifiableRootModel modifiableRootModel)

This method is probably one of the most important in the `ModuleBuilder` class in my opinion, since
it allows us to define the directory structure of the module in question as well as files if needed,
as depicted in listing \ref{code:setuprootmodel}

\begin{listing}[h]
\inputminted[firstline=35, lastline=52, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{setupRootModel method}
\label{code:setuprootmodel}
\end{listing}

#### boolean isSuitableSdktype(SdkTypeId sdktype)

This method is used to check if the SDK is suitable for the module. In our own case we simply check
if the SDK passed in as argument is an instance of our custom SdkType, as depicted in listing \ref{code:issuitablesdk}.

\begin{listing}[h]
\inputminted[firstline=54, lastline=56, linenos]{kotlin}{code-source/ch/eif/intelliprolog/module/PrologModuleBuilder.kt}
\caption{isSuitableSdktype method}
\label{code:issuitablesdk}
\end{listing}

### Registering the module type

The `ModuleType` needs to be added to the extensions section of the `plugin.xml` file,
the element that needs to be added is depicted in listing \ref{reg:moduletype}.

\begin{listing}[h]
\inputminted[breaklines, fontsize=\footnotesize,firstline=32, lastline=32]{xml}{code-resources/META-INF/plugin.xml}
\caption{ModuleType registration}
\label{reg:moduletype}
\end{listing}
