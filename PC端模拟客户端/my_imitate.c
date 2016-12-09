#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

void *show_on()
{
	system("/home/kurt/ws/close.sh");
	system("/home/kurt/ws/turn_on.sh");
}

void *show_off()
{
	system("/home/kurt/ws/close.sh");
	system("/home/kurt/ws/turn_off.sh");
}
int main()
{
	pthread_t on;
	pthread_t off;
	int client_sockfd, status;
	struct sockaddr_in remote_addr;
	char recvbuf[2]; 
	char sendbuf[10];
	memset(&remote_addr,0,sizeof(remote_addr)); 
	remote_addr.sin_family=AF_INET;
	remote_addr.sin_addr.s_addr=inet_addr("192.168.1.108");
	remote_addr.sin_port=htons(10687); 
	
	if((client_sockfd=socket(AF_INET,SOCK_STREAM,0))<0)
	{
		perror("socket");
		return 1;
	}
	int connected = -1;
	do
	{
		connected = connect(client_sockfd,(struct sockaddr *)&remote_addr,sizeof(struct sockaddr));
		sleep(2);
	}while(connected<0);
	printf("connected to server/n");
	pid_t child;
	if((child = fork()) == -1)
	{
		perror("fork error");
		exit(EXIT_FAILURE);
	}
	else if(child == 0)//接收端
	{
		while(1)
		{
			recv(client_sockfd,recvbuf,strlen(recvbuf),0);
			if(recvbuf[0]=='1')
			{
				printf("服务器发送开机请求");
				pthread_create(&on,NULL,show_on,NULL);
				status= 1;
			}
			else if(recvbuf[0]=='2')
			{
				printf("服务器发送关闭请求");
				pthread_create(&off,NULL,show_off,NULL);
				status =2;
			}
			memset(recvbuf, 0, sizeof(recvbuf));		
		}
	}
	else	//发送端
	{
		status = 0;
		while(1)
		{
			if(status==1)
			{
				stpcpy(sendbuf,"220011001\0");
				send(client_sockfd,sendbuf,strlen(sendbuf),0);
				sleep(60);
				memset(sendbuf, 0, sizeof(sendbuf));
			}
			else if(status==2)
			{
				stpcpy(sendbuf,"000000000\0");
				send(client_sockfd,sendbuf,strlen(sendbuf),0);
				sleep(60);
				memset(sendbuf, 0, sizeof(sendbuf));
			}
		}
	}
	close(client_sockfd);
    return 0;
}


