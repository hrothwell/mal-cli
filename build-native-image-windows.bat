@echo off
set root=%~dp0
call %root%gradlew.bat clean build
echo making config directory
mkdir %root%build\libs\META-INF\native-image
set configPath=%root%native-image-items\config
@REM remove the config folder if it already existed as the merge-dir stuff can leave some unwanted remnants from prior builds
echo removing config directory if it exists already
rmdir /s %configPath%
echo running agentlib
@REM no args for base command, then add all subcommands also. potentially will need to run with any arguments also?
@REM login process will be kind of annoying/tedious to hit
@REM TODO add more scenarios or think of a better way to go through program flow. Error scenarios might be hit or miss
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar -h
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar login
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar refresh
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar suggest
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar suggest -l 101
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar random
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar -m random
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar --quiet random
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar --quiet -m random
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar random --include-not-yet-released
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar -m random --include-not-yet-released
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar random -u hone_the_rat -l completed
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar -m random -u hone_the_rat -l completed
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar random -u unknown_user_113355_idk_error -l completed
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar -m random -u unknown_user_113355_idk_error -l completed
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar search -q "Tokyo Ghoul" -l 2
call java -agentlib:native-image-agent=config-merge-dir=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar -m search -q "Tokyo Ghoul" -l 2
echo config files located here "%configPath%"
set exeBuildResultPath=%root%native-image-items\build-results
mkdir %exeBuildResultPath%
cd %exeBuildResultPath%
echo building native image
@REM "-J--add-modules -JALL-SYSTEM" is part of a work around which should be fixed in later graalvm version: https://github.com/oracle/graal/issues/4671
call native-image --no-fallback -J--add-modules -JALL-SYSTEM --report-unsupported-elements-at-runtime -H:ConfigurationFileDirectories=%configPath% -jar %root%build\libs\mal-cli-1.0-SNAPSHOT-all.jar mal --enable-url-protocols=https,http
echo running exe to test! mal.exe is found at "%exeBuildResultPath%"
call %exeBuildResultPath%\mal
cd %root%