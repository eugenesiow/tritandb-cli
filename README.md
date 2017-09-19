TritanDB Command Line Client
======

**TritanDb** is a time-series database for Internet of Things Analytics with a rich graph data model and fast lightweight core.
It can be used to ingest, store and query time-series data in real-time.
TRITAnDb stands for Time-series Rapid Internet of Things Analytics Database.

Usage
-------

```
usage: tritandb-cli [-h] --ingest [-H HOST] [-P PORT]

required arguments:
  --ingest,     Command/Mode of Operation
  --query,
  --list


optional arguments:
  -h, --help    show this help message and exit

  -H HOST,      Server Host e.g. localhost or 192.168.0.100
  --host HOST

  -P PORT,      Server Port e.g. 5700
  --port PORT
```


Installation
------------

You can build TritanDb's Command Line Client using Gradle

    git clone https://github.com/eugenesiow/tritandb-cli.git
    cd tritandb-cli
    ./gradlew build

Documentation
-------------
* [Docs](https://eugenesiow.gitbooks.io/tritandb)

Projects
-------
* [TritanDB Main Repo](https://github.com/eugenesiow/tritandb-kt)
* [Experimental Repo](https://github.com/eugenesiow/tritandb-experimental)
