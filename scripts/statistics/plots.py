import matplotlib.pyplot as plt

LATENCY_PATH = "output/latency/"
THROUGHPUT_PATH = "output/throughput/"
EXPERIMENT_TIME_SECONDS = 5

""" 
Processes the latencies and returns the medium latency for each 
experiment.
"""
def latency(inputs):
    total_latency = []
    for experiment in inputs:
        processed_lines = []
        num_results = 0
        experiment_latency = 0
        experiment_path = LATENCY_PATH + experiment
        experiment_file = open(experiment_path)

        for line in experiment_file.readlines():
            processed_lines.append(line.strip().split(','))

        experiment_file.close()

        for line in processed_lines:
            line.pop(0)
            num_results = num_results + len(line)
            for latency in line:
                experiment_latency = experiment_latency + int(latency)

        medium_latency = int(experiment_latency / num_results)

        total_latency.append(medium_latency)      

    return total_latency


""" 
Processes the throughputs and returns 
the medium throughput of each experiment.
"""
def throughput(inputs):
    total_throughput = []
    for experiment in inputs:
        processed_lines = []
        experiment_throughput = 0
        experiment_path = THROUGHPUT_PATH + experiment
        experiment_file = open(experiment_path)

        for line in experiment_file.readlines():
            processed_lines.append(line.strip())
        
        experiment_file.close()
        processed_lines.pop(0) # TODO: Probably delete from output

        for result in processed_lines:
            splitted = result.split(',')
            throughput = int(splitted[1])
            experiment_throughput = experiment_throughput + throughput
        
        total_throughput.append(int(experiment_throughput / EXPERIMENT_TIME_SECONDS))
    
    return total_throughput


def plot_graph(latencies, throughputs, color):
    plt.title('Saturn Experiments')
    plt.xlabel('Throughput (Op/s)')
    plt.ylabel('Perceived Latency (ms)')
    
    plt.plot(throughputs, latencies, color, linestyle="dashed")
    plt.plot(throughputs, latencies, color + '*')

if __name__ == "__main__":
    input_saturn = [
        "saturn-3-clients.txt",
        "saturn-5-clients.txt",
        "saturn-10-clients.txt",
        "saturn-15-clients.txt",
        "saturn-20-clients.txt",
        "saturn-25-clients.txt",
        "saturn-30-clients.txt",
        "saturn-35-clients.txt",
        "saturn-50-clients.txt",
        "saturn-55-clients.txt",
        "saturn-60-clients.txt",
        "saturn-65-clients.txt",
        "saturn-80-clients.txt",
        "saturn-100-clients.txt"
    ]

    input_c3 = [
        #"c3-10-clients.txt",
        #"c3-25-clients.txt",
        #"c3-50-clients.txt",
        #"c3-100-clients.txt",
        #"c3-500-clients.txt",
        #"c3-1000-clients.txt",
    ]

    throughputs_saturn = throughput(input_saturn)
    latencies_saturn = latency(input_saturn)

    #throughputs_c3 = throughput(input_c3)
    #latencies_c3 = latency(input_c3)

    plot_graph(latencies_saturn, throughputs_saturn, 'b')
    #plot_graph(latencies_c3, throughputs_c3, 'r')
    plt.show()
