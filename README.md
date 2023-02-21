[![Gradle Build](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml/badge.svg)](https://github.com/hrothwell/anime-cli/actions/workflows/gradle.yml)

# mal-cli

A CLI to interact with MyAnimeList's web API

## [Pre Requirements to using](../../wiki/Pre-Requirements)

## [Usage](../../wiki/Usage)

``` 
~ mal -h

Usage: mal [OPTIONS] COMMAND [ARGS]...

  CLI for interacting with MAL

Options:
  --debug / --no-debug  run in debug mode and get more log output
  --loud / --quiet      attempt to open any anime pages, open login url, etc.
                        default true / loud
  -a / -m               to run commands for anime (-a) or manga (-m)
  -h, --help            Show this message and exit

Commands:
  login    Login/authorize this app to have more access to MAL API
  random   Select a random anime/manga from your lists
  suggest  Get anime suggestions! Manga suggestions not yet supported by MAL
  refresh  call to refresh your oauth tokens
  search   Search MAL for an anime/manga

extended user guide, source code, report issues, etc -
https://github.com/hrothwell/anime-cli

```

## Latest Windows native exe

- Latest windows exe build can be found [here](./native-image-items/build-results/anime.exe)
- I am trying to only ever merge completely functional/working native-image code, but no promises this will always work.
  More automated testing to come, maybe...
- Ideally get versioning setup as well