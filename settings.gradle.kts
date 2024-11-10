rootProject.name = "one-liners"
include(":ai-client")
include(":console-color")
project(":ai-client").projectDir = File(settingsDir,".mod/ai-client")
project(":console-color").projectDir = File(settingsDir,".mod/console-color")