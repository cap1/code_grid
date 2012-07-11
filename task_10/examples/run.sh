#!/bin/sh
#PBS -N mpihello
#PBS -l nodes=17
/usr/bin/mpiexec /var/local/torque/pbsuser9/code_grid/task_10/examples/a.out

