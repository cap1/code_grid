#include <stdio.h>
#include <mpi.h>
#include <unistd.h>
int main(int argc, char *argv[]) 
{
	int myrank;
	int nprocs;
	int ranksum;
	int length;
	int sendto;
	int i;
	char name[80];	

	MPI_Status status;
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);  
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	MPI_Get_processor_name(name,&length);
  	
	// Solve the assignment here

	for (i=0; i<nprocs; i++)	
	{
		printf("====NEXT ROUND: %d started====",i);
		if(myrank == i)
		{
			MPI_Send(&myrank,1,MPI_INT,myrank+1,myrank,MPI_COMM_WORLD);
			//printf("Process %d sent message to process %d\n",myrank,myrank+1);
			MPI_Recv(&ranksum,1,MPI_INT,nprocs-1,nprocs-1,MPI_COMM_WORLD,&status);
			//Sleep to keep space-time continuum intact ;)
			sleep(1);
			printf("Process %d received message from process %d\n",myrank,nprocs-1);
			printf("---Process sum is %d\n",ranksum);
		}
		else
		{
			MPI_Recv(&ranksum,1,MPI_INT,myrank-1,myrank-1,MPI_COMM_WORLD,&status);
			printf("Process %d received message from process %d\n",myrank,myrank-1);
			ranksum += myrank;
			if(myrank == nprocs-1)
			{
				sendto = 0;
			}
			else
			{
				sendto = myrank + 1;
			}
			MPI_Send(&ranksum,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
			//printf("Process %d sent message to process %d\n",myrank,sendto);
			printf("---Process sum is %d\n",ranksum);
		}
	}	


	MPI_Finalize();
	return 0;
}
