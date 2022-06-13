import os
import multiprocessing

NUM_CPUS = None
COMPILE_COMMAND = 'mvn clean compile assembly:single'
RUN_COMMAND = 'java -cp target/peersim.jar peersim.Simulator {}'
TIME = 20000

def run_command(cmd):
    os.system(RUN_COMMAND.format(cmd))

def run_commands(commands, n_parallel):
    worker = multiprocessing.Pool(n_parallel)
    worker.map(run_command, commands)

if __name__ == "__main__":
    os.system(COMPILE_COMMAND)

    commands = [
        'config/saturn/config-3-clients.txt',
        'config/saturn/config-5-clients.txt',
        'config/saturn/config-10-clients.txt',
        'config/saturn/config-15-clients.txt',
        'config/saturn/config-20-clients.txt',
        'config/saturn/config-25-clients.txt',
        'config/saturn/config-30-clients.txt',
        'config/saturn/config-35-clients.txt',
        'config/saturn/config-50-clients.txt',
    ]

    if time != None:
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
