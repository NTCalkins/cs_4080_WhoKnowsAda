#!/bin/zsh
# Run the text editor using the test commands 100 times
# Feed the name of the executable as an argument
for ((i = 0; i < 10000; ++i)); do { $1 < TimeTest.txt }; done
