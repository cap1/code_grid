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

	//printf("====NEXT ROUND: %d started====\n",i);
	
	if(myrank == 0)
	{
		MPI_Send(&myrank,1,MPI_INT,myrank+1,myrank,MPI_COMM_WORLD);
		//printf("Process %d sent message to process %d\n",myrank,myrank+1);
		//MPI_Recv(&ranksum,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
		//Sleep to keep space-time continuum intact ;)
		sleep(1);
		printf("Process %d received message from process %d\n",myrank,nprocs-1);
		printf("---Process sum is %d\n",ranksum);
	}
	
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
	
	MPI_Recv(&recvrank,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
	MPI_Send(&recvrank,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
	round++;
	while(recvrank != myrank)
	{
		printf("ROUND NR. #%d\n",round);
		round++;	
		MPI_Recv(&recvrank,1,MPI_INT,recvfrom,recvfrom,MPI_COMM_WORLD,&status);
		printf("Process %d received rank %d from process %d\n",myrank,recvrank,recvfrom);
		MPI_Send(&recvrank,1,MPI_INT,sendto,myrank,MPI_COMM_WORLD);
		//printf("Process %d sent message to process %d\n",myrank,sendto);
		ranksum += recvrank;
	
	}
	printf("---Process sum is %d\n",ranksum);


	MPI_Finalize();
	return 0;
}
