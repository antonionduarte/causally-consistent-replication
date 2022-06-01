# Causally Consistent Replication
Simulator to build and test causal consistency algorithms.

# TODO - Simulator:
- [ ] Client simulation
  - [X] Configuration - Reading values from config file
  - [X] Operations being sent in a closed loop
  - [X] Distinction between read/write operations
  - [ ] Access specific keys to prepare for partial replication
- [ ] Configuration - Create an easy to read sample config file 
- [ ] Metrics
  - [X] Latency
  - [X] Operations/s
  - [ ] Visibility Times
- [X] Heap / Scheduler change - A node can only receive one operation on a specific timestamp. 
- [ ] Transport - Latency Matrix / Throughput into consideration
  - [ ] Read and process latency matrices to a format acceptable for Peersim
  - [ ] Generate a tree graph based on it
- [ ] Protocol - Base Meta-protocol
    - [X] Internal event queue for non processed events
    - [X] Changes to account for difference between reads/writes
    - [X] Base protocolMessage container class getSize() for Transport etc...
- [X] Operation propagation through the system

# TODO - Protocols
- [ ] C3
- [ ] Saturn

# Running the Simulator:
*Command:* ```java -cp target/peersim.jar peersim.Simulator config/config.txt```

# Latency Matrix

- Africa Cape Town        (0)
- US West (N. Cal)        (19)
- Asia Pacific Sydney     (7) 
- Canada                  (8)
- EU Paris                (14)
- EU London               (13)
- São Paulo               (16)
