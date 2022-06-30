import os
import multiprocessing

NUM_CPUS = None
COMPILE_COMMAND = 'mvn clean compile assembly:single'
RUN_COMMAND = 'java -cp target/peersim.jar peersim.Simulator {}'
TIME = 100000

def run_command(cmd):
    os.system(RUN_COMMAND.format(cmd))

def run_commands(commands, n_parallel):
    worker = multiprocessing.Pool(n_parallel)
    worker.map(run_command, commands)

if __name__ == "__main__":
    os.system(COMPILE_COMMAND)
    
    commands = [
        'config/saturn/sat-3-clients.txt',
        'config/saturn/sat-5-clients.txt',
        'config/saturn/sat-10-clients.txt',
        'config/saturn/sat-15-clients.txt',
        'config/saturn/sat-20-clients.txt',
        'config/saturn/sat-25-clients.txt',
        'config/saturn/sat-30-clients.txt',
        'config/saturn/sat-35-clients.txt',
        'config/saturn/sat-50-clients.txt',
        'config/saturn/sat-55-clients.txt',
        'config/saturn/sat-60-clients.txt',
        'config/saturn/sat-65-clients.txt',
        'config/saturn/sat-80-clients.txt',
        'config/saturn/sat-100-clients.txt',
    
        'config/c3/c3-3-clients.txt',
        'config/c3/c3-5-clients.txt',
        'config/c3/c3-10-clients.txt',
        'config/c3/c3-15-clients.txt',
        'config/c3/c3-20-clients.txt',
        'config/c3/c3-25-clients.txt',
        'config/c3/c3-30-clients.txt',
        'config/c3/c3-35-clients.txt',
        'config/c3/c3-50-clients.txt',
        'config/c3/c3-55-clients.txt',
        'config/c3/c3-60-clients.txt',
        'config/c3/c3-65-clients.txt',
        'config/c3/c3-80-clients.txt',
        'config/c3/c3-100-clients.txt',
    ]

    if TIME != None:
        for command in commands:
            file = open(command, 'r')
            lines = file.readlines()
            lines[0] = 'simulation.endtime \t \t ' + str(TIME) + '\n'
            file.close()
            file = open(command, 'w')
            for line in lines:
                file.write(line)
    
    run_commands(commands, n_parallel=10)      

    print('\n----------------------------')
    print('Finished Running Experiments')
    print('----------------------------\n')
