
# Data-Cockpit

Welcome to the project! This document will guide you through various sections.

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)
- [Issue and bug report](#issue)
- [Contributing](#contributing)
- [Discussions](#discussion)
- [License](#license)

## Introduction

Data-Cockpit is a suite of integrated software tools designed for configuring, executing, and visualizing digital experiments. A digital experiment involves running a software application based on predefined hypotheses and parameters to perform calculations and generate results. Experiment plans involve multiple runs of the software with varying configurations, enabling users to compare the impact of different parameters, hypotheses, or strategies on the outcomes.

Data-Cockpit facilitates the entire process, from the setup and execution of experiments to the analysis of data generated by Java programs.

## Installation

In order to use this program for developement purpose, anyone can freely fork this project. The code-base comes with all the different Data-Cockpit components ready to use in their latest version. 
As the Data-Cockpit is still under developement, it currenlty still relies on the availability of the /div folder located at the root of the project. This folder contains various mandatory configurations used to set the project and to enable the looging feature. 
Even if the project is ready to be used without further configuration, the removal of these configurations are planned for future releases. 

To find more information about concret use of the program, check the [Usage](#usage) entry below. 

## Usage

The Data-Cockpit can be used to configure, run and visualize various execution plans. This can be achieved by creating its own implementations of the Experiment interface. 

Then, the Tree component is responsible for the enumeration of all the availible Experiment configuration within the program and will display them by using its own GUI. 
The Database component registers all the Datapoint object created by the Tree and will manage them in order to redistribute them to the visualizer component or any other visualizer tools a user may have produced. 
The Visualizer component is responsible for Data rendering by using various available graphs. It fetch the content of the Database component and compute the recieved information. 

For more details explanation please take a look at the [getting started page](https://github.com/heiafr-isc/Data-Cockpit/wiki/Getting-Started)

## Issue

Anyone is free to produce new issues. Important informations about that matter can be consulted on the associated project's wiki page about [issues](https://github.com/heiafr-isc/Data-Cockpit/wiki/Setup-an-issue)

## Contributing

Every contribution for the Data-Cockpit project are welcomed!

See the [contributors guideline](https://github.com/heiafr-isc/Data-Cockpit/wiki/Contributors-guideline) wiki page for more information on how to contribute to the project.

> We would like to have this repository in a polite and friendly atmosphere, so please be kind and respectful to others. For more details, look at [Code of Conduct](https://github.com/heiafr-isc/Data-Cockpit/blob/main/CODE_OF_CONDUCT.md).

## Discussion

If you want to submit an idea, ask something about the program behavior or other matter than are not suitable to open a new issue. Feel free to check the ["Discussions"](https:/github.com/heiafr-isc/Data-Cockpit/discussions) tab

## License

As this project is meant to be and remained open-source among all hypothetical distributions of the Data-Cockpit Software, the LGPL v3 license is applied to this project. 

Make sure to always add the license header to any code file you may produce. In case of modification of a project file, please add your personal informations in the header's contributor section of the file and the date of your modifications. 

The [full license](https://github.com/heiafr-isc/Data-Cockpit/blob/main/LICENSE) is available in the project root directory. This license must be equivalently applied to all possible forks or distributions of that project. Wether the software is entirely or partially reused. Make sure to take good not of the LICENSE document content. 

