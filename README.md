symb
============

The tool to symbolicate a crashlog using stored xarchives. 

Usage:

    java -jar ./symb.jar cr.crash -o ./symbolicated

Symbolicated crashlog will be stored in "symbolicated" folder for every xcarhive which have the same UUID with the input crashlog (https://developer.apple.com/library/ios/qa/qa1765/_index.html). The tool performs symbolicaiton according the rules: http://stackoverflow.com/questions/13574933/ios-crash-reports-atos-not-working-as-expected/13576028#13576028. By defaul ~/Library/Developer/Xcode/Archives/ path is used as the source of xcarhive files.

Last buld: https://www.dropbox.com/s/uzx6nqollfyo930/symb.jar?dl=0

Options:

-arch <architecture>  
Force to set arch parameter of atos. When it is skipped, it is got from a crash log.

-s <path>  
Path to xarchives folder. The default value is ~/Library/Developer/Xcode/Archives/.

-atos <path>  
Path for atos command. The default value is /Applications/Xcode.app/Contents/Developer/usr/bin/atos.

-d  
Log all command outputs.

