# CyBench-IntelliJ

CyBench is an integrated code benchmark platform for running, storing, analyzing & comparing performance benchmarks with
one mission: help you build fast code.

Benchmark your code, algorithms, platforms, modules, libraries, API calls, JVMs, Java garbage collection (GC) settings.
Test with varying runtime configurations, versions and more. Avoid making costly mistakes, which can wreck your
reputation and user experience.

Create & share performance scorecards with your team & community. Compare and choose best performing runtime
configurations, libraries, code dependencies for your project.

Leverage Java Microbenchmark Harness -JMH benchmarks and integrate into your development, build and CI/CD workflow.
Build better, faster software with CyBench. Click here to see how CyBench works.

IntelliJ CyBench plugin simplifies your work creating and running benchmarks. The plugin creates two tool windows to
work with the reports:

* explorer view - shows you your reports already generated in project and enables you to open the selected plugin
* reports view - show the opened reports by explorer view

## Installation

### Supported IntelliJ IDEA versions

* Lowest:  `2018.1.x`
* Highest: `2020.3.x`

### Installation process

Download the [latest release](https://github.com/K2NIO/gocypher-cybench-intellij/releases/latest) and install it
manually using
<kbd>Preferences</kbd> → <kbd>Plugins</kbd> → <kbd>⚙️</kbd> → <kbd>Install plugin from disk...</kbd>
Or You can simply drag the downloaded plugin archive file and drop it into the IntelliJ IDEA Welcome screen.

![install from disc][install]

## Quick start

* Step 1:    Create or open project
* Step 2:    Create a benchmark, open a class you want to benchmark methods to; click `code -> generate...` or `alt+ins`
  to open `generate` menu, and select `CyBenchBenchmark`
* Step 3:    If it's needed plugin asks you to add the libraries to classpath and/or dependencies
* Step 4:    Run generated benchmark
* Step 5:    Open the CyBench explorer window (hit refresh if you don't see the report generated)
* Step 6:    Select the report to open it.

## Detailed instructions

### Generate benchmark stubs

Feature generates benchmark class stubs, adds necessary annotations and methods for a selected JAVA file. Benchmark
method is generated for each public method found in the class.

Pre-conditions:

* Selected file is Java file
* Index for IntelliJ files is already built

Steps:

* Select any JAVA class file in the project explorer which meets pre-conditions.
* Select menu Code->Generate (alt+ins)
* Select CyBench Generate.

![generation]

### Run

If it's benchmark classes, gutter button for running benchmark on benchmark classes will appear.

For a quick run, with default configuration press `Run 'run config name' ctrl+shift+f10`.

![running2][]

To create configuration press menu button `Create 'run configuration'...`

The dialog will appear to edit default settings. After editing setting hit `OK`.

**NOTE** If you want to send your report to CyBench, make sure to check "Send Report To CyBench" and enter your "Bench Query Token" (which can found on the CyBench UI) corresponding to the workspace you want your report stored in.

![running3]

You can also add a configuration for automated performance regression testing, which will run with every single
benchmark report.

**NOTE** In order to run automated comparisons, you must add the **benchQueryToken** to the configuration.

| Property name        | Description           | Options  |
| ------------- |-------------| -----:|
| **Run Performance Regression Test** | Choose whether or not to actually run a performance regression test with your benchmark. | true or false |
| **Scope** | Choose between comparing within current version, or between previous versions. When using `BETWEEN`, a specific version must be specified with the property `Compare Version`. | `WITHIN` or `BETWEEN` |
| **Compare Version** | Used for `BETWEEN` version comparisons. | Any project version you have previously tested |
| **Number of Latest Reports** | How many reports do you want to compare against? 1 will compare this report against the most recent report in the version you are comparing against. # > 1 will compare this report against the average of the scores of the most recent # reports in the version you are comparing against. | Number >= 1 |
| **Number of Allowed Anomalies** | How many anomalies do you want to allow? If the number of benchmark anomalies surpasses your specified number, CyBench benchmark runner will fail... triggering your CI/CD pipeline to halt. | Number >= 0 |
| **Comparison Method** | Decide which method of comparison to use. `DELTA` will compare difference in score, and requires an additional property, `Comparison Threshold`. `SD` will do comparisons regarding standard deviation. `SD` requires an additional property as well, `Deviations Allowed`. | `DELTA` or `SD` |
| **Comparison Threshold** | Only used with the `DELTA` method. `GREATER` will compare raw scores, `PERCENT_CHANGE` is used to measure the percent change of the score in comparison to previous scores. `PERCENT_CHANGE` requires an additional property: `Percent Change Allowed`. | `GREATER` or `PERCENT_CHANGE` |
| **Percent Change Allowed** | This argument is used when running assertions, makes sure your new score is within X percent of the previous scores you're comparing to. | Any Double value. |
| **Deviations Allowed** | Used with assertions to check that the new score is within the given amount of deviations from the mean. (mean being calculated from the scores being compared to). | Any Double value. |

![running4]

Select created Run Configuration and press `Run` to start benchmarking. Benchmarking console will appear.

![runConsole]

Once the benchmarking is finished, the report will be created in project directory under `reports` child. You can view
all reports in Explorer window.

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
