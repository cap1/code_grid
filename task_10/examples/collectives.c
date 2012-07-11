#include <mpi.h>
#include <math.h>
#include <stdio.h>
void main(argc,argv)
int argc;
char *argv[];
{
/***********************************************************************
 *                                                                     *
 * This is an MPI example on collective communication routines         *
 *                                                                     *
 ***********************************************************************/
      int p, i, Iam, root;
      char x[4], y[4], a, alphabet;

/* Starts MPI processes ... */

   MPI_Init(&argc,&argv);                 /* starts MPI */
   MPI_Comm_rank(MPI_COMM_WORLD, &Iam);   /* get current process id */
   MPI_Comm_size(MPI_COMM_WORLD, &p);     /* get number of processes */

root = 1;
if (Iam == 0) {
  printf("\n");
  printf("* This program demonstrates collective\n");
  printf("  MPI functions\n");
  printf("* Up to four processors are to be used for the demo\n");
  printf("\n\n");
  printf("  Function  Proc  Sendbuf              Recvbuf\n");
  printf("  --------  ----  -------              -------\n");
}

  MPI_Barrier(MPI_COMM_WORLD);

  for (i=0; i<p; i++) {
    x[i] = ' ';
  }
  alphabet = 'a';
/* Performs a gather operation */
  x[0] = alphabet+Iam;
  for (i=0; i<p; i++) {
        y[i] = ' ';
  }
  MPI_Gather(x,1,MPI_CHAR,          /* send buf,count,type */
             y,1,MPI_CHAR,          /* recv buf,count,type */
             root,                  /* root (data origin)  */
             MPI_COMM_WORLD);       /* comm,flag           */

  printf(" MPI_Gather    :  %d ", Iam);
  for (i=0; i<p; i++) {
    printf("  %c",x[i]);
  }
  printf("     ");
  for (i=0; i<p; i++) {
    printf("  %c",y[i]);
  }
  printf("\n");

  MPI_Barrier(MPI_COMM_WORLD);

/* Performs an all-gather operation */
  x[0] = alphabet+Iam;
  for (i=0; i<p; i++) {
    y[i] = ' ';
  }
  MPI_Allgather(x,1,MPI_CHAR,     /* send buf,count,type */
                y,1,MPI_CHAR,     /* recv buf,count,type */
                MPI_COMM_WORLD);  /* comm,flag           */

  printf(" MPI_Allgather :  %d ", Iam);
  for (i=0; i<p; i++) {
    printf("  %c",x[i]);
  }
  printf("     ");
  for (i=0; i<p; i++) {
    printf("  %c",y[i]);
  }
  printf("\n");

  MPI_Barrier(MPI_COMM_WORLD);

/* Perform a scatter operation */
  for (i=0; i<p; i++) {
    x[i] = alphabet+i+Iam*p;
    y[i] = ' ';
  }
  MPI_Scatter(x,1,MPI_CHAR,      /* send buf,count,type */
              y,1,MPI_CHAR,      /* recv buf,count,type */
              root,              /*    ! data origin    */
              MPI_COMM_WORLD);   /* comm,flag           */

  printf(" MPI_Scatter   :  %d ", Iam);
  for (i=0; i<p; i++) {
    printf("  %c",x[i]);
  }
  printf("     ");
  for (i=0; i<p; i++) {
    printf("  %c",y[i]);
  }
  printf("\n");

  MPI_Barrier(MPI_COMM_WORLD);

/* Perform an all-to-all operation */

  for (i=0; i<p;i++) {
    x[i] = alphabet+i+Iam*p;
    y[i] = ' ';
  }

  MPI_Alltoall(x,1,MPI_CHAR,       /* send buf,count,type */
               y,1,MPI_CHAR,       /* recv buf,count,type */
               MPI_COMM_WORLD);    /* comm,flag           */

  printf(" MPI_Alltoall  :  %d ", Iam);
  for (i=0; i<p; i++) {
    printf("  %c",x[i]);
  }
  printf("     ");
  for (i=0; i<p; i++) {
    printf("  %c",y[i]);
  }
  printf("\n");

  MPI_Barrier(MPI_COMM_WORLD);

/* Performs a broadcast operation */
  a = ' ';
  for (i=0; i<p; i++) {
    x[i] = ' ';
    y[i] = ' ';
  }
  if(Iam == root) {
    a = 'b';
    x[0] = a;
  }
  MPI_Bcast(&a,1,MPI_CHAR,          /* buf,count,type */
            root,MPI_COMM_WORLD);  /* root,comm,flag */

  printf(" MPI_Bcast     :  %d ", Iam);
  for (i=0; i<p; i++) {
    printf("  %c",x[i]);
  }
  printf("     ");
  printf("  %c",a);
  printf("\n");

  MPI_Barrier(MPI_COMM_WORLD);

  MPI_Finalize();                     /* let MPI finish up ... */

}
