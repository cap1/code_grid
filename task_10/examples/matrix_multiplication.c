/*
 * Praktikum: Anwendung und Programmierung im Grid · SS 2012, 
 * Matrix Multiplication
 */

#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

int main( int argc, char *argv[] )
{
    int rank; /* Rank of process */
    int size; /* Number of processes */
    
    MPI_Init( 0, 0 );
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size); /* MPI_COMM_WORLD is collection of processes*/
    
    //Program here
    
    
    MPI_Finalize();
    return 0;
}
