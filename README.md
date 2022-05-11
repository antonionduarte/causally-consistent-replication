# Causally Consistent Replication
Simulator to build and test causal consistency algorithms.

# TODO - Simulator:
- [ ] Client simulation
  - [X] Configuration - Reading values from config file
  - [ ] Operations being sent in a closed loop
  - [ ] Distinction between read/write operations
- [ ] Configuration - Create an easy to read sample config file 
- [ ] Metrics
  - [ ] Latency
  - [ ] Operations/s
- [X] Heap / Scheduler change - A node can only receive one operation on a specific timestamp. 
- [ ] Transport - Latency Matrix / Throughput into consideration
- [ ] Protocol - Base Meta-protocol
    - [X] Internal event queue for non processed events
    - [ ] Changes to account for difference between reads/writes
    - [X] Base message container class getSize() for Transport etc...
- [ ] Operation propagation through the system

# TODO - Protocols
- [ ] C3
- [ ] Saturn

# Running the Simulator:
*Command:* ```java -cp target/peersim.jar peersim.Simulator config/config.txt```
