package scripts

//def cmd = new CommandShell()
//cmd.execute("dir ")
def res = "cmd /c dir ".execute().text

println res