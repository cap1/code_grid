#include <stdio.h>
#include <mpi.h>
#include <unistd.h>
int main(int argc, char *argv[]) 
{
	int myrank;
	int nprocs;
	int recvrank;
	int length;
	int sendto;
	int recvfrom;
	int ranksum=0;
	int round=0;
	const int verbose = 1;
	char name[80];	

	MPI_Status status;
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);  
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	MPI_Get_processor_name(name,&length);
  	
	// Solve the assignment here

	while( round != nprocs)
	{
		if(myrank == nprocs-1)
		{
			sendto = 0;
		}
		else
		{
			sendto = myrank + 1;
		}
		
		if(myrank == 0)
		{
			recvfrom = nprocs-1;
		}
		else
		{
			recvfrom = myrank-1;
		}

		if(myrank == round)
		{
			if(verbose) printf("STARTING ROUND NR. #%d\n",round);
			MPI_Send(&myrank,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
			if(verbose) printf("Process %d sent message to process %d\n",myrank,sendto);
			MPI_Recv(&recvrank,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
			if(verbose) printf("Process %d received rank %d from process %d\n",myrank,recvrank,recvfrom);
			if(verbose) printf("ROUND NR. #%d FINISHED\n",round);
		}
		else
		{
			MPI_Recv(&recvrank,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
			if(verbose) printf("Process %d received rank %d from process %d\n",myrank,recvrank,recvfrom);
			MPI_Send(&recvrank,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
			if(verbose) printf("Process %d sent message to process %d\n",myrank,sendto);

		}
		round++;	
		ranksum += recvrank;
	}
	printf("Process %d sum = %d\n",myrank,ranksum);
	

	MPI_Finalize();
	return 0;
}
