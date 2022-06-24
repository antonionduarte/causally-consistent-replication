import matplotlib.pyplot as plt
import itertools

VISIBILITY_PATH = "output/visibility/"

NUMBER_NODES = 7

EXPERIMENT_TIME_SATURN = 30
EXPERIMENT_TIME_C3 = 30

TIME_INTERVAL = 5

""" 
Returns the lowest value of a list
"""
def lowest(lst):
    lowest = float('inf')
    for elem in lst:
        if elem < lowest: lowest = elem
    return lowest

""" 
Returns the highest value of a list
""" 
def highest(lst):
    highest = -1 
    for elem in lst:
        if elem > highest: highest = elem
    return highest


# operações podem executar quando se verifica que já não podemos 

""" 
Processes one specific experiment
""" 
def visibility(experiment):
    x = []
    y = []

    x_total = []
    y_total = []

    experiment_path = VISIBILITY_PATH + experiment
    experiment_file = open(experiment_path)

    file = open(experiment_path)
    lines = file.readlines()
    file.close()
    
    for line in lines: 
        splitted = line.strip().split(',')
        splitted.pop()
        to_int = []

        for elem in splitted:
            to_int.append(int(int(elem) / 10))

        lowest_val = lowest(to_int)
        highest_val = highest(to_int)

        if len(to_int) == NUMBER_NODES:
            x_total.append(lowest_val)
            y_total.append(highest_val - lowest_val)

    max_time = highest(x_total)
    curr_timestamp = 0
    while curr_timestamp + TIME_INTERVAL <= max_time:
        i = 0
        interval_x = []
        interval_y = []
        while i < len(x_total):
            if x_total[i] >= curr_timestamp and x_total[i] < curr_timestamp + TIME_INTERVAL:
                interval_x.append(x_total[i])
                interval_y.append(y_total[i])

            i = i + 1

        sum_x = 0
        sum_y = 0
        for (x_val, y_val) in zip(interval_x, interval_y):
            sum_x = sum_x + x_val
            sum_y = sum_y + y_val 

        final_x = sum_x / len(interval_x)
        final_y = sum_y / len(interval_y)

        x.append(final_x)
        y.append(final_y)

        curr_timestamp = curr_timestamp + TIME_INTERVAL

    return x, y

def plot_graph(x, y, color, str_label):
    plt.title('Saturn vs C3')
    plt.xlabel('Time (ms)')
    plt.ylabel('Message visibility time (ms)')
    
    line = plt.plot(x, y, color + 'o-', label=str_label)

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
    ]

    input_c3 = [
        "c3-3-clients.txt",
        "c3-5-clients.txt",
        "c3-10-clients.txt",
        "c3-15-clients.txt",
        "c3-20-clients.txt",
        "c3-25-clients.txt",
        "c3-30-clients.txt",
        "c3-35-clients.txt",
        "c3-50-clients.txt",
        "c3-55-clients.txt",
        "c3-60-clients.txt",
        "c3-65-clients.txt",
        "c3-80-clients.txt",
    ]

    x_visibility_c3, y_visibility_c3 = visibility("c3-35-clients.txt")
    x_visibility_sat, y_visibility_sat = visibility("saturn-35-clients.txt")

    # print(x_visibility_c3)
    # print(y_visibility_c3)
    # print(x_visibility_c3)
    # print(y_visibility_c3)

    plot_graph(x_visibility_c3, y_visibility_c3, 'r', 'C3')
    plot_graph(x_visibility_sat, y_visibility_sat, 'b', 'Saturn')
    
    plt.legend()

    plt.savefig('plot-visibility.pdf')
