echo off
set root=%~dp0
call %root%gradlew.bat clean build
echo making config directory
mkdir %root%build\libs\META-INF\native-image
set configPath=%root%build\libs\META-INF\native-image
echo running agentlib
call java -agentlib:native-image-agent=config-output-dir=%configPath% -jar %root%build\libs\anime-cli-1.0-SNAPSHOT-all.jar
echo config files located here "%configPath%"
set exeBuildResultPath=%root%native-image-result
mkdir %exeBuildResultPath%
cd %exeBuildResultPath%
echo building native image
call native-image --no-fallback --report-unsupported-elements-at-runtime -H:ConfigurationFileDirectories=%configPath% -jar %root%build\libs\anime-cli-1.0-SNAPSHOT-all.jar anime --enable-url-protocols=https --no-server
echo running exe to test! anime.exe is found at "%exeBuildResultPath%"
call %exeBuildResultPath%\anime
cd %root%