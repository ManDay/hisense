#include <stdio.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <assert.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

#define SOCKET_NAME "\0a9srv"

#define DISP_BASE "/sys/devices/platform/soc/soc:qcom,dsi-display-primary/"
#define DISP_MODE "epd_display_mode"
#define DISP_CLEAR "epd_force_clear"
#define DISP_CONT "epd_contrast"

#define LED_BASE_ALL "/sys/class/backlight/aw99703-bl-"
#define LED_BRIGHTNESS "brightness"
#define LED_PATH( yellow ) ( yellow ? ( LED_BASE_ALL "1/" LED_BRIGHTNESS ) : ( LED_BASE_ALL "2/" LED_BRIGHTNESS ) )

#define BAT_BASE "/sys/class/power_supply/battery/"
#define BAT_CTRL "charge_control_limit"
#define BAT_STAT "status"

const unsigned short epd_mode_ids[ ]= { 515,513,518,521 };

#define COUNT( array ) ( sizeof( array )/sizeof( array[ 0 ] ) )
#define GETFILE( TARGET,VARNAME,SIZE ) char VARNAME[ SIZE ]; read_file( TARGET,VARNAME,SIZE ); VARNAME[ SIZE - 1 ]= '\0'
#define MIN( a,b ) ( (a) < (b) ? (a) : (b) )
#define RETBUF( TARGET,N,TYPE,RESULT ) { TYPE result = RESULT; memcpy( TARGET,&result,MIN( N,sizeof( TYPE ) ) ); return MIN( N,sizeof( TYPE ) ); }
#define RETVAL( TARGET,N,TYPE,SRC ) TYPE TARGET = 0; memcpy( &TARGET,SRC,MIN( sizeof( TYPE ),N ) );
#define ASSERT0( val ) assert( ( val )!= -1 )
 
bool write_file( const char* const f,const char* const s,size_t l ) {
 int fd;
 ASSERT0( ( fd = open( f,O_WRONLY | O_TRUNC ) ) );
 ASSERT0( write( fd,s,l ) );
 close( fd );
 return true;
}

void read_file( const char* const f,char* const s,size_t l ) {
 int fd;
 ASSERT0( ( fd = open( f,O_RDONLY ) ) );
 ASSERT0( read( fd,s,l ) );
 close( fd );
}

size_t led_read( bool yellow,void* b,size_t n ) {
 GETFILE( LED_PATH( yellow ),s,5 ); 
 RETBUF( b,n,unsigned short,(unsigned short)(strtol( s,NULL,0 )) )
}

size_t cont_read( void* b,size_t n ) {
 GETFILE( DISP_BASE DISP_CONT,s,5 ); 
 RETBUF( b,n,unsigned char,(unsigned char)(strtol( s,NULL,0 )) )
}

size_t epd_read( void* b,size_t n ) {
 GETFILE( DISP_BASE DISP_MODE,s,4 );
 
 long l = strtol( s,NULL,0 );
 size_t i;
 for( i = 0; i < COUNT( epd_mode_ids )- 1; i++ )
  if( l == epd_mode_ids[ i ] )
   break;
 
 RETBUF( b,n,unsigned char,(unsigned char)(i) )
}

size_t bat_read( void* b,size_t n ) {
 GETFILE( BAT_BASE BAT_STAT,s,9 );
 RETBUF( b,n,bool,strncmp( s,"Charging",8 )== 0 ) 
}

size_t epd_clear( void* b,size_t n ) {
 write_file( DISP_BASE DISP_CLEAR,"1\n",2 );
 return 0;
}

bool epd_write( void* b,size_t n ) {
 RETVAL( mode,n,size_t,b );
 
 unsigned short code = epd_mode_ids[ mode % COUNT( epd_mode_ids ) ];
 
 char s[ BUFSIZ ];
 return write_file( DISP_BASE DISP_MODE,s,snprintf( s,sizeof( s ),"%hu\n",code ) );
}

bool led_write( bool yellow,void* b,size_t n ) {
 RETVAL( val,n,unsigned short,b );
 char s[ BUFSIZ ];
 return write_file( LED_PATH( yellow ),s,snprintf( s,sizeof( s ),"%hu\n",val ) );
}

bool cont_write( void* b,size_t n ) {
 RETVAL( val,n,unsigned char,b );
 char s[ BUFSIZ ];
 return write_file( DISP_BASE DISP_CONT,s,snprintf( s,sizeof( s ),"%hhu\n",val ) );
}

bool bat_write( void* b,size_t n) {
 RETVAL( charge,n,bool,b )
 
 if( charge )
  return write_file( BAT_BASE BAT_CTRL,"0\n",2 );
 else
  return write_file( BAT_BASE BAT_CTRL,"10\n",3 );
}

struct Action {
 char id;
 bool (*writer)( void*,size_t );
 size_t (*reader)( void*,size_t );
};

// A kingdom for currying or even just lambdas...
size_t white_read( void* b,size_t n ) { return led_read( false,b,n ); }
size_t yellow_read( void* b,size_t n ) { return led_read( true,b,n ); }
bool white_write( void* b,size_t n ) { return led_write( false,b,n ); }
bool yellow_write( void* b,size_t n ) { return led_write( true,b,n ); }

const struct Action action_map[ ] = {
 { 'm',epd_write,epd_read },
 { 'r',NULL,epd_clear },
 { 'c',cont_write,cont_read },
 { 'b',bat_write,bat_read },
 { 'w',white_write,white_read },
 { 'y',yellow_write,yellow_read }
};

size_t get_action( char id ) {
 size_t i;
 for( i = COUNT( action_map ); i > 0; i-- )
  if( action_map[ i - 1 ].id == id )
   break;
 return i;
}

int main( void ) {
 int sock;
 char buffer[ BUFSIZ ];
 
 struct sockaddr_un server_addr = { AF_UNIX,SOCKET_NAME };

 ASSERT0( sock = socket( AF_UNIX,SOCK_SEQPACKET,0 ) );
 ASSERT0( bind( sock,
         (struct sockaddr*)&server_addr,
         offsetof( struct sockaddr_un,sun_path )+sizeof( SOCKET_NAME )-1 ) );
 ASSERT0( listen( sock,8 ) );
 
 while( true ) {
  int cmdstream = accept( sock,NULL,NULL );
  
  size_t i;
  while( ( i = read( cmdstream,buffer,BUFSIZ-1 ) )> 0 ) {
   size_t action_idx = get_action( buffer[ 0 ] );
   
   if( action_idx ) {
    struct Action a = action_map[ action_idx - 1 ];
    
 /*
    fprintf( stderr,"Action '%c' with arguments '",a.id );
    for( int j = 1; j<i; j++ )
     fprintf( stderr,"\\%02hhx",buffer[ j ] );
    fprintf( stderr,"'\n" );
*/

    if( i > 1 && a.writer )
     a.writer( buffer + 1,i - 1 );
    else {
     char result[ BUFSIZ ];
     size_t n = a.reader( result,BUFSIZ );
     write( cmdstream,result,n ); 
    };
   } else
    fprintf( stderr,"A9Srv did not understand '%c'\n",buffer[ 0 ] );
  }
  
  close( cmdstream );
 }
 
 return 0;
}
