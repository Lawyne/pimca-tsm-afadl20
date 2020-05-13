# pimca-tsm-afadl20

## Requirements
Gradle
[OBP v0.0.8](https://bintray.com/plug-obp/distributions/download_file?file_path=obp2-remote-0.0.8.zip)

## How to use the model:

  1. Windows: 
  > .\gradlew.bat build run 
  
  1. Mac:
  > ./gradlew build run
2. Launch [OBP v0.0.8](https://bintray.com/plug-obp/distributions/download_file?file_path=obp2-remote-0.0.8.zip)
3. Open obp.remote
4. Open attackProperties.gpsl
5. Model check with ease !

## How to edit the model:
The model is located at 
>pimca-tsm-afadl20/plug-runtime-tsm/src/plug/language/tsm/examples/cta/

* Configuration.java contains variables
* ExMain.java contains guarded-command declaration

### How to create a guarded-command:
>        Behavior<Configuration> *[name]* =
>                new Behavior<>(
>                         "*[name]*",
>                        *[guard]*,
>                        *[command]*, 
>                        *[channel]*,
>                        *[isUrgent]*);
