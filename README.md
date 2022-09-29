# anime-cli
simple tool to select a random anime from a user's MAL list

## Requirements
- [MAL API client id](https://myanimelist.net/apiconfig)
- said client id is placed in a text file located at `${HOME}/anime-cli/mal-secret.txt` 
- need java for now, want to make this run native at some point (stated 9-28-22, see how long it actually takes)

## "Installing" (not really installing yet)
- download git repo
- run `./gradlew clean build`
- scripts should now be present in `build/install/anime-cli/bin`

## Usage
- example if starting from project root: `build/install/anime-cli/bin/anime-cli -u user -l list_status`
  - there is also a `.bat` version available
- inputs
  - `-u --user-name` Defaults to mine for now but will probably add option to include it in the `mal-secret.txt`
  - `-l --list-status [completed|plan_to_watch|watching|on_hold|dropped]` list to select from
  - `-h --help` show the help info
