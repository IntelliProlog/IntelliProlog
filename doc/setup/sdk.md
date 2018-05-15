## SDK setup

The setup of the IntelliJ Platform SDK and common JDK is a very easy step in the setup process for the
development of IntelliJ plugins but probably one of the most important ones.

We will first setup the common JDK, followed by the IntelliJ Platform SDK and IntelliJ Community Edition
source files.

### Common JDK

The setup of the common JDK is accomplished through the Project Structure dialog that can be
reached through the File menu in an open project or the Configure menu on the IntelliJ IDEA start page.

![Project Structure in File menu](content/images/project_settings_menu_entry1.png)

![Project Structure in Configure menu on start page](content/images/project_settings_menu_entry2.png)

In the Project Structure dialog, select the SDK item on the left side, followed by clicking the +
sign and selecting JDK, as illustrated in the following image.

![Setup common JDK in Project Structure](content/images/project_structure1.png)

We then select the JDK source folder we wish to setup, in my case I selected the Java 8 JDK.

### IntelliJ Platform SDK 

The setup of the IntelliJ Platform SDK is done in the same window as the common JDK, and the same +
sign but selecting IntelliJ Platform SDK instead, as illustrated in the following image.

![Setup IntelliJ Platform SDK](content/images/project_structure2.png)

We then select the directory containing the install IntelliJ IDEA, normally IntelliJ should suggest
it by default, after that select the previously configured common JDK.

![Select common JDK for the IntelliJ Platform SDK](content/images/project_structure3.png)

### IntelliJ Community Edition source code

After configuring the common JDK and IntelliJ Platform SDK, we can setup the IntelliJ source code.
This is done by changing to the Sourcepath tab while the selection is on the IntelliJ Platform SDK,
and then clicking the + symbol and selecting the root directory where you checked out the IntelliJ
from GitHub.

![Configuring sourcepath of IntelliJ Community](content/images/project_structure4.png)
