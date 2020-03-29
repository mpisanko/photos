# Cogent take home test

A client merged their home photo folder with their partner's folder, with disastrous consequences. They now have a lot of duplicate photos in different places in the folder structure.

They've attached a .zip of this photos folder to this email.

Write a command-line program in your chosen language which finds files which have exactly the same contents and outputs any duplicates (and their locations) to standard output.

 
## Requirements:

* In your implementation, only use the standard libraries of your chosen language. No shelling out, or using external services or libraries.

* You are free to use external libraries for the purposes of testing.

* The code should be readable, reusable, and easy to modify. We're looking for production-quality code.


## Consider the the following points in formulating your solution:

* What if this same solution was used on a really large set of photos? What if it was a thousand photos? Or tens of thousands?

* What if this was a three-way merge, with triplicates? Does your solution account for this?

* Some of these files may have had their filename changed.

* Some of these may have only their extension changed.

 
Your code should be of production quality, and demonstrate the idioms of your chosen language.

 
Please submit a zipped git repository, with a readme.
