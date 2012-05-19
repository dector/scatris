Scatris
=======

![Scatris Logo](https://lh3.googleusercontent.com/-jNyZOdrXqns/T7X9ATxY0YI/AAAAAAAADBk/ga3obTdS8W4/s317/scatris_on_black.png "Scatris")

Writed on Scala with using lwjgl and slick2d.


Controls
--------

__Left/Right__ - move falling element
__Up__ - rotate falling element ccw
__Down__ - speed-up falling element
__Space__ - drop falling element down
__F2__ - Show/hide 'phantom' (place, where element will fall)
__P__ - pause
__Esc__ - exit


Screenshots
----------

![Scatris screenshot](https://lh6.googleusercontent.com/-yVnkRjsIUNg/T7V89VQXS3I/AAAAAAAADBE/9GB17Bfwn6I/s640/scatris_screen.png "Scatris")

![Scatris screenshot with phantom element](https://lh4.googleusercontent.com/--BeEhg0VZN8/T7gWBkUJpLI/AAAAAAAADCE/3xhr0B5FWqk/s640/scatris_phantom.png "Phantom")


Download
--------

[Dropbox.com (full package) [10.8 MB]](http://goo.gl/h466Q "Download Scatris")


Running
-------

__Linux__:

    $ sh run_linux.sh

__Windows__:

Start file (with doubleclick or from cmd) run_windows.bat

__Other platforms__:

    java -cp lib/*::scatris.jar -Djava.library.path=lib/native/os_name ua.org.dector.scatris.Scatris

_Note_: Change  `::` for delimiter in your OS.
