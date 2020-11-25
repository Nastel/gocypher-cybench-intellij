# CyBench-Intellij

CyBench is an integrated code benchmark platform for running, storing, analyzing & comparing performance benchmarks with one mission: help you build fast code.

Benchmark your code, algorithms, platforms, modules, libraries, API calls, JVMs, Java garbage collection (GC) settings. Test with varying runtime configurations, versions and more. Avoid making costly mistakes, which can wreck your reputation and user experience.

Create & share performance scorecards with your team & community. Compare and choose best performing runtime configurations, libraries, code dependencies for your project. 

Leverage Java Microbenchmark Harness -JMH benchmarks and integrate into your development, build and CI/CD workflow. Build better, faster software with CyBench. Click here to see how CyBench works.

Intellij CyBench plugin simplifies your work creating and running benchmarks. The plugin creates two tool windows to work with the reports: 

* explorer view - shows you your reports already generated in project and enables you to open the selected plugin
* reports view - show the opened reports by explorer view

## Quick start

* Step 1:	Create or open project
* Step 2:	Create a benchmark, open a class you want to benchmark methods to; click `code -> generate...` or `alt+ins` to open `generate` menu, and select `CyBenchBenchmark`
* Step 3:	If it's needed plugin asks you to add the libraries to classpath and/or dependencies
* Step 4:	Run generated benchmark
* Step 5:	Open the CyBench explorer window (hit refresh if you don't see the report generated) 
* Step 6:	Select the report to open it.

## Installation

Download the [latest release](https://github.com/K2NIO/gocypher-cybench-intellij/releases/latest) and install it manually using
<kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>
Or You can simply drag the downloaded plugin archive file and drop it into the IntelliJ IDEA Welcome screen.

![install from disc][install]


## Detailed instructions

### Generate benchmark stubs 

Feature generates benchmark class stubs, adds necessary annotations and methods for a selected JAVA file. Benchmark method is generated for each public method found in the class.

Pre-conditions:
* Selected file is Java file
* Index for intellij files is already built 

Steps:

* Select any JAVA class file in the project explorer which meets pre-conditions.
* Select menu Code->Generate (alt+ins)
* Select CyBench Generate.

![generation]

### Run

If it's benchmark classes, gutter button for running bechmark on benchmark classes will appear.

For a quick run, with default configuration press `Run 'run config name' ctrl+shift+f10`.

![running2][]

To create configuration press menu button `Create 'run configuration'...`

The dialog will appear to edit default settings. After editing setting hit `OK`.  

![running3]

Select created Run Configuration and press `Run` to start benchmarking. Benchmarking console will appear.

![runConsole]

Once the benchmarking is finished, the report will be created in project directory under `reports` child.
You can view all reports in Explorer window.

![explorer]

Once you click on particular report the report ToolWindow will appear at IDE's bottom panel.

![report]
 
Every test on report is displayed as item on tree at left, you can select individual tests to see the results.

![report2]

[install]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/install.JPG "Install manually"
[explorer]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/explorer.JPG ""
[generation]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/generating.JPG ""
[install2]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/install2.JPG ""
[report]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/report.JPG " "
[report2]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/report2.JPG " "
[runConsole]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/runConsole.JPG " "
[running1]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/running1.JPG " "
[running2]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/running2.JPG " "
[running3]: https://github.com/K2NIO/gocypher-cybench-intellij/raw/main/docs/img/running3.JPG " "
