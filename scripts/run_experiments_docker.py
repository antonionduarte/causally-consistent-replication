import os
import multiprocessing

NUM_CPUS = None
COMPILE_COMMAND = 'mvn clean compile assembly:single'

DOCKER_BUILD = 'docker build -t causality-sim .'    
DOCKER_COPY = 'docker cp {}:output {}'
DOCKER_RM_CONTAINER = 'docker rm {}'

RUN_COMMAND = 'docker run --name {} causality-sim {}'
TIME = 500

def get_container_name(cmd):
    splitted = cmd.split('/')
    name = (splitted[2].split('.'))[0]
    return name

def run_command(cmd):
    os.system(RUN_COMMAND.format(get_container_name(cmd), cmd))

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
        'config/saturn/config-55-clients.txt',
        'config/saturn/config-60-clients.txt',
        'config/saturn/config-65-clients.txt',
        'config/saturn/config-80-clients.txt',
        'config/saturn/config-100-clients.txt',

        # 'config/c3/c3-3-clients.txt',
        # 'config/c3/c3-5-clients.txt',
        # 'config/c3/c3-10-clients.txt',
        # 'config/c3/c3-15-clients.txt',
        # 'config/c3/c3-20-clients.txt',
        # 'config/c3/c3-25-clients.txt',
        # 'config/c3/c3-30-clients.txt',
        # 'config/c3/c3-35-clients.txt',
        # 'config/c3/c3-50-clients.txt',
        # 'config/c3/c3-55-clients.txt',
        # 'config/c3/c3-60-clients.txt',
        # 'config/c3/c3-65-clients.txt',
        # 'config/c3/c3-80-clients.txt',
        # 'config/c3/c3-100-clients.txt',
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

    os.system(DOCKER_BUILD)
    
    run_commands(commands, n_parallel=3)      

    print('\n----------------------------')
    print('Finished Running Experiments')
    print('----------------------------\n')

    for command in commands:
        os.system(DOCKER_COPY.format(get_container_name(command), os.getcwd()))
        os.system(DOCKER_RM_CONTAINER.format(get_container_name(command)))
