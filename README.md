How to run the demo's
---------------------

The project setup is identical to the project setup of the RinSim simulator. We added an additional 
maven module for our project. However, you won't be able to use only this module because we made some
changes in the core and UI. 

All demo's are located in project.strategies.*.*Demo.java. These will run with the user interface. 
Automated batch experiments are located in project.experiments.*. Every non-abstract Experiment subclass
has a main() method and can be run.

The results are written to CSV files in project/files/results. 
