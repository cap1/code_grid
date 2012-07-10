#include <stdio.h>
#include <mpi.h>
int main(int argc, char *argv[]) {
  MPI_Status recv_status;
  MPI_Init(&argc, &argv);
  MPI_Comm_rank(MPI_COMM_WORLD, &myrank);  
  MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
  MPI_Get_processor_name(name,&length);
  
  // Solve the assignment here
  
  MPI_Finalize();
}
