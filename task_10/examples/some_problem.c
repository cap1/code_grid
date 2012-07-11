/*
 * Praktikum: Anwendung und Programmierung im Grid · SS 2012, Some Problem.
 */

#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv) {
    int rank, size;
    int BIG = atoi(argv[1]);
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    
    int *i = (int *) malloc(sizeof(int) * BIG);
    // reserve BIG(10000) int pointers.
    i[0] = rank;
    i[BIG - 1] = size * (rank + 1);
    
    printf("Task %d is trying to send to task %d\n", rank, (rank + 1) % size);
    MPI_Send(i, BIG, MPI_INT, (rank + 1) % size, 1, MPI_COMM_WORLD);
    printf("Task %d sent values i[0]=%d and i[%d]=%d\n", rank, i[0], BIG-1, i[BIG - 1]);
    
    printf("Task %d is trying to receive from task %d\n", rank, (size + rank - 1) % size);
    MPI_Recv(i, BIG, MPI_INT, (size + rank - 1) % size, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    
    printf("Task %d received values i[0]=%d and i[%d]=%d from task %d\n", rank, i[0], BIG-1, i[BIG - 1], (size + rank - 1) % size);
    
    MPI_Finalize();
    return 0;
}
