# pimca-tsm-afadl20

## Requirements
* Java v8.0
* Gradle v6.0
* [OBP v0.0.8](https://bintray.com/plug-obp/distributions/download_file?file_path=obp2-remote-0.0.8.zip)

## How to use the model:
  1. Windows: 
  ```
  .\gradlew.bat build run 
  ```
  1. Mac:
  ```
  ./gradlew build run
  ```
2. Launch [OBP v0.0.8](https://bintray.com/plug-obp/distributions/download_file?file_path=obp2-remote-0.0.8.zip)
3. Load obp.remote
4. Load attackProperties.gpsl
5. Model check with ease !

## How to edit the model:
The model is located at 
>pimca-tsm-afadl20/plug-runtime-tsm/src/plug/language/tsm/examples/cta/

* Configuration.java contains variables
* ExMain.java contains guarded-command declaration within execution units.

### How to create a guarded-command:
```
        Behavior<Configuration> [name] =
                new Behavior<>(
                        [name],
                        [guard],
                        [command], 
                        [channel],
                        [isUrgent]);
```                        
  
* *[name]* is the guarded-command name.
* *[guard]* is a boolean function of a Configuration which must be true for the guarded-command to be executed.
* *[command]* is a Configuration function of a Configuration which modify the current Configuration into a new one once the guarded-command is triggered.
* *[channel]* is the synchronisation channel of the guarded-command *cf Example*, leave empty if none.
* *[isUrgent]* is a boolean stipulating if the guarded-command is *urgent*.

### Example: Sensor
* In Configuration.java:
```	      
       //Sensor
       public boolean sTriggerSensor = false;
       public int sWaterLevel = 10;
       public boolean sIsCorrupted = false;
```
* In ExMain.java
```        
List<Behavior<Configuration>> sensor() {

        Behavior<Configuration> f2c =
                new Behavior<>(
                         "f2c",
                        (c) -> true,
                        (c) -> {
                            c.sWaterLevel = c.wtWaterLevel;
                            c.sTriggerSensor = true;
                            return c;
			    },  
                        Channel.in("measure")
                        ,false);
        Behavior<Configuration> c2f =
                new Behavior<>(
                         "c2f",
                         (c) -> c.sTriggerSensor && !c.sIsCorrupted,
                         (c) -> {
                             c.sTriggerSensor = false;                            
                             return c;
 			     },   
                        Channel.out("updateLevel"),
                        true);
        Behavior<Configuration> c2fa =
                new Behavior<>(
                         "c2fattacked",
                         (c) -> c.sTriggerSensor && c.sIsCorrupted,
                         (c) -> {
                             c.sTriggerSensor = false;                            
                             return c;
 			     },
                         true);
        Behavior<Configuration> a2a =
                new Behavior<>(
                         "attacked",
                        (c) -> true,
                        (c) -> {
                             c.sIsCorrupted = true;
                             return c;
                             }, 
                        Channel.in("corruptSensor")
                        ,false);
        return Arrays.asList(f2c,c2f,c2fa,a2a);
    }
   ```
  
The sensor contains 4 guarded-commands:
  Name | Guard | Command | Channel | Urgent
  ---- | ----- | ------- | ------- | ------
  f2c | always true | pull the water level from the tank, trigger the sensor | in (measure) *(from the water tank)* | No
  c2f | sensor is triggered, sensor is not corrupted | remove trigger | out (updateLevel) *(to the PLC)* | Yes
  c2fa | sensor is triggered, sensor is corrupted | remove trigger | none | Yes
  a2a | always true | corrupt the sensor | in (corruptSensor) *(from the attacker)* | No
  
The sensor relays the water level from the water tank to the PLC if it is working properly. Whenever the attacker corrupt the sensor, the sensor no longer relays the water level to the PLC.

## How to check properties
Within OBP, edit *attackProperties.gpsl* using LTL syntax.
Property declaration follows this pattern:
> [name] = [LTL]
* *[name]* is the property name.
* *[LTL]* should refer to model variable using pipes ``` |variable name|```
