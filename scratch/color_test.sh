#!/bin/bash
# True color test script
for r in {0..255..5}; do
    for g in {0..255..5}; do
        for b in {0..255..5}; do
            printf "\e[48;2;%d;%d;%dm " "$r" "$g" "$b"
        done
        printf "\e[0m\n"
    done
done
