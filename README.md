# Causally Consistent Replication
Repository for research and development for a simulator to test algorithms that assure causal consistency upon data replication.

# TODO:
- [ ] Message "Container" Class - contains things like the development the size of the data etc...
- [ ] Client simulation - nClients per node configurable, writes/reads percentage configurable.
- [ ] Metrics - Latecy per operation - perReplica and Global, Operations/s
- [ ] Transport - Latency Matrix / Throughput into consideration
- [ ] Protocol - Base Meta-protocol

# Running Peersim:
*Command:* ```java -cp target/peersim.jar peersim.Simulator config/config.txt```
