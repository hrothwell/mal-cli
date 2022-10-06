[![Gradle Build](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml/badge.svg)](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml)

# anime-cli
A CLI to interact with MyAnimeList's web API

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

## Latest Windows native exe
- Latest windows exe build can be found [here](./native-image-items/build-results/anime.exe)
- I am trying to only ever merge completely functional/working native-image code, but no promises this will always work. More automated testing to come
- Ideally get versioning setup as well
