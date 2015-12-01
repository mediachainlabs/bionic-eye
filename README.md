# bionic-eye

Right now, a pretty basic translation of the [OpenCV Java tutorial][opencv-java-tutorial]
into clojure, updated to use the OpenCV 3.0 APIs.  

## Setup

Getting OpenCV and clojure to play nice is mostly based on the approach from 
[this tutorial][opencv-clojure-tutorial], although I installed OpenCV using homebrew
instead of compiling from scratch.

### Install OpenCV
This will probably take a while to compile...

```bash
% brew tap homebrew/science
% brew install opencv3 --with-java --with-tbb --with-contrib
```

### Install the OpenCV libs as local Maven artifacts
We want to be able to refer to OpenCV as a dependency in our `project.clj`, so we
need to install the OpenCV java API and the native library into our local Maven repo.

First, install the [lein-localrepo plugin][localrepo] by adding it to your `~/.lein/profiles.clj`:

```clojure
{:user {:plugins [[lein-localrepo "0.5.3"]]}
```

Then we need to make a JAR for the OpenCV native lib, and install it and the main OpenCV
jar into our local repo.
```bash
% mkdir ~/scratchdir
% cd ~/scratchdir

# Wrap the native lib in a JAR
% mkdir -p native/macosx/x86_64
% cp /usr/local/opt/opencv3/share/OpenCV/java/libopencv_java300.dylib ./native/macosx/x86_64
% jar -cMf opencv-native-300.jar native

# Install using localrepo lein plugin
% cp /usr/local/opt/opencv3/share/OpenCV/java/opencv-300.jar .
% lein localrepo install opencv-300.jar opencv/opencv 3.0.0
% lein localrepo install opencv-native-300.jar opencv/opencv-native 3.0.0

# can get rid of our working dir now
% cd
% rm -rf scratchdir
```

Now we can refer to `opencv/opencv` and `opencv/opencv-native` in our `project.clj` like
any other dependency.

## Usage
Fire up a repl in your favorite clojure environment.  I like [cursive][cursive-clojure], but
you can use whatever :) If you don't want to muck about setting up an editor, you can just use
`lein repl`.

```
% lein repl

user=> (load-file "src/bionic_eye/core.clj")
nil
user=> (in-ns 'bionic-eye.core)
#object[clojure.lang.Namespace 0x1e44e3e9 "bionic-eye.core"]
bionic-eye.core=> (detect-face lena "/tmp/lena-face.png")
true
```

That should dump a `lena-face.png` file in `/tmp` with the Lena's famous face outlined
with a green rectangle.

[opencv-java-tutorial]: http://docs.opencv.org/2.4/doc/tutorials/introduction/desktop_java/java_dev_intro.html
[opencv-clojure-tutorial]: http://docs.opencv.org/2.4/doc/tutorials/introduction/clojure_dev_intro/clojure_dev_intro.html