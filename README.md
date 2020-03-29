# photo duplicate detector
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
 - group by filesize - those are duplicate candidates (dupicates are files with same content - hence **filesize must be identical**)
 - for all files in the same size group compare contents
 
###Points to consider:

#### very big input (bigger than available memory)
 The uncompressed files can be split into partitions, for each partition we output result of grouping files by size, sorted by size. 
 Those intermediate results are then split into groups containing size groups up to a limit (eg: up to 1MB, 5MB, etc - depending of input size).
 The groups in each threshold are merged into single file, sorted and can then be procesed.    
 
#### three way merge
This approach covers this scenario (comparing equally sized files' contents)
 
#### some files had their names changed
This approach does not take filenames into account
 
#### some files may have extension changed
This approach does not take filenames into account

## Installation

Download from https://github.com/mpisanko/photos

## Usage

This can be run as an executable java jar to which you pass photo zip archive file path. It will output filepaths of duplicate files to STDOUT (aka console ;) 

Run the project's tests:

    $ clojure -A:test:runner

Build an uberjar:

    $ clojure -A:uberjar

Run that uberjar:

    $ java -jar photos.jar [filepath]

## Options

The only option accepted is path to the photo archive zip.
