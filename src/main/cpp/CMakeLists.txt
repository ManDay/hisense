cmake_minimum_required( VERSION 3.20 )
project( A9Server C )

add_executable( a9srv )

target_sources( a9srv PRIVATE a9srv.c )
