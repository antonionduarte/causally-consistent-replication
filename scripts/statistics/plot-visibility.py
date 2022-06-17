import matplotlib.pyplot as plt

VISIBILITY_PATH = "output/visibility/"

NUMBER_NODES = 7

EXPERIMENT_TIME_SATURN = 30
EXPERIMENT_TIME_C3 = 30

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

""" 
Processes one specific experiment
""" 
def visibility(experiment):
    x = []
    y = []

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
            to_int.append(int(elem))

        lowest_val = lowest(to_int)
        highest_val = highest(to_int)

        if len(to_int) == NUMBER_NODES:
            x.append(lowest_val)
            y.append(highest_val - lowest_val)

    return x, y

def plot_graph(x, y, color):
    plt.title('Saturn vs C3')
    plt.xlabel('Time (s)')
    plt.ylabel('Message visibility time')
    
    plt.plot(x, y, color + 'o')

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

    x_visibility_c3, y_visibility_c3 = visibility("c3-50-clients.txt")
    x_visibility_sat, y_visibility_sat = visibility("saturn-50-clients.txt")

    plot_graph(x_visibility_c3, y_visibility_c3, 'r')
    plot_graph(x_visibility_sat, y_visibility_sat, 'b')

    plt.savefig('plot-visibility.png')
