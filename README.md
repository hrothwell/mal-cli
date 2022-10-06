[![Gradle Build](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml/badge.svg)](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml)

# anime-cli
A CLI to interact with MyAnimeList's web API

## [Pre Requirements to using](../../wiki/Pre-Requirements)

## [Usage](../../wiki/Usage)
``` 
~ anime -h
Usage: anime [OPTIONS] COMMAND [ARGS]...

  CLI for interacting with MAL

Options:
  -h, --help  Show this message and exit

Commands:
  login    Login/authorize this app to have more access to MAL API
  random   Select a random anime from your lists
  suggest  Get anime suggestions!
  refresh  call to refresh your oauth tokens

extended user guide, source code, etc - https://github.com/hrothwell/anime-cli
```

## Latest Windows native exe
- Latest windows exe build can be found [here](./native-image-items/build-results/anime.exe)
- I am trying to only ever merge completely functional/working native-image code, but no promises this will always work. More automated testing to come
- Ideally get versioning setup as well
