#! /bin/bash

mkdir output
mkdir output/latency
mkdir output/throughput

java -cp peersim.jar peersim.Simulator $1 > test.txt
bin/bash
