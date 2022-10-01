[![Gradle Build](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml/badge.svg)](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml)

# anime-cli
simple tool to select a random anime from a user's MAL list

## Requirements
- [MAL API client id](https://myanimelist.net/apiconfig)
- said client id is placed in a json file located at `${HOME}\anime-cli\mal-secret.json` 
- probably need java if wanting to make your own build, if on Windows there is a provided exe file in `.\windows-native-image-exe`

## Usage (gradle, jar, and `build\scripts`)
- gradle: `.\gradlew run`
- jar
  - `.\gradlew clean build`
  - `java -jar build\libs\anime-cli-1.0.SNAPSHOT-all.jar`
- scripts:
  - `.\gradlew clean build`
  - `build\install\anime-cli\bin\anime-cli` or `build\install\anime-cli\bin\anime-cli.bat`
- inputs
  - `-u --user-name` Defaults to value of `user_name` in `${HOME}\anime-cli\mal-secret.json`
  - `-l --list-status [completed|plan_to_watch|watching|on_hold|dropped]` list to select from
  - `-h --help` show the help info

## Building a native image using GraalVM
- **Pre req:**
  - Follow install directions for [GraalVM](https://www.graalvm.org/22.2/docs/getting-started/#install-graalvm)
    - I developed this on Windows, which has extra [pre-reqs](https://www.graalvm.org/22.2/docs/getting-started/windows/#prerequisites-for-using-native-image-on-windows)
    - [GraalVM and Native Image on Windows 10 article](https://medium.com/graalvm/using-graalvm-and-native-image-on-windows-10-9954dc071311)
- After all the pre-req, I had a hard time finding what exactly would work, what commands to run with what parameters, etc. The exact process character for character that I did is as follows:
  - Open CMD as administrator, run the necessary VS code script: `"C:\Program Files (x86)\Microsoft Visual Studio\2019\BuildTools\VC\Auxiliary\Build\vcvars64.bat"`
    - replace path with wherever your bat file is located
  - Starting in project root:
    - `.\gradlew clean build`
    - `cd build\libs`
    - `mkdir META-INF\native-image`
    - `java -agentlib:native-image-agent=config-output-dir=META-INF\native-image -jar anime-cli-1.0-SNAPSHOT-all.jar`
    - `native-image --no-fallback --report-unsupported-elements-at-runtime -H:ConfigurationFileDirectories=META-INF\native-image -jar anime-cli-1.0-SNAPSHOT-all.jar anime --enable-url-protocols=https --no-server`
      - Specifying the config directory `META-INF\native-image` was crucial for me, I saw some examples online stating `META-INF\native-image` was automatically seen/pulled in when using `native-image`, but it didn't work for me. Likely user error :P
    - run the cli! `anime`
  - After this process, `.\build\libs\anime.exe` should be present, from here I just copied it out as this build folder is constantly overwritten by gradle
  - ~~I may potentially write a script to automate this whole process, would be fairly specific to this application as I have several flags I am using for the `native-image` calls and what not~~ already wrote one
  - **tl;dr for Windows**
    - open cmd as admin
    - run VS code script 
    - run `build-native-image-windows.bat`
    - if all goes well, exe file present in `.\native-image-result`
