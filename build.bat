
rmdir /S /Q worker\target\plugins

mkdir worker\target\plugins

copy /Y elasticsearch.plugin\target\elasticsearch.plugin-1.0-SNAPSHOT.zip worker\target\plugins

copy /Y elasticsearch.plugin\src\test\resources\elasticsearch-plugin.properties worker\target\plugins

copy /Y worker\src\main\resources\worker.yaml worker\target