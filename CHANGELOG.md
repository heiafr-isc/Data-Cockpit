# Changelog

All notable changes to this project will be documented in this file.

## 1.1.0 (2024.09.02)
Add the abstract class AbstractInOutDataManager to manage dependencies between components in a cleaner way.
- database: The recreated AbstractInOutDataManager implements both AdvancedDataRetriever and AbstractResultsManager interfaces. The central SmartDataPointCollector class can now simply extends the new abstract class. This way, the Database component make a more suitable use of interfaces hold by the general_libraries and still offers two dedicated "parts" useful for both the Tree and the Visualizer.
- general_libraries: Now holds the AdvancedDataRetriever and AbstractResultsManager interfaces. Conceptually, this is a better approach to protect other component from depending on each other. The general_libraries is meant to offers these kind of interface
- tree: Simple update to import interfaces placed in the general_libraries instead of the Database

## 1.0.1 (2024.09.01)
Deletion of unused interface known as SaveAndLoadAble
- database: The component has been adapted to the removal of the SaveAndLoadAble interface. The SmartDataPointCollector class no longer implements the interface

## 1.0.0 (2024.07.12)
Start of Data-Cockpit multi-module project : 

The program has been refactored and prepared for this public repository. 
