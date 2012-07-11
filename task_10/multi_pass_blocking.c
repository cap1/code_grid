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
	char name[80];	

	MPI_Status status;
	MPI_Init(&argc, &argv);
	MPI_Comm_rank(MPI_COMM_WORLD, &myrank);  
	MPI_Comm_size(MPI_COMM_WORLD, &nprocs);
	MPI_Get_processor_name(name,&length);
  	
	// Solve the assignment here

	
	
	while(recvrank != myrank || round != nprocs-1)
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
			printf("STARTING ROUND NR. #%d\n",round);
			MPI_Send(&myrank,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
			//printf("Process %d sent message to process %d\n",myrank,myrank+1);
			MPI_Recv(&recvrank,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
			printf("Process %d received rank %d from process %d\n",myrank,recvrank,recvfrom);
			printf("ROUND NR. #%d FINISHED\n",round);
			continue;
		}
		round++;	
		MPI_Recv(&recvrank,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
		printf("Process %d received rank %d from process %d\n",myrank,recvrank,recvfrom);
		MPI_Send(&recvrank,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
		//printf("Process %d sent message to process %d\n",myrank,sendto);
		ranksum += recvrank;
		//printf("---Process sum is %d\n",ranksum);
	}


	MPI_Finalize();
	return 0;
}
