# duplicate photo detector
### Task 
A client merged their home photo folder with their partner's folder, with disastrous consequences. They now have a lot of duplicate photos in different places in the folder structure.

They've attached a .zip of this photos folder to this email.

Write a command-line program in your chosen language which finds files which have exactly the same contents and outputs any duplicates (and their locations) to standard output.
                       
### Assumptions
 - files are identical if they have same size and same content (it's possible to have same photo with or without metadata - which will affect file size but that's outside of the scope of this exercise)
 - archive to be processed is a zip file

### Approach
Need to keep in mind that the file might be very big.

My initial thoughts are:
 - get a flat list of filepaths with filesizes (that might be a map)
 - group by filesize - those are duplicate candidates (duplicates are files with same content - hence **filesize must be identical**)
 - for all files in the same size group compare contents
 - stick to the clojure philisophy of operating on raw data
 - also stick to UNIX philosophy of small tools doing one job well with consistent interface (so they can be composed in different ways)
 - main function is the orchestrator and communicates with modules using a map containing keys: 
    - `error` (which is populated in case of error with `message` and `code`)
    - `result` successful result of function call
 - what do I do with that __MACOSX trash directory? manual exclusion seems a bit brittle..
 
After writing:
 - the approached worked well 
 - decided to keep the __MACOSX in report, this can be dealt with by piping output through `grep -v '__MACOSX'`
 - could add another commandline option to exclude certain paths 
 
### Points to consider:

##### very big input (bigger than available memory)
 The uncompressed files can be split into partitions, for each partition we output result of grouping files by size, sorted by size. 
 Those intermediate results are then split into groups containing size groups up to a limit (eg: up to 1MB, 5MB, etc - depending of input size).
 The groups in each threshold are merged into single file, sorted and can then be procesed.
 
##### three way merge
This approach covers this scenario (comparing equally sized files' contents)
 
##### some files had their names changed
This approach does not take filenames into account
 
##### some files may have extension changed
This approach does not take filenames into account

## Installation

Download from https://github.com/mpisanko/photos.
This project is written in `clojure` and requires Java 8+ be installed. To build install [clojure](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)

## Usage

This can be run as an executable java jar to which you pass photo zip archive file path. 
It will print report about fduplicate files to STDOUT (aka console ;) 

###### Commands below assume you're in root directory of the project
Run the project's tests:

    $ clojure -A:test:runner

Build an uberjar:

    $ clojure -A:uberjar

Run that uberjar:

    $ java -jar photos.jar [filepath-to-duplicate-archive]