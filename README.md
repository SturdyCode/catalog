# Catalog

[ ![Download](https://api.bintray.com/packages/mcumings/catalog/catalog/images/download.svg) ](https://bintray.com/mcumings/catalog/catalog/_latestVersion)

## Overview

Catalog is a gradle plugin for Android that records logs when running Espresso tests.
It will run automatically with `connected{Variant}AndroidTest` tasks and print the LogCat into `app/build/outputs/androidTest-results/`:

![](screenshot.png)

To include it in your project:
```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.sturdycode:catalog:1.0.0'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.sturdycode.catalog'
```

## Release

To release:
```
./gradlew clean build bintrayUpload -PbintrayUser=USERNAME -PbintrayKey=API_KEY -PdryRun=false
```

## History

Originally released by [songkick](https://github.com/SongKick/catalog), this library was
[abandoned](https://github.com/songkick/catalog/blob/master/CONTRIBUTING.md).  We thank
songkick for their open source contribution, making it possible to continue using their
useful plugin.

## License

```
The MIT License

Copyright © 2016 Songkick
Copyright © 2018 SturdyCode

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```