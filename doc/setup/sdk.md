## SDK setup

The setup of the IntelliJ Platform SDK and common JDK is a very easy step in the setup process for the
development of IntelliJ plugins but probably one of the most important ones.

We will first setup the common JDK, followed by the IntelliJ Platform SDK and IntelliJ Community Edition
source files.

### Common JDK

The setup of the common JDK is accomplished through the Project Structure dialog that can be
reached through the File menu in an open project or the Configure menu on the IntelliJ IDEA start page,
illustrated in figures \ref{fig:projstructfile} and \ref{fig:projstructconf}.

\begin{figure}
\centering
\includegraphics[width=\textwidth/4]{content/images/project_settings_menu_entry1.png}
\caption{Project Structure in File menu}
\label{fig:projstructfile}
\end{figure}

\begin{figure}
\centering
\includegraphics[width=\textwidth/2]{content/images/project_settings_menu_entry2.png}
\caption{Project Structure in Configure menu on start page}
\label{fig:projstructconf}
\end{figure}

In the Project Structure dialog, select the SDK item on the left side, followed by clicking the +
sign and selecting JDK, as illustrated in the figure \ref{fig:commonjdk}.

\begin{figure}
\centering
\includegraphics[width=\textwidth/2]{content/images/project_structure1.png}
\caption{Setup common JDK in Project Structure}
\label{fig:commonjdk}
\end{figure}

We then select the JDK source folder we wish to setup, in our case we selected the Java 8 JDK.

### IntelliJ Platform SDK

The setup of the IntelliJ Platform SDK is done in the same window as the common JDK, and the same +
sign but selecting IntelliJ Platform SDK instead, as illustrated in the figure \ref{fig:intellijsdk}.

\begin{figure}
\centering
\includegraphics[width=\textwidth/2]{content/images/project_structure2.png}
\caption{Setup IntelliJ Platform SDK}
\label{fig:intellijsdk}
\end{figure}

We then select the directory containing the install IntelliJ IDEA, normally IntelliJ should suggest
it by default, after that select the previously configured common JDK, illustrated in figure \ref{fig:jdksdk}.

\begin{figure}
\centering
\includegraphics[width=\textwidth/2]{content/images/project_structure3.png}
\caption{Select common JDK for the IntelliJ Platform SDK}
\label{fig:jdksdk}
\end{figure}

### IntelliJ Community Edition source code

After configuring the common JDK and IntelliJ Platform SDK, we can setup the IntelliJ source code.
This is done by changing to the Sourcepath tab while the selection is on the IntelliJ Platform SDK,
and then clicking the + symbol and selecting the root directory where you checked out the IntelliJ
from GitHub, illustrated in figure \ref{fig:intellijsource}.

\begin{figure}
\centering
\includegraphics[width=\textwidth/4]{content/images/project_structure4.png}
\caption{Configuring sourcepath of IntelliJ Community}
\label{fig:intellijsource}
\end{figure}
