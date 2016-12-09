#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/shm.h>
#include <pthread.h>
#include <time.h>

#define MYPORT  11000
#define BUFFER_SIZE 1024

int main()
{
    ///
    int sock_cli = socket(AF_INET,SOCK_STREAM, 0);

    ///
    struct sockaddr_in servaddr;
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(MYPORT);  ///servaddr.sin_addr.s_addr = inet_addr("192.168.1.112");  ///

        if (connect(sock_cli, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("connect");
        exit(1);
    }

    char sendbuf[BUFFER_SIZE];
    char recvbuf[BUFFER_SIZE];
    /*while (fgets(sendbuf, sizeof(sendbuf), stdin) != NULL)
    {
        send(sock_cli, sendbuf, strlen(sendbuf),0); ///      if(strcmp(sendbuf,"exit\n")==0)
            break;
        recv(sock_cli, recvbuf, sizeof(recvbuf),0); ///
        fputs(recvbuf, stdout);

        memset(sendbuf, 0, sizeof(sendbuf));
        memset(recvbuf, 0, sizeof(recvbuf));
    }*/
    pid_t p1,p2;
    p1=fork();
    int x=1;
    if(p1==0)
    {
    //fgets(sendbuf, sizeof(sendbuf), stdin) != NULL
      while (1)
     {
      srand(time(0));
      int a=rand()%300+300;
      a=a*100000;
      int b=rand()%1+487;
      b=b*100;
      a=a+b+1;
      
      //itoa(a,sendbuf,10);
      
      sprintf(sendbuf,"%d",a);
      strcat(sendbuf,"\n");
      //printf("123\n");
      send(sock_cli, sendbuf, strlen(sendbuf),0);
      memset(sendbuf, 0, sizeof(sendbuf));
      sleep(10);
     }
    }
    p2=fork();
    if(p2==0)
    {
      while(1)
      {
      memset(recvbuf, 0, sizeof(recvbuf));
      recv(sock_cli, recvbuf, sizeof(recvbuf),0);
      //printf("%s",recvbuf);
      int y=(int)recvbuf[0];
      y=y-48;
      //printf("%d",y);
      if(y==1){
        //printf("recive command:Startup!\n");
        execl("./led","4","1",NULL);
        }
      else{
        //printf("recive command:Shutdown!\n");
        execl("./led","4","0",NULL);
        }      
      //fputs(recvbuf, stdout);
      memset(recvbuf, 0, sizeof(recvbuf));
      }
    }
    waitpid(p1,NULL,0);
    waitpid(p2,NULL,0);
    close(sock_cli);
    return 0;
}







