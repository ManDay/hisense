#include <stdio.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <assert.h>
#include <fcntl.h>
#include <unistd.h>

#define SOCKET_NAME "\0testsocket"
#define BUFFER_SIZE 512

void write_file( const char* const f,const char* const s,size_t l ) {
 int fd;
 assert( ( fd = open( f,O_WRONLY ) )!= -1 );
 assert( write( fd,s,l )!= -1 );
 close( fd );
}

void epd_write( unsigned code ) {
 char s[3];
 sprintf( s,"%u",code );
 write_file( "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/epd_display_mode",
             s,
             sizeof( s ) );
}

int main( void ) {
 int sock;
 char buffer[ BUFFER_SIZE ];
 
 struct sockaddr_un server_addr = { AF_UNIX,SOCKET_NAME };

 assert( ( sock = socket( AF_UNIX,SOCK_DGRAM,0 ) )>= 0 );
 assert( bind( sock,
         (struct sockaddr*)&server_addr,
         offsetof( struct sockaddr_un,sun_path )+sizeof( SOCKET_NAME )-1 )>= 0 );
 
 while (1) {
  ssize_t i = recv( sock,buffer,BUFFER_SIZE-1,0 );
  
  if( i>0 ) {
   buffer[ i ]= '\0';
   char cmd = buffer[ 0 ];
   
   char* args = buffer + 1;

   if( cmd == 'c' )
    epd_write( 515 );
   else if( cmd == 'b' )
    epd_write( 513 );
   else if( cmd == 's' )
    epd_write( 518 );
   else if( cmd == 'p' )
    epd_write( 521 );
  }
 }

 return 0;
}
