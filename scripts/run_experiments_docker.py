import os
import multiprocessing

NUM_CPUS = None
COMPILE_COMMAND = 'mvn clean compile assembly:single'

DOCKER_BUILD = 'docker build -t causality-sim .'    
DOCKER_COPY = 'docker cp {}:output {}'
DOCKER_RM_CONTAINER = 'docker rm {}'

RUN_COMMAND = 'docker run --name {} causality-sim {}'
TIME = 100000

def get_container_name(cmd):
    splitted = cmd.split('/')
    name = (splitted[2].split('.'))[0]
    return name

def run_command(cmd):
    print(RUN_COMMAND.format(get_container_name(cmd), cmd))
    os.system(RUN_COMMAND.format(get_container_name(cmd), cmd))

def run_commands(commands, n_parallel):
    worker = multiprocessing.Pool(n_parallel)
    worker.map(run_command, commands)

if __name__ == "__main__":
    # os.system(COMPILE_COMMAND)

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
        'config/saturn/sat-150-clients.txt',
        'config/saturn/sat-200-clients.txt',
        'config/saturn/sat-250-clients.txt',
        'config/saturn/sat-300-clients.txt',


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
        'config/c3/c3-150-clients.txt',
        'config/c3/c3-200-clients.txt',
        'config/c3/c3-250-clients.txt',
        'config/c3/c3-300-clients.txt',

        'config/c3sat/c3sat-3-clients.txt',
        'config/c3sat/c3sat-5-clients.txt',
        'config/c3sat/c3sat-10-clients.txt',
        'config/c3sat/c3sat-15-clients.txt',
        'config/c3sat/c3sat-20-clients.txt',
        'config/c3sat/c3sat-25-clients.txt',
        'config/c3sat/c3sat-30-clients.txt',
        'config/c3sat/c3sat-35-clients.txt',
        'config/c3sat/c3sat-50-clients.txt',
        'config/c3sat/c3sat-55-clients.txt',
        'config/c3sat/c3sat-60-clients.txt',
        'config/c3sat/c3sat-65-clients.txt',
        'config/c3sat/c3sat-80-clients.txt',
        'config/c3sat/c3sat-100-clients.txt',
        'config/c3sat/c3sat-150-clients.txt',
        'config/c3sat/c3sat-200-clients.txt',
        'config/c3sat/c3sat-250-clients.txt',
        'config/c3sat/c3sat-300-clients.txt',

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
            file.close()

    os.system(DOCKER_BUILD)
    
    run_commands(commands, n_parallel=30)      

    print('\n----------------------------')
    print('Finished Running Experiments')
    print('----------------------------\n')

    for command in commands:
        os.system(DOCKER_COPY.format(get_container_name(command), os.getcwd()))
        os.system(DOCKER_RM_CONTAINER.format(get_container_name(command)))
