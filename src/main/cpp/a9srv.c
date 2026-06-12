#include <stdio.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <assert.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

/* Socket Protocol
 *
 * <id>=<value> Sets a value
 * <id> Gets a value
 * <id>! Toggles a value
 */

#define COUNT( array ) ( sizeof( array )/sizeof( array[ 0 ] ) )
#define REDUCE( type,var,src,n ) type var = 0; memcpy( &var,src,( n )<sizeof( type ) ? ( n ) : sizeof( type ) )
#define ASSERT0( val ) assert( ( val )!= -1 )

#define SOCKET_NAME "\0testsocket"
#define BUFFER_SIZE 512
#define DISP_BASE "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/"

#define DISP_MODE ( DISP_BASE "epd_display_mode" )
#define DISP_CLEAR ( DISP_BASE "epd_force_clear" )
#define LED_BASE "/sys/class/backlight/aw99703-bl-"
#define LED_BRIGHTNESS "/brightness"

#define LED_PATH( yellow ) ( yellow ? ( LED_BASE "1" LED_BRIGHTNESS ) : ( LED_BASE "2" LED_BRIGHTNESS ) )

const short epd_mode_ids[ ]= { 515,513,518,521 };
 
void write_file( const char* const f,const char* const s,size_t l ) {
 int fd;
 ASSERT0( ( fd = open( f,O_WRONLY ) ) );
 ASSERT0( write( fd,s,l ) );
 close( fd );
}

void read_file( const char* const f,char* const s,size_t l ) {
 int fd;
 ASSERT0( ( fd = open( f,O_RDONLY ) ) );
 ASSERT0( read( fd,s,l ) );
 close( fd );
}

void epd_write( char mode ) {
 short code = epd_mode_ids[ mode % COUNT( epd_mode_ids ) ];

 char s[4];
 snprintf( s,sizeof( s ),"%hi\n",code );
 write_file( DISP_MODE,s,sizeof( s ) );
}

char epd_read( ) {
 char s[4];
 
 read_file( DISP_MODE,s,sizeof( s ) );
 s[ 3 ]= '\0';
 long l = strtol( s,NULL,0 );
 
 for( char i = 0; i<COUNT( epd_mode_ids ); i++ )
  if( l == epd_mode_ids[ i ] )
   return i;
 
 return 0;
}

void epd_clear( ) {
 write_file( DISP_CLEAR,"1",1 );
}

void led_write( bool yellow,char val ) {
 char s[4];
 write_file( LED_PATH( yellow ),s,snprintf( s,sizeof( s ),"%hi\n",val ) );
} 

char led_read( bool yellow ) {
 char s[4];
 
 read_file( LED_PATH( yellow ),s,sizeof( s ) );
 long l = strtol( s,NULL,0 );
 
 char result = l;
 
 return result;
}

int main( void ) {
 int sock;
 char buffer[ BUFFER_SIZE ];
 
 struct sockaddr_un server_addr = { AF_UNIX,SOCKET_NAME };

 ASSERT0( sock = socket( AF_UNIX,SOCK_SEQPACKET,0 ) );
 ASSERT0( bind( sock,
         (struct sockaddr*)&server_addr,
         offsetof( struct sockaddr_un,sun_path )+sizeof( SOCKET_NAME )-1 ) );
 ASSERT0( listen( sock,8 ) );
 
 while( true ) {
  int cmdstream = accept( sock,NULL,NULL );
  
  size_t i;
  while( ( i = read( cmdstream,buffer,BUFFER_SIZE-1 ) )> 0 ) {
   buffer[ i ]= '\0';
   char cmd = buffer[ 0 ];
   char* args = i>1 ? buffer + 1 : NULL;
   
   if( cmd == 'm' )
    if( args ) {
     REDUCE( char,val,args,i - 1 );
     epd_write( val );
    } else {
     char val = epd_read( );
     ASSERT0( write( cmdstream,&val,sizeof( epd_read ) ) );
    }
   else if( cmd == 'c' )
    epd_clear( );
   else {
    bool white = cmd == 'w';
    if( white || cmd == 'y' )
     if( args ) {
      REDUCE( char,val,args,i - 1 );
      led_write( white,val );
     } else {
      char val = led_read( white );
      ASSERT0( write( cmdstream,&val,sizeof( led_read ) ) );
     }
    else
     fprintf( stderr,"Did not understand '%c'\n",cmd ); 
   }
  }
  
  close( cmdstream );
 }
 
 return 0;
}
