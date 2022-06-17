#! /bin/bash

mkdir output
mkdir output/latency
mkdir output/throughput
mkdir output/visibility

java -cp peersim.jar peersim.Simulator $1
bin/bash
