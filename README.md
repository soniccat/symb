XcodeBuilder
============

A tool to store all your builds to be able to symbolicate your crashlog through all of builds.

Features:

1) Build and store all your builds. I build a project and upload the result by a console script. It's very useful but I overwrite bulds every time. Now I can just write:
XcodeBuilder -build "xcodebuild -workspace /Users/username/foldername/superapp.xcworkspace -scheme TargetName -configuration Debug build"
and app and dSYM files will be saved in separate directory.

2) Symbolcations. Xcode doesn't symbolicate well in my case. When I have a crashlog I run:
XcodeBuilder -symbolicate -c "./crash.crash" -o "./symbolicated" -arch armv7
and I will have symbolicated crashlogs for every stored version.

3) Ftp upload and synchronization. These featureas are in progress.
